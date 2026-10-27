package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

public class MatchHistoryAdapter extends RecyclerView.Adapter<MatchHistoryViewHolder> {
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
    private List<Match> listMatch;
    private int myId;
    private HistoryActivity activity;

    public MatchHistoryAdapter(List<Match> matches, int myId, HistoryActivity activity) {
        this.listMatch = matches;
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
        final Match match = listMatch.get(position);
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
        String text = context.getString(resultText);
        if (opponent.isForfeited()) {
            text += " · " + context.getString(R.string.history_opponent_forfeited);
        } else if (mine.isForfeited()) {
            text += " · " + context.getString(R.string.history_me_forfeited);
        }
        holder.txtResult.setText(text);
        holder.txtOpponent.setText(context.getString(R.string.history_opponent,
                opponent.getPlayer().getDisplayName(), match.getId()));
        holder.txtPoints.setText("+" + PointFormat.format(match.getPoints(myId)));
        holder.txtScore.setText(context.getString(R.string.history_score,
                mine.getCorrectCount(), opponent.getCorrectCount()));
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
        return listMatch.size();
    }
}
