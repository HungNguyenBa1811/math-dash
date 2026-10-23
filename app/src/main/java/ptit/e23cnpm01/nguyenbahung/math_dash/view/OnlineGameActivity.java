package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.controller.GameListener;
import ptit.e23cnpm01.nguyenbahung.math_dash.controller.MatchController;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Màn Thi đấu online. Activity chỉ hiển thị: không tự chấm điểm, không tự phân thắng thua.
 */
public class OnlineGameActivity extends AppCompatActivity implements View.OnClickListener, GameListener {
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
    private Player me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game);
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
        int[] digitIds = {R.id.btnDigit0, R.id.btnDigit1, R.id.btnDigit2, R.id.btnDigit3, R.id.btnDigit4,
                R.id.btnDigit5, R.id.btnDigit6, R.id.btnDigit7, R.id.btnDigit8, R.id.btnDigit9};
        for (int i = 0; i < btnDigits.length; i++) {
            btnDigits[i] = findViewById(digitIds[i]);
            btnDigits[i].setOnClickListener(this);
        }
        btnBackspace.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnBackToMenu.setOnClickListener(this);
        me = new SessionDAO(this).getPlayer();
        showProblem(MatchController.getInstance().startGame(this));
    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            MatchController controller = MatchController.getInstance();
            int points = controller.submitAnswer(Integer.parseInt(txtAnswer.getText().toString()));
            MathProblem next = controller.nextProblem();
            showScorePopup(points);
            showProblem(next);
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
                        MatchController.getInstance().leaveMatch();
                        finish();
                    }
                })
                .show();
    }

    // Đối thủ ghi điểm: vẽ lại điểm đối thủ
    @Override
    public void onMatchChanged(Match match) {
        for (MatchPlayer mp : match.getPlayers()) {
            if (mp.getPlayer().getId() != me.getId()) {
                txtOpponentScore.setText(String.valueOf(mp.getScore()));
            }
        }
    }

    @Override
    public void onTick(int secondsLeft) {
        txtTimer.setText(String.format(Locale.US, "%02d:%02d", secondsLeft / 60, secondsLeft % 60));
    }

    // Hết giờ: khóa bàn phím, chờ máy đối thủ hết giờ
    @Override
    public void onTimeUp() {
        for (Button btn : btnDigits) {
            btn.setEnabled(false);
        }
        btnBackspace.setEnabled(false);
        btnSubmit.setEnabled(false);
        txtTimer.setText("Hết giờ! Đang chờ kết quả...");
    }

    // Server đã lưu trận: hiện kết quả trên bản trận server trả về
    @Override
    public void onMatchEnd(Match match) {
        for (MatchPlayer mp : match.getPlayers()) {
            if (mp.getPlayer().getId() != me.getId() && mp.isForfeited()) {
                Toast.makeText(this, "Đối thủ đã thoát", Toast.LENGTH_SHORT).show();
            }
        }
        showResult(match);
    }

    // Mất kết nối hoặc không lưu được kết quả: báo lỗi trên overlay, chỉ còn nút Về menu
    @Override
    public void onError(String message) {
        txtResult.setText(message);
        txtResult.setTextSize(20);
        txtResult.setTextColor(ContextCompat.getColor(this, R.color.arcade_text_bright));
        txtFinalScore.setVisibility(View.GONE);
        layoutResult.setVisibility(View.VISIBLE);
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

    // Điểm mới của mình, chữ "+12" xanh hoặc "MISS" đỏ bay lên 40dp và mờ dần trong 0,6 giây
    private void showScorePopup(int points) {
        txtMyScore.setText(String.valueOf(MatchController.getInstance().getSession().getScore()));
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

    // Overlay kết quả: THẮNG! / THUA! / HÒA! và tỉ số mình - đối thủ
    private void showResult(Match match) {
        String result = match.getResult(me.getId());
        int myScore = 0;
        int opponentScore = 0;
        for (MatchPlayer mp : match.getPlayers()) {
            if (mp.getPlayer().getId() == me.getId()) {
                myScore = mp.getScore();
            } else {
                opponentScore = mp.getScore();
            }
        }
        if (result.equals(Match.WIN)) {
            txtResult.setText("THẮNG!");
            txtResult.setTextColor(ContextCompat.getColor(this, R.color.arcade_green));
        } else if (result.equals(Match.LOSE)) {
            txtResult.setText("THUA!");
            txtResult.setTextColor(ContextCompat.getColor(this, R.color.arcade_red));
        } else {
            txtResult.setText("HÒA!");
            txtResult.setTextColor(ContextCompat.getColor(this, R.color.arcade_amber));
        }
        txtFinalScore.setText(myScore + " - " + opponentScore);
        layoutResult.setVisibility(View.VISIBLE);
    }
}
