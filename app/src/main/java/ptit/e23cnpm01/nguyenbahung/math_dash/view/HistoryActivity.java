package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView rvHistory;
    private TextView txtEmpty;
    private HistoryAdapter adapter;
    private MatchApiDAO matchApiDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        rvHistory = findViewById(R.id.rvHistory);
        txtEmpty = findViewById(R.id.txtEmpty);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        matchApiDAO = new MatchApiDAO();
        Player me = new SessionDAO(this).getPlayer();
        loadHistory(me.getId());
    }

    // Gọi API trong Thread, gắn danh sách lên giao diện trong runOnUiThread
    private void loadHistory(final int playerId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Match> listMatch = matchApiDAO.getHistory(playerId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new HistoryAdapter(listMatch, playerId);
                            rvHistory.setAdapter(adapter);
                            rvHistory.setVisibility(listMatch.isEmpty() ? View.GONE : View.VISIBLE);
                            txtEmpty.setVisibility(listMatch.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HistoryActivity.this, "Không tải được lịch sử đấu, kiểm tra kết nối mạng",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
}
