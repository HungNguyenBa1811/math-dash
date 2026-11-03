package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.PlayerApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;

public class HistoryActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    public static final String EXTRA_MATCH_ID = "matchId";
    // Tab selected when the screen opens
    public static final String EXTRA_TAB = "tab";
    public static final int TAB_RANKING = 1;

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
        pieChart.setData(wins, draws, losses);
        txtWins.setText(String.valueOf(wins));
        txtDraws.setText(String.valueOf(draws));
        txtLosses.setText(String.valueOf(losses));
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

    // Load only the first time the tab is selected
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

    private static class MatchHistoryAdapter extends RecyclerView.Adapter<MatchHistoryViewHolder> {
        private List<Match> items = new ArrayList<>();
        private final SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        private int myId;
        private HistoryActivity activity;

        public MatchHistoryAdapter(List<Match> matches, int myId, HistoryActivity activity) {
            this.items = matches;
            this.myId = myId;
            this.activity = activity;
        }

        @NonNull
        @Override
        public MatchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_history, parent, false);
            return new MatchHistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MatchHistoryViewHolder holder, int position) {
            final Match match = items.get(position);
            Context context = holder.itemView.getContext();
            MatchPlayer opponent = match.getOpponent(myId);
            // Our own row is the opponent's opponent
            MatchPlayer mine = match.getOpponent(opponent.getPlayer().getId());
            String result = match.getResult(myId);

            int color;
            int barColor;
            int resultText;
            if (Match.WIN.equals(result)) {
                color = R.color.win;
                barColor = R.color.win;
                resultText = R.string.history_win;
            } else if (Match.DRAW.equals(result)) {
                color = R.color.draw;
                barColor = R.color.draw_fill;
                resultText = R.string.history_draw;
            } else {
                color = R.color.lose;
                barColor = R.color.lose;
                resultText = R.string.history_lose;
            }
            holder.viewResultBar.setBackgroundTintList(ColorStateList.valueOf(context.getColor(barColor)));
            holder.txtResult.setTextColor(context.getColor(color));
            holder.txtResult.setText(resultText);
            holder.txtOpponent.setText(context.getString(R.string.history_opponent,
                    opponent.getPlayer().getDisplayName(), match.getId()));
            holder.txtPoints.setText("+" + PointFormat.format(match.getPoints(myId)));
            // e.g. "6 – 3 câu · Đối thủ bỏ trận"
            String score = context.getString(R.string.history_score,
                    mine.getCorrectCount(), opponent.getCorrectCount());
            if (opponent.isForfeited()) {
                score += " · " + context.getString(R.string.history_opponent_forfeited);
            } else if (mine.isForfeited()) {
                score += " · " + context.getString(R.string.history_me_forfeited);
            }
            holder.txtScore.setText(score);
            holder.txtEndedAt.setText(match.getEndedAt() == null ? "" : timeFormat.format(match.getEndedAt()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onMatchClick(match);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class MatchHistoryViewHolder extends RecyclerView.ViewHolder {
        View viewResultBar;
        TextView txtOpponent;
        TextView txtResult;
        TextView txtPoints;
        TextView txtScore;
        TextView txtEndedAt;

        public MatchHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewResultBar = itemView.findViewById(R.id.viewResultBar);
            txtOpponent = itemView.findViewById(R.id.txtOpponent);
            txtResult = itemView.findViewById(R.id.txtResult);
            txtPoints = itemView.findViewById(R.id.txtPoints);
            txtScore = itemView.findViewById(R.id.txtScore);
            txtEndedAt = itemView.findViewById(R.id.txtEndedAt);
        }
    }

    private static class RankingAdapter extends RecyclerView.Adapter<RankingViewHolder> {
        private List<Player> items = new ArrayList<>();
        private int myId;

        public RankingAdapter(List<Player> players, int myId) {
            this.items = players;
            this.myId = myId;
        }

        @NonNull
        @Override
        public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
            return new RankingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
            Player p = items.get(position);
            Context context = holder.itemView.getContext();
            holder.txtRank.setText(String.valueOf(position + 1));
            holder.txtName.setText(p.getDisplayName());
            holder.txtPoints.setText(PointFormat.format(p.getTotalPoints()));
            // The viewer's own row
            boolean mine = p.getId() == myId;
            holder.itemView.setBackgroundResource(mine ? R.drawable.bg_highlight : R.drawable.bg_card);
            TextView[] cells = {holder.txtRank, holder.txtName, holder.txtPoints};
            Typeface body = ResourcesCompat.getFont(context, R.font.body);
            for (TextView cell : cells) {
                cell.setTextColor(context.getColor(mine ? R.color.on_primary : R.color.ink));
                cell.setTypeface(body, mine ? Typeface.BOLD : Typeface.NORMAL);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView txtRank;
        TextView txtName;
        TextView txtPoints;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRank = itemView.findViewById(R.id.txtRank);
            txtName = itemView.findViewById(R.id.txtName);
            txtPoints = itemView.findViewById(R.id.txtPoints);
        }
    }

    // Win / draw / loss donut chart drawn with Canvas
    public static class PieChartView extends View {
        private int wins;
        private int draws;
        private int losses;
        private final Paint arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint percentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF oval = new RectF();

        public PieChartView(Context context, AttributeSet attrs) {
            super(context, attrs);
            arcPaint.setStyle(Paint.Style.STROKE);
            percentPaint.setTextAlign(Paint.Align.CENTER);
            percentPaint.setTypeface(ResourcesCompat.getFont(context, R.font.baloo2_extrabold));
            percentPaint.setColor(context.getColor(R.color.ink));
            labelPaint.setTextAlign(Paint.Align.CENTER);
            labelPaint.setTypeface(ResourcesCompat.getFont(context, R.font.be_vietnam_pro_semibold));
            labelPaint.setColor(context.getColor(R.color.ink_dim));
        }

        public void setData(int wins, int draws, int losses) {
            this.wins = wins;
            this.draws = draws;
            this.losses = losses;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float size = Math.min(getWidth(), getHeight());
            float stroke = size * 0.16f;
            float left = (getWidth() - size) / 2 + stroke / 2;
            float top = (getHeight() - size) / 2 + stroke / 2;
            oval.set(left, top, left + size - stroke, top + size - stroke);
            arcPaint.setStrokeWidth(stroke);

            int total = wins + draws + losses;
            if (total == 0) {
                arcPaint.setColor(getContext().getColor(R.color.line));
                canvas.drawArc(oval, 0, 360, false, arcPaint);
            } else {
                // From the top, clockwise
                float start = -90;
                start = drawPart(canvas, start, wins, total, R.color.win);
                start = drawPart(canvas, start, draws, total, R.color.draw_fill);
                drawPart(canvas, start, losses, total, R.color.lose);
            }

            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            percentPaint.setTextSize(size * 0.2f);
            labelPaint.setTextSize(size * 0.1f);
            String percent = total == 0 ? getContext().getString(R.string.label_no_time)
                    : Math.round(wins * 100f / total) + "%";
            canvas.drawText(percent, cx, cy + size * 0.03f, percentPaint);
            canvas.drawText(getContext().getString(R.string.label_win_rate), cx, cy + size * 0.16f, labelPaint);
        }

        // Returns the start angle of the next arc
        private float drawPart(Canvas canvas, float start, int count, int total, int colorRes) {
            if (count == 0) {
                return start;
            }
            float sweep = 360f * count / total;
            arcPaint.setColor(getContext().getColor(colorRes));
            canvas.drawArc(oval, start, sweep, false, arcPaint);
            return start + sweep;
        }
    }
}
