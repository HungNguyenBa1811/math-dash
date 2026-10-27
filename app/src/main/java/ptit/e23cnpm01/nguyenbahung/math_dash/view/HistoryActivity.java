package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;

public class HistoryActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    public static final String EXTRA_MATCH_ID = "matchId";
    // Thẻ mở sẵn khi vào màn: 0 là Trận của tôi, TAB_RANKING là Bảng xếp hạng
    public static final String EXTRA_TAB = "tab";
    public static final int TAB_RANKING = 1;

    private Player me;
    private List<Match> matches;
    private TabLayout tabLayout;
    private LinearLayout layoutMyMatches;
    private PieChartView pieChart;
    private TextView txtWins;
    private TextView txtDraws;
    private TextView txtLosses;
    private TextView txtTotal;
    private TextView txtEmpty;
    private RecyclerView rvMatches;
    private LinearLayout layoutRanking;
    private RecyclerView rvRanking;
    private boolean rankingLoaded;
    private MatchApiDAO matchApiDAO = new MatchApiDAO();
    private PlayerApiDAO playerApiDAO = new PlayerApiDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        tabLayout = findViewById(R.id.tabLayout);
        layoutMyMatches = findViewById(R.id.layoutMyMatches);
        pieChart = findViewById(R.id.pieChart);
        txtWins = findViewById(R.id.txtWins);
        txtDraws = findViewById(R.id.txtDraws);
        txtLosses = findViewById(R.id.txtLosses);
        txtTotal = findViewById(R.id.txtTotal);
        txtEmpty = findViewById(R.id.txtEmpty);
        rvMatches = findViewById(R.id.rvMatches);
        layoutRanking = findViewById(R.id.layoutRanking);
        rvRanking = findViewById(R.id.rvRanking);
        rvMatches.setLayoutManager(new LinearLayoutManager(this));
        rvRanking.setLayoutManager(new LinearLayoutManager(this));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_my_matches));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_ranking));
        tabLayout.addOnTabSelectedListener(this);
        me = new SessionDAO(this).getPlayer();
        showSummary(null);
        loadHistory();
        if (getIntent().getIntExtra(EXTRA_TAB, 0) == TAB_RANKING) {
            tabLayout.selectTab(tabLayout.getTabAt(TAB_RANKING));
        }
    }

    private void loadHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Match> list = matchApiDAO.getHistory(me.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            matches = list;
                            showSummary(matches);
                            rvMatches.setAdapter(new MatchHistoryAdapter(matches, me.getId(), HistoryActivity.this));
                            txtEmpty.setVisibility(matches.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    });
                } catch (IOException | JSONException e) {
                    showError(R.string.msg_history_error);
                }
            }
        }).start();
    }

    private void showSummary(List<Match> matches) {
        int wins = 0;
        int draws = 0;
        int losses = 0;
        if (matches != null) {
            for (Match m : matches) {
                String result = m.getResult(me.getId());
                if (Match.WIN.equals(result)) {
                    wins++;
                } else if (Match.DRAW.equals(result)) {
                    draws++;
                } else {
                    losses++;
                }
            }
        }
        pieChart.setData(wins, draws, losses);
        txtWins.setText(getString(R.string.label_wins, wins));
        txtDraws.setText(getString(R.string.label_draws, draws));
        txtLosses.setText(getString(R.string.label_losses, losses));
        txtTotal.setText(getString(R.string.label_total_matches, wins + draws + losses));
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        boolean ranking = tab.getPosition() == TAB_RANKING;
        layoutMyMatches.setVisibility(ranking ? View.GONE : View.VISIBLE);
        layoutRanking.setVisibility(ranking ? View.VISIBLE : View.GONE);
        if (ranking && !rankingLoaded) {
            loadRanking();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    // Chỉ tải lần đầu chọn thẻ
    private void loadRanking() {
        rankingLoaded = true;
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
                    rankingLoaded = false;
                    showError(R.string.msg_ranking_error);
                }
            }
        }).start();
    }

    public void onMatchClick(Match match) {
        Intent intent = new Intent(this, MatchDetailActivity.class);
        intent.putExtra(EXTRA_MATCH_ID, match.getId());
        startActivity(intent);
    }

    private void showError(final int messageRes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HistoryActivity.this, messageRes, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
