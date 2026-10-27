package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchCallback;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchFirebaseDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerListCallback;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PresenceFirebaseDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

// Không chấm bài ở app: mọi kết quả lấy từ server
public class MatchActivity extends AppCompatActivity
        implements View.OnClickListener, MatchCallback, PlayerListCallback {
    public static final String EXTRA_MATCH_ID = "matchId";
    private static final long MATCH_MILLIS = 60_000;

    private TextView txtTimer;
    private TextView txtOpponentStatus;
    private RecyclerView rvQuestions;
    private Button btnSubmit;
    private Button btnExit;
    private LinearLayout layoutResult;
    private TextView txtResult;
    private TextView txtMyResult;
    private TextView txtOpponentResult;
    private TextView txtPoints;
    private TextView txtContinue;
    private Button btnContinue;
    private Button btnStop;
    private Player me;
    private int matchId;
    private int opponentId;
    private CountDownTimer timer;
    private QuestionAdapter questionAdapter;
    private MatchFirebaseDAO matchFirebaseDAO = new MatchFirebaseDAO();
    private PresenceFirebaseDAO presenceFirebaseDAO = new PresenceFirebaseDAO();
    private MatchApiDAO matchApiDAO = new MatchApiDAO();
    private boolean started;
    private boolean submitted;
    private boolean resultLoading;
    private boolean resultShown;
    private boolean opponentLeft;
    private boolean leaving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        bindViews();
        me = new SessionDAO(this).getPlayer();
        matchId = getIntent().getIntExtra(EXTRA_MATCH_ID, 0);
        matchFirebaseDAO.watchMatch(matchId, this);
    }

    private void bindViews() {
        txtTimer = findViewById(R.id.txtTimer);
        txtOpponentStatus = findViewById(R.id.txtOpponentStatus);
        rvQuestions = findViewById(R.id.rvQuestions);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnExit = findViewById(R.id.btnExit);
        bindResultViews();
        questionAdapter = new QuestionAdapter();
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        rvQuestions.setAdapter(questionAdapter);
        for (Button b : new Button[]{btnSubmit, btnExit, btnContinue, btnStop}) {
            b.setOnClickListener(this);
        }
    }

    private void bindResultViews() {
        layoutResult = findViewById(R.id.layoutResult);
        txtResult = findViewById(R.id.txtResult);
        txtMyResult = findViewById(R.id.txtMyResult);
        txtOpponentResult = findViewById(R.id.txtOpponentResult);
        txtPoints = findViewById(R.id.txtPoints);
        txtContinue = findViewById(R.id.txtContinue);
        btnContinue = findViewById(R.id.btnContinue);
        btnStop = findViewById(R.id.btnStop);
    }

    @Override
    public void onClick(View v) {
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

    @Override
    public void onStopped(int playerId) {
        if (playerId == me.getId() || leaving) {
            return;
        }
        if (resultShown) {
            leaving = true;
            showToast(getString(R.string.msg_opponent_stopped));
            finish();
        } else if (!opponentLeft) {
            opponentLeft = true;
            showToast(getString(R.string.msg_opponent_left));
            loadResult();
        }
    }

    @Override
    public void onFail(String message) {
        showToast(message);
    }

    // Đối thủ rời danh sách online giữa trận: coi như bỏ trận
    @Override
    public void onPlayersChanged(List<Player> players) {
        if (!started || resultShown || opponentLeft || leaving) {
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
        questionAdapter.setQuestions(match.getQuestions());
        timer = createTimer();
        timer.start();
        presenceFirebaseDAO.watchOnlinePlayers(this);
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
        questionAdapter.setLocked(true);
        final List<Integer> answers = questionAdapter.getAnswers();
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
        btnSubmit.setVisibility(View.GONE);
        txtTimer.setText(R.string.msg_submitted);
    }

    private void onSubmitFailed(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                submitted = false;
                btnSubmit.setEnabled(true);
                questionAdapter.setLocked(false);
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
        layoutResult.setVisibility(View.VISIBLE);
        showResultTitle(match.getResult(me.getId()));
        showScores(match);
        MatchPlayer opponent = opponentOf(match);
        boolean forfeited = opponent.isForfeited() || mineOf(match).isForfeited();
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
        txtMyResult.setText(getString(R.string.label_my_result, mine.getCorrectCount(),
                formatTime(match.getFinishTime(me.getId()))));
        txtOpponentResult.setText(getString(R.string.label_opponent_result, opponent.getPlayer().getDisplayName(),
                opponent.getCorrectCount(), formatTime(match.getFinishTime(opponent.getPlayer().getId()))));
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
        return getString(R.string.label_seconds, PointFormat.format(Math.round(millis / 100.0) / 10.0));
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
