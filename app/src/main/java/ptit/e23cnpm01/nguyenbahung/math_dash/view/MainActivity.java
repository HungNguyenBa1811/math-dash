package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

// Màn chính: chỉ chọn chế độ chơi
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtMyName;
    private Button btnPlayBot;
    private Button btnPlayOnline;
    private ImageButton btnLogout;
    private Player me;
    private SessionDAO sessionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtMyName = findViewById(R.id.txtMyName);
        btnPlayBot = findViewById(R.id.btnPlayBot);
        btnPlayOnline = findViewById(R.id.btnPlayOnline);
        btnLogout = findViewById(R.id.btnLogout);
        btnPlayBot.setOnClickListener(this);
        btnPlayOnline.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        sessionDAO = new SessionDAO(this);
        me = sessionDAO.getPlayer();
        txtMyName.setText(getString(R.string.label_hello, me.getDisplayName()));
    }

    @Override
    public void onClick(View v) {
        if (v == btnPlayBot) {
            startActivity(new Intent(this, DifficultyActivity.class));
        } else if (v == btnPlayOnline) {
            startActivity(new Intent(this, OnlineActivity.class));
        } else if (v == btnLogout) {
            logout();
        }
    }

    private void logout() {
        sessionDAO.clear();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
