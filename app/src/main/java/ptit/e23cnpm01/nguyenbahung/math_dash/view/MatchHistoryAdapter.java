package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

public class MatchHistoryAdapter extends BaseAdapter<Match, MatchHistoryViewHolder> {
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
        // Dòng của chính mình trong trận là đối thủ của đối thủ
        MatchPlayer mine = match.getOpponent(opponent.getPlayer().getId());
        String result = match.getResult(myId);

        int color;
        int resultText;
        if (Match.WIN.equals(result)) {
            color = R.color.arcade_green;
            resultText = R.string.history_win;
        } else if (Match.DRAW.equals(result)) {
            color = R.color.arcade_amber;
            resultText = R.string.history_draw;
        } else {
            color = R.color.arcade_red;
            resultText = R.string.history_lose;
        }
        holder.viewResultBar.setBackgroundColor(context.getColor(color));
        holder.txtResult.setTextColor(context.getColor(color));
        holder.txtResult.setText(resultText);
        holder.txtOpponent.setText(context.getString(R.string.history_opponent,
                opponent.getPlayer().getDisplayName(), match.getId()));
        holder.txtPoints.setText("+" + PointFormat.format(match.getPoints(myId)));
        // "6 – 3 câu · Đối thủ bỏ trận"
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
}
