package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONException;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchCallback;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchFirebaseDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.ProblemDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.GameSession;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Màn Thi đấu online. Chấm điểm bằng MathProblem + GameSession, đọc / ghi trận qua MatchFirebaseDAO;
 * thắng thua do server quyết định (MatchApiDAO.finishMatch), Activity chỉ hiển thị bản trận server trả về.
 */
public class OnlineGameActivity extends AppCompatActivity implements View.OnClickListener, MatchCallback {
    private static final long GAME_MILLIS = 60000;
    private static final int ONLINE_DIFFICULTY = 1;
    private static final int MAX_DIGITS = 3;
    private static final String EMPTY_ANSWER = "?";

    private TextView txtMyScore;
    private TextView txtOpponentScore;
    private TextView txtScorePopup;
    private TextView txtTimer;
    private TextView txtProblem;
    private TextView txtAnswer;
    private Button[] btnDigits = new Button[10];
    private ImageButton btnBackspace;
    private Button btnSubmit;
    private LinearLayout layoutResult;
    private TextView txtResult;
    private TextView txtFinalScore;
    private Button btnBackToMenu;

    private MatchFirebaseDAO matchFirebaseDAO = new MatchFirebaseDAO();
    private MatchApiDAO matchApiDAO = new MatchApiDAO();
    private ProblemDAO problemDAO;
    private GameSession session;
    private CountDownTimer timer;
    private String roomCode;
    private Player me;
    private boolean finishing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game);
        initViews();
        me = new SessionDAO(this).getPlayer();
        roomCode = getIntent().getStringExtra("roomCode");
        matchFirebaseDAO.watchMatch(roomCode, this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            submitAnswer(Integer.parseInt(txtAnswer.getText().toString()));
        } else if (v == btnBackspace) {
            deleteDigit();
        } else if (v == btnBackToMenu) {
            finish();
        } else {
            for (int digit = 0; digit < btnDigits.length; digit++) {
                if (v == btnDigits[digit]) {
                    appendDigit(digit);
                }
            }
        }
    }

    // Nhấn Back khi đang đấu: hỏi xác nhận, thoát thì bị xử thua
    @Override
    public void onBackPressed() {
        if (layoutResult.getVisibility() == View.VISIBLE) {
            finish();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("Thoát sẽ bị xử thua?")
                .setNegativeButton("Ở lại", null)
                .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveMatch();
                    }
                })
                .show();
    }

    // Trận thay đổi: lần đầu thì bắt đầu chơi; trận đã xong thì gọi server kết thúc; còn lại vẽ điểm đối thủ
    @Override
    public void onSuccess(Match match) {
        if (session == null) {
            startGame(match);
        }
        if (match.isOver()) {
            finishMatch();
        } else {
            showOpponentScore(match);
        }
    }

    // Mất kết nối giữa trận: dừng trận, báo lỗi trên overlay
    @Override
    public void onFail(String message) {
        if (finishing) {
            return;
        }
        finishing = true;
        stopGame();
        showError(message);
    }

    @Override
    protected void onDestroy() {
        stopGame();
        super.onDestroy();
    }

    // Bắt đầu thi đấu: 2 máy cùng questionSeed nên cùng dãy câu hỏi
    private void startGame(Match match) {
        matchFirebaseDAO.registerDisconnect(roomCode, me.getId());
        problemDAO = new ProblemDAO(new Random(match.getQuestionSeed()));
        session = new GameSession(me, ONLINE_DIFFICULTY, true);
        timer = createTimer();
        timer.start();
        showProblem(nextProblem());
    }

    // Chấm đáp án bằng MathProblem + GameSession; đúng thì ghi điểm lên Firebase
    private void submitAnswer(int value) {
        int points = 0;
        if (session.getCurrentProblem().isCorrect(value)) {
            points = session.recordCorrectAnswer();
            matchFirebaseDAO.updateScore(roomCode, me.getId(), session.getScore());
        } else {
            session.recordWrongAnswer();
        }
        showScorePopup(points);
        showProblem(nextProblem());
    }

    // Sinh câu tiếp theo theo mức hiện tại và lưu làm câu hiện tại
    private MathProblem nextProblem() {
        MathProblem problem = problemDAO.generateProblem(session.getCurrentLevel());
        session.setCurrentProblem(problem);
        return problem;
    }

    // Thoát giữa trận: bị xử thua; máy đối thủ thấy forfeited sẽ gọi server kết thúc trận
    private void leaveMatch() {
        stopGame();
        matchFirebaseDAO.forfeit(roomCode, me.getId());
        finish();
    }

    // Trận đã xong: gọi server kết thúc trận trong Thread, chỉ gọi một lần nhờ cờ finishing
    private void finishMatch() {
        if (finishing) {
            return;
        }
        finishing = true;
        stopGame();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Match saved = null;
                try {
                    saved = matchApiDAO.finishMatch(roomCode);
                } catch (IOException | JSONException e) {
                    saved = null;
                }
                showMatchEnd(saved);
            }
        }).start();
    }

    // Về luồng giao diện để hiện kết quả trên bản trận server trả về
    private void showMatchEnd(final Match saved) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (saved == null) {
                    showError("Không lưu được kết quả trận, kiểm tra kết nối mạng");
                } else {
                    showResult(saved);
                }
            }
        });
    }

    // Dừng đồng hồ và gỡ lắng nghe Firebase
    private void stopGame() {
        if (timer != null) {
            timer.cancel();
        }
        matchFirebaseDAO.stopWatching(roomCode);
    }

    // Đồng hồ 60 giây; hết giờ thì ghi timeUp lên Firebase, khóa bàn phím chờ máy đối thủ hết giờ
    private CountDownTimer createTimer() {
        return new CountDownTimer(GAME_MILLIS, 1000) {
            @Override
            public void onTick(long millisLeft) {
                int secondsLeft = (int) ((millisLeft + 999) / 1000);
                txtTimer.setText(String.format(Locale.US, "%02d:%02d", secondsLeft / 60, secondsLeft % 60));
            }

            @Override
            public void onFinish() {
                session.setGameOver(true);
                matchFirebaseDAO.setTimeUp(roomCode, me.getId());
                lockKeypad();
            }
        };
    }

    private void initViews() {
        txtMyScore = findViewById(R.id.txtMyScore);
        txtOpponentScore = findViewById(R.id.txtOpponentScore);
        txtScorePopup = findViewById(R.id.txtScorePopup);
        txtTimer = findViewById(R.id.txtTimer);
        txtProblem = findViewById(R.id.txtProblem);
        txtAnswer = findViewById(R.id.txtAnswer);
        btnBackspace = findViewById(R.id.btnBackspace);
        btnSubmit = findViewById(R.id.btnSubmit);
        layoutResult = findViewById(R.id.layoutResult);
        txtResult = findViewById(R.id.txtResult);
        txtFinalScore = findViewById(R.id.txtFinalScore);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnBackspace.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnBackToMenu.setOnClickListener(this);
        initDigitButtons();
    }

    private void initDigitButtons() {
        int[] digitIds = {R.id.btnDigit0, R.id.btnDigit1, R.id.btnDigit2, R.id.btnDigit3, R.id.btnDigit4,
                R.id.btnDigit5, R.id.btnDigit6, R.id.btnDigit7, R.id.btnDigit8, R.id.btnDigit9};
        for (int i = 0; i < btnDigits.length; i++) {
            btnDigits[i] = findViewById(digitIds[i]);
            btnDigits[i].setOnClickListener(this);
        }
    }

    // Thêm 1 chữ số vào cuối số đang nhập, tối đa 3 chữ số
    private void appendDigit(int digit) {
        String answer = txtAnswer.getText().toString();
        if (answer.equals(EMPTY_ANSWER)) {
            answer = "";
        }
        if (answer.length() < MAX_DIGITS) {
            txtAnswer.setText(answer + digit);
            btnSubmit.setEnabled(true);
        }
    }

    // Xóa chữ số cuối; hết số thì hiện lại dấu ? và khóa nút OK
    private void deleteDigit() {
        String answer = txtAnswer.getText().toString();
        if (answer.length() <= 1 || answer.equals(EMPTY_ANSWER)) {
            txtAnswer.setText(EMPTY_ANSWER);
            btnSubmit.setEnabled(false);
            return;
        }
        txtAnswer.setText(answer.substring(0, answer.length() - 1));
    }

    // Hết giờ: khóa bàn phím, chờ kết quả
    private void lockKeypad() {
        for (Button btn : btnDigits) {
            btn.setEnabled(false);
        }
        btnBackspace.setEnabled(false);
        btnSubmit.setEnabled(false);
        txtTimer.setText("Hết giờ! Đang chờ kết quả...");
    }

    // Đối thủ ghi điểm: vẽ lại điểm đối thủ
    private void showOpponentScore(Match match) {
        for (MatchPlayer mp : match.getPlayers()) {
            if (mp.getPlayer().getId() != me.getId()) {
                txtOpponentScore.setText(String.valueOf(mp.getScore()));
            }
        }
    }

    // Điểm mới của mình, chữ "+12" xanh hoặc "MISS" đỏ bay lên 40dp và mờ dần trong 0,6 giây
    private void showScorePopup(int points) {
        txtMyScore.setText(String.valueOf(session.getScore()));
        txtScorePopup.setText(points > 0 ? "+" + points : "MISS");
        txtScorePopup.setTextColor(ContextCompat.getColor(this, points > 0 ? R.color.arcade_green : R.color.arcade_red));
        txtScorePopup.setVisibility(View.VISIBLE);
        txtScorePopup.setAlpha(1f);
        txtScorePopup.setTranslationY(0f);
        float distance = 40 * getResources().getDisplayMetrics().density;
        txtScorePopup.animate().translationY(-distance).alpha(0f).setDuration(600).start();
    }

    // Câu hỏi mới, xóa số đang nhập
    private void showProblem(MathProblem problem) {
        txtProblem.setText(problem.getExpression() + " = ");
        txtAnswer.setText(EMPTY_ANSWER);
        btnSubmit.setEnabled(false);
    }

    // Overlay kết quả: THẮNG! / THUA! / HÒA! (Match.getResult trên bản trận server trả về) và tỉ số
    private void showResult(Match match) {
        String result = match.getResult(me.getId());
        if (result.equals(Match.WIN)) {
            showResultText("THẮNG!", R.color.arcade_green);
        } else if (result.equals(Match.LOSE)) {
            showResultText("THUA!", R.color.arcade_red);
        } else {
            showResultText("HÒA!", R.color.arcade_amber);
        }
        showFinalScore(match);
        layoutResult.setVisibility(View.VISIBLE);
    }

    private void showResultText(String text, int colorId) {
        txtResult.setText(text);
        txtResult.setTextColor(ContextCompat.getColor(this, colorId));
    }

    // Tỉ số mình - đối thủ; đối thủ bỏ trận thì báo "Đối thủ đã thoát"
    private void showFinalScore(Match match) {
        int myScore = 0;
        int opponentScore = 0;
        for (MatchPlayer mp : match.getPlayers()) {
            if (mp.getPlayer().getId() == me.getId()) {
                myScore = mp.getScore();
            } else {
                opponentScore = mp.getScore();
                showOpponentLeft(mp);
            }
        }
        txtFinalScore.setText(myScore + " - " + opponentScore);
    }

    private void showOpponentLeft(MatchPlayer opponent) {
        if (opponent.isForfeited()) {
            Toast.makeText(this, "Đối thủ đã thoát", Toast.LENGTH_SHORT).show();
        }
    }

    // Mất kết nối hoặc không lưu được kết quả: báo lỗi trên overlay, chỉ còn nút Về menu
    private void showError(String message) {
        txtResult.setText(message);
        txtResult.setTextSize(20);
        txtResult.setTextColor(ContextCompat.getColor(this, R.color.arcade_text_bright));
        txtFinalScore.setVisibility(View.GONE);
        layoutResult.setVisibility(View.VISIBLE);
    }
}
