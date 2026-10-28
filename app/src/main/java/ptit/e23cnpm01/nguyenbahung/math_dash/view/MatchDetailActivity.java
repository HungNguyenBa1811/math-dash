package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.SessionDAO;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

public class MatchDetailActivity extends AppCompatActivity {
    private Player me;
    private Match match;
    private int matchId;
    private TextView txtTitle;
    private TextView txtResult;
    private TextView txtPoints;
    private TextView txtMyName;
    private TextView txtMyStats;
    private TextView txtOpponentName;
    private TextView txtOpponentStats;
    private TextView txtMyColumn;
    private TextView txtOpponentColumn;
    private RecyclerView rvProblems;
    private MatchApiDAO matchApiDAO = new MatchApiDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);
        txtTitle = findViewById(R.id.txtTitle);
        txtResult = findViewById(R.id.txtResult);
        txtPoints = findViewById(R.id.txtPoints);
        txtMyName = findViewById(R.id.txtMyName);
        txtMyStats = findViewById(R.id.txtMyStats);
        txtOpponentName = findViewById(R.id.txtOpponentName);
        txtOpponentStats = findViewById(R.id.txtOpponentStats);
        txtMyColumn = findViewById(R.id.txtMyColumn);
        txtOpponentColumn = findViewById(R.id.txtOpponentColumn);
        rvProblems = findViewById(R.id.rvProblems);
        rvProblems.setLayoutManager(new LinearLayoutManager(this));
        me = new SessionDAO(this).getPlayer();
        matchId = getIntent().getIntExtra(HistoryActivity.EXTRA_MATCH_ID, 0);
        txtTitle.setText(getString(R.string.detail_title, matchId));
        loadMatch();
    }

    private void loadMatch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Match m = matchApiDAO.getById(matchId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMatch(m);
                        }
                    });
                } catch (IOException | JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MatchDetailActivity.this, R.string.msg_detail_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void showMatch(Match m) {
        match = m;
        MatchPlayer opponent = match.getOpponent(me.getId());
        // Dòng của chính mình trong trận là đối thủ của đối thủ
        MatchPlayer mine = match.getOpponent(opponent.getPlayer().getId());
        if (match.getEndedAt() != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            txtTitle.setText(getString(R.string.detail_title_time, match.getId(), format.format(match.getEndedAt())));
        }
        showResult(match.getResult(me.getId()));
        txtPoints.setText(getString(R.string.detail_points, PointFormat.format(match.getPoints(me.getId()))));
        txtMyName.setText(mine.getPlayer().getDisplayName());
        txtMyStats.setText(stats(mine));
        txtOpponentName.setText(opponent.getPlayer().getDisplayName());
        txtOpponentStats.setText(stats(opponent));
        txtMyColumn.setText(mine.getPlayer().getDisplayName());
        txtOpponentColumn.setText(opponent.getPlayer().getDisplayName());
        rvProblems.setAdapter(new ProblemAdapter(match.getQuestions(), mine.getAnswers(), opponent.getAnswers()));
    }

    private void showResult(String result) {
        int color = Match.WIN.equals(result) ? R.color.arcade_green
                : Match.DRAW.equals(result) ? R.color.arcade_amber : R.color.arcade_red;
        int text = Match.WIN.equals(result) ? R.string.result_win
                : Match.DRAW.equals(result) ? R.string.result_draw : R.string.result_lose;
        txtResult.setText(text);
        txtResult.setTextColor(getColor(color));
    }

    // "10/10 câu · 38,2 giây", hoặc "Bỏ trận"
    private String stats(MatchPlayer mp) {
        if (mp.isForfeited()) {
            return getString(R.string.detail_forfeited);
        }
        long time = match.getFinishTime(mp.getPlayer().getId());
        String seconds = time == Long.MAX_VALUE ? getString(R.string.label_no_time)
                : getString(R.string.label_seconds, PointFormat.format(Math.round(time / 100.0) / 10.0));
        return getString(R.string.detail_stats, mp.getCorrectCount(), seconds);
    }
}
