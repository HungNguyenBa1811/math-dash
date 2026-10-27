package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.BotGame;

/**
 * Chọn độ khó của máy: Dễ, Trung bình, Khó (Hoàng, M2-06).
 */
public class DifficultyActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnEasy;
    private Button btnMedium;
    private Button btnHard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        btnEasy = findViewById(R.id.btnEasy);
        btnMedium = findViewById(R.id.btnMedium);
        btnHard = findViewById(R.id.btnHard);
        btnEasy.setOnClickListener(this);
        btnMedium.setOnClickListener(this);
        btnHard.setOnClickListener(this);
    }

    // Mở màn thi đấu với độ khó đã chọn, đóng màn này để Về menu quay thẳng về màn chính
    @Override
    public void onClick(View v) {
        int difficulty = BotGame.EASY;
        if (v == btnMedium) {
            difficulty = BotGame.MEDIUM;
        } else if (v == btnHard) {
            difficulty = BotGame.HARD;
        }
        Intent intent = new Intent(this, BotGameActivity.class);
        intent.putExtra(BotGameActivity.EXTRA_DIFFICULTY, difficulty);
        startActivity(intent);
        finish();
    }
}
