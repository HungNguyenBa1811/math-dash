package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

/**
 * Bảng xếp hạng: tổng điểm ↓, trung bình điểm đối thủ ↓, trung bình thời gian thắng ↑ (server sắp xếp sẵn).
 */
public class RankingActivity extends AppCompatActivity {
    private RecyclerView rvRanking;
    private Player me;
    private PlayerApiDAO playerApiDAO = new PlayerApiDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        rvRanking = findViewById(R.id.rvRanking);
        rvRanking.setLayoutManager(new LinearLayoutManager(this));
        me = new SessionDAO(this).getPlayer();
        loadRanking();
    }

    // Lấy bảng xếp hạng trong Thread, gắn vào danh sách trên luồng giao diện
    private void loadRanking() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Player> players = playerApiDAO.getRanking();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rvRanking.setAdapter(new RankingAdapter(players, me.getId()));
                        }
                    });
                } catch (IOException | JSONException e) {
                    showError();
                }
            }
        }).start();
    }

    private void showError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RankingActivity.this, R.string.msg_ranking_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
