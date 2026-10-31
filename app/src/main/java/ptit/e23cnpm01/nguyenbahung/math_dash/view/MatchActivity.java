package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchCallback;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchFirebaseDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerListCallback;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PresenceFirebaseDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Answer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

// Không chấm bài ở app: mọi kết quả lấy từ server
public class MatchActivity extends BaseActivity implements MatchCallback, PlayerListCallback {
    public static final String EXTRA_MATCH_ID = "matchId";
    private static final long MATCH_MILLIS = 60_000;
    // Khóa 4 ô một lúc sau khi chọn để bấm đúp không trả lời nhầm câu sau
    private static final long NEXT_DELAY = 200;
    private static final String[] LETTERS = {"A", "B", "C", "D"};

    private TextView txtTimer;
    private TextView txtOpponentStatus;
    private TextView txtProgress;
    private TextView txtExpression;
    private LinearLayout layoutOptions;
    private Button[] btnOptions = new Button[4];
    private Button btnSubmit;
    private Button btnExit;
    private LinearLayout layoutResult;
    private TextView txtResult;
    private TextView txtOpponentName;
    private TextView txtMyCorrect;
    private TextView txtOpponentCorrect;
    private TextView txtMyTime;
    private TextView txtOpponentTime;
    private TextView txtPoints;
    private TextView txtContinue;
    private Button btnContinue;
    private Button btnStop;
    private int matchId;
    private int opponentId;
    private List<MathProblem> questions = new ArrayList<>();
    private List<Answer> answers = new ArrayList<>();
    // Chỉ số câu đang hiện và thời điểm câu đó hiện ra
    private int current;
    private long shownAt;
    private CountDownTimer timer;
    private MatchFirebaseDAO matchFirebaseDAO = new MatchFirebaseDAO();
    private PresenceFirebaseDAO presenceFirebaseDAO = new PresenceFirebaseDAO();
    private MatchApiDAO matchApiDAO = new MatchApiDAO();
    private boolean started;
    private boolean submitted;
    private boolean resultLoading;
    private boolean resultShown;
    // Firebase báo đối thủ có stoppedBy trước khi mình có kết quả; đối thủ bị xử bỏ trận
    private boolean opponentStopped;
    private boolean opponentForfeited;
    private boolean leaving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        bindViews();
        matchId = getIntent().getIntExtra(EXTRA_MATCH_ID, 0);
        matchFirebaseDAO.watchMatch(matchId, this);
    }

    private void bindViews() {
        txtTimer = findViewById(R.id.txtTimer);
        txtOpponentStatus = findViewById(R.id.txtOpponentStatus);
        txtProgress = findViewById(R.id.txtProgress);
        txtExpression = findViewById(R.id.txtExpression);
        layoutOptions = findViewById(R.id.layoutOptions);
        btnOptions[0] = findViewById(R.id.btnOptionA);
        btnOptions[1] = findViewById(R.id.btnOptionB);
        btnOptions[2] = findViewById(R.id.btnOptionC);
        btnOptions[3] = findViewById(R.id.btnOptionD);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnExit = findViewById(R.id.btnExit);
        bindResultViews();
        for (Button b : btnOptions) {
            b.setOnClickListener(this);
        }
        for (Button b : new Button[]{btnSubmit, btnExit, btnContinue, btnStop}) {
            b.setOnClickListener(this);
        }
    }

    private void bindResultViews() {
        layoutResult = findViewById(R.id.layoutResult);
        txtResult = findViewById(R.id.txtResult);
        txtOpponentName = findViewById(R.id.txtOpponentName);
        txtMyCorrect = findViewById(R.id.txtMyCorrect);
        txtOpponentCorrect = findViewById(R.id.txtOpponentCorrect);
        txtMyTime = findViewById(R.id.txtMyTime);
        txtOpponentTime = findViewById(R.id.txtOpponentTime);
        txtPoints = findViewById(R.id.txtPoints);
        txtContinue = findViewById(R.id.txtContinue);
        btnContinue = findViewById(R.id.btnContinue);
        btnStop = findViewById(R.id.btnStop);
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < btnOptions.length; i++) {
            if (v == btnOptions[i]) {
                chooseOption(i);
                return;
            }
        }
        if (v == btnSubmit) {
            submitAnswers();
        } else if (v == btnExit) {
            confirmLeave();
        } else if (v == btnContinue) {
            sendChoice(true);
        } else if (v == btnStop) {
            sendChoice(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (resultShown) {
            sendChoice(false);
        } else {
            confirmLeave();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        matchFirebaseDAO.stopWatching(matchId);
        presenceFirebaseDAO.stopWatching();
    }

    @Override
    public void onSuccess(Match match) {
        if (!started && !match.getQuestions().isEmpty()) {
            startMatch(match);
        }
        showOpponentStatus(match);
        if (Match.FINISHED.equals(match.getStatus())) {
            loadResult();
        }
    }

    @Override
    public void onNextMatch(int nextMatchId) {
        if (!leaving) {
            leaving = true;
            openNextMatch(nextMatchId);
        }
    }

    // stoppedBy là đối thủ: hoặc bỏ trận giữa trận, hoặc chọn Dừng sau khi có kết quả.
    // Server ghi stoppedBy và status là 2 lần ghi, nên hàm này có thể được gọi 2 lần
    @Override
    public void onStopped(int playerId) {
        if (playerId == me.getId() || leaving) {
            return;
        }
        if (!resultShown) {
            // Chưa có kết quả thì chưa biết là bỏ trận hay Dừng: tải kết quả rồi showResult() phân biệt
            opponentStopped = true;
            loadResult();
        } else if (!opponentForfeited) {
            leaveAfterStop();
        }
    }

    private void leaveAfterStop() {
        leaving = true;
        showToast(getString(R.string.msg_opponent_stopped));
        finish();
    }

    @Override
    public void onFail(String message) {
        showToast(message);
    }

    // Đối thủ rời danh sách online giữa trận: coi như bỏ trận
    @Override
    public void onPlayersChanged(List<Player> players) {
        if (!started || resultShown || opponentStopped || leaving) {
            return;
        }
        for (Player p : players) {
            if (p.getId() == opponentId) {
                return;
            }
        }
        forfeit(opponentId);
    }

    private void startMatch(Match match) {
        started = true;
        opponentId = opponentOf(match).getPlayer().getId();
        questions = match.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            answers.add(new Answer());
        }
        showQuestion();
        timer = createTimer();
        timer.start();
        presenceFirebaseDAO.watchOnlinePlayers(this);
    }

    // Hiện câu thứ current; làm xong cả 10 câu thì hiện nút Nộp bài
    private void showQuestion() {
        if (submitted) {
            return;
        }
        if (current >= questions.size()) {
            showMessage(getString(R.string.msg_all_answered, questions.size()));
            btnSubmit.setVisibility(View.VISIBLE);
            return;
        }
        MathProblem problem = questions.get(current);
        txtProgress.setText(getString(R.string.label_question, current + 1, questions.size()));
        txtExpression.setText(problem.getExpression());
        for (int i = 0; i < btnOptions.length; i++) {
            btnOptions[i].setText(optionText(i, problem.getAnswerOptions().get(i)));
            btnOptions[i].setEnabled(true);
            btnOptions[i].setAlpha(1f);
        }
        shownAt = SystemClock.elapsedRealtime();
    }

    // Ghi phương án đã chọn và thời gian làm câu, làm mờ 3 ô còn lại rồi sang câu sau
    private void chooseOption(int index) {
        if (submitted || current >= questions.size()) {
            return;
        }
        Answer answer = answers.get(current);
        answer.setSelectedOption(questions.get(current).getAnswerOptions().get(index));
        answer.setAnswerTime(SystemClock.elapsedRealtime() - shownAt);
        for (int i = 0; i < btnOptions.length; i++) {
            btnOptions[i].setEnabled(false);
            btnOptions[i].setAlpha(i == index ? 1f : 0.3f);
        }
        current++;
        layoutOptions.postDelayed(new Runnable() {
            @Override
            public void run() {
                showQuestion();
            }
        }, NEXT_DELAY);
    }

    // "A" nhỏ ở trên, giá trị lớn ở dưới
    private CharSequence optionText(int index, int value) {
        SpannableString text = new SpannableString(LETTERS[index] + "\n" + value);
        text.setSpan(new RelativeSizeSpan(0.5f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }

    // Thay câu hỏi bằng một dòng thông báo chữ đậm; 4 ô ẩn nhưng vẫn giữ chỗ
    private void showMessage(String message) {
        layoutOptions.setVisibility(View.INVISIBLE);
        txtExpression.setVisibility(View.GONE);
        txtProgress.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        txtProgress.setText(message);
    }

    private CountDownTimer createTimer() {
        return new CountDownTimer(MATCH_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                MatchActivity.this.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                MatchActivity.this.onFinish();
            }
        };
    }

    public void onTick(long millisLeft) {
        txtTimer.setText(String.format(Locale.US, "00:%02d", millisLeft / 1000));
    }

    public void onFinish() {
        txtTimer.setText(R.string.time_zero);
        if (!submitted) {
            showMessage(getString(R.string.msg_time_up));
        }
        submitAnswers();
    }

    private void showOpponentStatus(Match match) {
        MatchPlayer opponent = opponentOf(match);
        int text = opponent.getSubmittedAt() != null ? R.string.status_opponent_submitted
                : R.string.status_opponent_working;
        txtOpponentStatus.setText(getString(text, opponent.getPlayer().getDisplayName()));
    }

    private void submitAnswers() {
        if (submitted || !started) {
            return;
        }
        submitted = true;
        btnSubmit.setEnabled(false);
        showMessage(getString(R.string.msg_submitting));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    matchApiDAO.submit(matchId, me.getId(), answers);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onSubmitted();
                        }
                    });
                } catch (IOException | JSONException e) {
                    onSubmitFailed(e.getMessage());
                }
            }
        }).start();
    }

    private void onSubmitted() {
        if (timer != null) {
            timer.cancel();
        }
        btnSubmit.setVisibility(View.INVISIBLE);
        showMessage(getString(R.string.msg_submitted));
    }

    private void onSubmitFailed(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                submitted = false;
                btnSubmit.setEnabled(true);
                btnSubmit.setVisibility(View.VISIBLE);
                showToast(message);
            }
        });
    }

    private void confirmLeave() {
        if (leaving) {
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage(R.string.msg_confirm_leave)
                .setNegativeButton(R.string.btn_stay, null)
                .setPositiveButton(R.string.btn_leave, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveMatch();
                    }
                })
                .show();
    }

    private void leaveMatch() {
        leaving = true;
        forfeit(me.getId());
        finish();
    }

    private void forfeit(final int playerId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    matchApiDAO.forfeit(matchId, playerId);
                } catch (IOException | JSONException e) {
                    showToast(e.getMessage());
                }
            }
        }).start();
    }

    private void loadResult() {
        if (resultLoading) {
            return;
        }
        resultLoading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Match match = matchApiDAO.getById(matchId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showResult(match);
                        }
                    });
                } catch (IOException | JSONException e) {
                    resultLoading = false;
                    showToast(e.getMessage());
                }
            }
        }).start();
    }

    private void showResult(Match match) {
        resultShown = true;
        if (timer != null) {
            timer.cancel();
        }
        MatchPlayer opponent = opponentOf(match);
        opponentForfeited = opponent.isForfeited();
        boolean forfeited = opponentForfeited || mineOf(match).isForfeited();
        if (opponentStopped && !forfeited) {
            // Đối thủ đã chọn Dừng: cả 2 về màn Chơi online
            leaveAfterStop();
            return;
        }
        if (opponentForfeited) {
            showToast(getString(R.string.msg_opponent_left));
        }
        layoutResult.setVisibility(View.VISIBLE);
        showResultTitle(match.getResult(me.getId()));
        showScores(match);
        txtContinue.setVisibility(forfeited ? View.GONE : View.VISIBLE);
        btnContinue.setVisibility(forfeited ? View.GONE : View.VISIBLE);
        btnStop.setText(forfeited ? R.string.btn_back_to_menu : R.string.btn_stop);
    }

    private void showResultTitle(String result) {
        int text = Match.WIN.equals(result) ? R.string.result_win
                : Match.LOSE.equals(result) ? R.string.result_lose : R.string.result_draw;
        int color = Match.WIN.equals(result) ? R.color.arcade_green
                : Match.LOSE.equals(result) ? R.color.arcade_red : R.color.arcade_amber;
        txtResult.setText(text);
        txtResult.setTextColor(getColor(color));
    }

    private void showScores(Match match) {
        MatchPlayer mine = mineOf(match);
        MatchPlayer opponent = opponentOf(match);
        txtOpponentName.setText(opponent.getPlayer().getDisplayName());
        txtMyCorrect.setText(getString(R.string.label_correct_count, mine.getCorrectCount()));
        txtOpponentCorrect.setText(getString(R.string.label_correct_count, opponent.getCorrectCount()));
        txtMyTime.setText(formatTime(match.getFinishTime(me.getId())));
        txtOpponentTime.setText(formatTime(match.getFinishTime(opponent.getPlayer().getId())));
        txtPoints.setText(getString(R.string.label_match_points, PointFormat.format(match.getPoints(me.getId())),
                PointFormat.format(mine.getPlayer().getTotalPoints())));
    }

    private void sendChoice(final boolean choice) {
        btnContinue.setEnabled(false);
        btnStop.setEnabled(false);
        if (choice) {
            txtContinue.setText(R.string.msg_waiting_opponent);
        } else {
            leaving = true;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    matchApiDAO.continueMatch(matchId, me.getId(), choice);
                } catch (IOException | JSONException e) {
                    showToast(e.getMessage());
                }
            }
        }).start();
        if (!choice) {
            finish();
        }
    }

    private void openNextMatch(int nextMatchId) {
        matchFirebaseDAO.stopWatching(matchId);
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra(EXTRA_MATCH_ID, nextMatchId);
        startActivity(intent);
        finish();
    }

    private MatchPlayer mineOf(Match match) {
        for (MatchPlayer mp : match.getPlayers()) {
            if (mp.getPlayer().getId() == me.getId()) {
                return mp;
            }
        }
        return match.getPlayers().get(0);
    }

    private MatchPlayer opponentOf(Match match) {
        for (MatchPlayer mp : match.getPlayers()) {
            if (mp.getPlayer().getId() != me.getId()) {
                return mp;
            }
        }
        return match.getPlayers().get(0);
    }

    // 38200 ms → "38,2 giây"; chưa nộp → "—"
    private String formatTime(long millis) {
        if (millis == Long.MAX_VALUE) {
            return getString(R.string.label_no_time);
        }
        return getString(R.string.label_seconds, PointFormat.formatSeconds(millis / 1000.0));
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MatchActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
