package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

/**
 * Màn chính: thông tin của mình, danh sách người chơi online (M2-02), Chơi với máy, Bảng xếp hạng, Đăng xuất.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtMyName;
    private TextView txtMyPoints;
    private RecyclerView rvPlayers;
    private Button btnPlayBot;
    private Button btnRanking;
    private Button btnLogout;
    private SessionDAO sessionDAO;
    private Player me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        sessionDAO = new SessionDAO(this);
        me = sessionDAO.getPlayer();
        txtMyName.setText(me.getDisplayName());
        loadMyPoints();
    }

    private void bindViews() {
        txtMyName = findViewById(R.id.txtMyName);
        txtMyPoints = findViewById(R.id.txtMyPoints);
        rvPlayers = findViewById(R.id.rvPlayers);
        btnPlayBot = findViewById(R.id.btnPlayBot);
        btnRanking = findViewById(R.id.btnRanking);
        btnLogout = findViewById(R.id.btnLogout);
        rvPlayers.setLayoutManager(new LinearLayoutManager(this));
        btnPlayBot.setOnClickListener(this);
        btnRanking.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnPlayBot) {
            startActivity(new Intent(this, DifficultyActivity.class));
        } else if (v == btnRanking) {
            startActivity(new Intent(this, RankingActivity.class));
        } else if (v == btnLogout) {
            logout();
        }
    }

    // Lấy tổng điểm mới nhất từ server trong Thread; lỗi mạng thì để trống
    private void loadMyPoints() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Player player = new PlayerApiDAO().getById(me.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMyPoints(player.getTotalPoints());
                        }
                    });
                } catch (IOException | JSONException e) {
                    // Không có mạng: giữ nguyên dòng tổng điểm
                }
            }
        }).start();
    }

    private void showMyPoints(double totalPoints) {
        txtMyPoints.setText(getString(R.string.label_points, PointFormat.format(totalPoints)));
    }

    // Đăng xuất: xóa phiên, quay về màn Đăng nhập
    private void logout() {
        sessionDAO.clear();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
