package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.DialogInterface;
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

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.BotGameDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.ProblemDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.BotGame;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

/**
 * Thi đấu với máy: 10 câu trắc nghiệm trong 60 giây, máy giả lập bài làm theo độ khó (Hoàng, M2-06).
 * Luật thắng thua nằm ở BotGame.getResult(), Activity chỉ chấm số câu đúng, lưu ván và hiển thị.
 */
public class BotGameActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_DIFFICULTY = "difficulty";
    private static final long GAME_MILLIS = 60_000;
    // Theo độ khó Dễ / Trung bình / Khó: xác suất máy làm đúng mỗi câu, khoảng thời gian máy nộp bài (giây)
    private static final double[] BOT_ACCURACY = {0.60, 0.80, 0.95};
    private static final int[] BOT_MIN_SECONDS = {45, 35, 25};
    private static final int[] BOT_MAX_SECONDS = {58, 48, 40};

    private TextView txtTimer;
    private TextView txtBotStatus;
    private RecyclerView rvQuestions;
    private Button btnExit;
    private Button btnSubmit;
    private LinearLayout layoutResult;
    private TextView txtResult;
    private TextView txtMyResult;
    private TextView txtBotResult;
    private Button btnMenu;
    private Button btnPlayAgain;
    private Player me;
    private BotGame game;
    private long startTime;
    private boolean submitted;
    private Random random = new Random();
    private CountDownTimer timer;
    private QuestionAdapter questionAdapter;
    private SessionDAO sessionDAO;
    private ProblemDAO problemDAO = new ProblemDAO();
    private BotGameDAO botGameDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot_game);
        bindViews();
        sessionDAO = new SessionDAO(this);
        botGameDAO = new BotGameDAO(this);
        me = sessionDAO.getPlayer();
        startGame();
    }

    private void bindViews() {
        txtTimer = findViewById(R.id.txtTimer);
        txtBotStatus = findViewById(R.id.txtBotStatus);
        rvQuestions = findViewById(R.id.rvQuestions);
        btnExit = findViewById(R.id.btnExit);
        btnSubmit = findViewById(R.id.btnSubmit);
        layoutResult = findViewById(R.id.layoutResult);
        txtResult = findViewById(R.id.txtResult);
        txtMyResult = findViewById(R.id.txtMyResult);
        txtBotResult = findViewById(R.id.txtBotResult);
        btnMenu = findViewById(R.id.btnMenu);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        questionAdapter = new QuestionAdapter();
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        rvQuestions.setAdapter(questionAdapter);
        for (Button b : new Button[]{btnExit, btnSubmit, btnMenu, btnPlayAgain}) {
            b.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            submitAnswers();
        } else if (v == btnExit) {
            confirmExit();
        } else if (v == btnPlayAgain) {
            startGame();
        } else if (v == btnMenu) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        confirmExit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    // ---------- Bắt đầu ván ----------
    // Tạo ván mới: sinh đề 10 câu, giả lập bài làm của máy, bắt đầu đếm 60 giây
    private void startGame() {
        int difficulty = getIntent().getIntExtra(EXTRA_DIFFICULTY, BotGame.EASY);
        game = new BotGame(me, difficulty);
        game.setQuestions(problemDAO.generateProblems(Match.QUESTION_COUNT));
        simulateBot();
        submitted = false;
        layoutResult.setVisibility(View.GONE);
        btnSubmit.setEnabled(true);
        txtBotStatus.setText(R.string.status_bot_working);
        txtTimer.setText(R.string.time_full);
        questionAdapter.setQuestions(game.getQuestions());
        questionAdapter.setLocked(false);
        rvQuestions.scrollToPosition(0);
        startTime = System.currentTimeMillis();
        timer = createTimer();
        timer.start();
    }

    // Máy làm đúng mỗi câu theo xác suất của độ khó, nộp bài vào một thời điểm ngẫu nhiên
    private void simulateBot() {
        int level = game.getDifficulty() - 1;
        int correct = 0;
        for (int i = 0; i < Match.QUESTION_COUNT; i++) {
            if (random.nextDouble() < BOT_ACCURACY[level]) {
                correct++;
            }
        }
        int seconds = BOT_MIN_SECONDS[level] + random.nextInt(BOT_MAX_SECONDS[level] - BOT_MIN_SECONDS[level] + 1);
        game.setBotCorrectCount(correct);
        game.setBotFinishTime(seconds * 1000L);
    }

    // ---------- Đếm giờ ----------
    private CountDownTimer createTimer() {
        if (timer != null) {
            timer.cancel();
        }
        return new CountDownTimer(GAME_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimer(millisUntilFinished);
            }

            // Hết 60 giây: tự nộp, câu chưa chọn tính là sai
            @Override
            public void onFinish() {
                txtTimer.setText(R.string.time_zero);
                submitAnswers();
            }
        };
    }

    // Cập nhật ô thời gian; tới thời điểm nộp của máy thì đổi dòng trạng thái
    private void updateTimer(long millisLeft) {
        txtTimer.setText(String.format(Locale.US, "00:%02d", millisLeft / 1000));
        if (System.currentTimeMillis() - startTime >= game.getBotFinishTime()) {
            txtBotStatus.setText(R.string.status_bot_submitted);
        }
    }

    // ---------- Nộp bài ----------
    // Chấm bài của người chơi, ghi thời gian làm bài, rồi lưu ván
    private void submitAnswers() {
        if (submitted) {
            return;
        }
        submitted = true;
        timer.cancel();
        btnSubmit.setEnabled(false);
        List<Integer> answers = questionAdapter.getAnswers();
        questionAdapter.setLocked(true);
        long finishTime = Math.min(System.currentTimeMillis() - startTime, GAME_MILLIS);
        game.setCorrectCount(countCorrect(answers));
        game.setFinishTime(finishTime);
        game.setPlayedAt(new Date());
        saveGame();
    }

    // Số câu có phương án đã chọn trùng đáp án đúng; câu chưa chọn (-1) là sai
    private int countCorrect(List<Integer> answers) {
        List<MathProblem> questions = game.getQuestions();
        int count = 0;
        for (int i = 0; i < questions.size() && i < answers.size(); i++) {
            if (questions.get(i).isCorrect(answers.get(i))) {
                count++;
            }
        }
        return count;
    }

    // Lưu ván vào SQLite trong Thread, xong thì hiện kết quả trên luồng giao diện
    private void saveGame() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean saved = botGameDAO.insert(game);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!saved) {
                            Toast.makeText(BotGameActivity.this, R.string.msg_save_failed, Toast.LENGTH_SHORT).show();
                        }
                        showResult();
                    }
                });
            }
        }).start();
    }

    // ---------- Kết quả ----------
    private void showResult() {
        String result = game.getResult();
        int text = Match.WIN.equals(result) ? R.string.result_win
                : Match.LOSE.equals(result) ? R.string.result_lose : R.string.result_draw;
        int color = Match.WIN.equals(result) ? R.color.arcade_green
                : Match.LOSE.equals(result) ? R.color.arcade_red : R.color.arcade_amber;
        txtResult.setText(text);
        txtResult.setTextColor(getColor(color));
        txtMyResult.setText(getString(R.string.label_my_result, game.getCorrectCount(),
                formatTime(game.getFinishTime())));
        txtBotResult.setText(getString(R.string.label_bot_result, game.getBotCorrectCount(),
                formatTime(game.getBotFinishTime())));
        layoutResult.setVisibility(View.VISIBLE);
    }

    // ---------- Thoát ----------
    // Đang làm bài thì hỏi lại trước khi hủy ván; đã có kết quả thì về menu luôn
    private void confirmExit() {
        if (submitted) {
            finish();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage(R.string.msg_confirm_exit_bot)
                .setNegativeButton(R.string.btn_stay, null)
                .setPositiveButton(R.string.btn_leave, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    // 38200 ms → "38,2 giây"
    private String formatTime(long millis) {
        return getString(R.string.label_seconds, PointFormat.format(Math.round(millis / 100.0) / 10.0));
    }
}
