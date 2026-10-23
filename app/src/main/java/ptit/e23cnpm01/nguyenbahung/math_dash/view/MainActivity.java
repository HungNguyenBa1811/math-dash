package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Màn Chính: tên hiển thị và 5 nút chức năng.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtDisplayName;
    private Button btnPlayOffline;
    private Button btnPlayOnline;
    private Button btnHistory;
    private Button btnSettings;
    private Button btnLogout;
    private SessionDAO sessionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtDisplayName = findViewById(R.id.txtDisplayName);
        btnPlayOffline = findViewById(R.id.btnPlayOffline);
        btnPlayOnline = findViewById(R.id.btnPlayOnline);
        btnHistory = findViewById(R.id.btnHistory);
        btnSettings = findViewById(R.id.btnSettings);
        btnLogout = findViewById(R.id.btnLogout);
        btnPlayOffline.setOnClickListener(this);
        btnPlayOnline.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        sessionDAO = new SessionDAO(this);
        Player me = sessionDAO.getPlayer();
        txtDisplayName.setText("Xin chào, " + me.getDisplayName());
    }

    @Override
    public void onClick(View v) {
        if (v == btnPlayOffline) {
            startActivity(new Intent(this, DifficultyActivity.class));
        } else if (v == btnPlayOnline) {
            startActivity(new Intent(this, OnlineActivity.class));
        } else if (v == btnHistory) {
            startActivity(new Intent(this, HistoryActivity.class));
        } else if (v == btnSettings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (v == btnLogout) {
            logout();
        }
    }

    // Đăng xuất: xóa phiên, quay về màn Đăng nhập
    private void logout() {
        sessionDAO.clear();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
