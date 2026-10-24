package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MatchPlayer;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
    private List<Match> listMatch;
    private int playerId;

    public HistoryAdapter(List<Match> listMatch, int playerId) {
        this.listMatch = listMatch;
        this.playerId = playerId;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    // Tìm điểm của mình và của đối thủ; kết quả tính lại bằng Match.getResult, không lưu trong CSDL
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Match match = listMatch.get(position);
        MatchPlayer mine = null;
        MatchPlayer opponent = null;
        for (MatchPlayer mp : match.getPlayers()) {
            if (mp.getPlayer().getId() == playerId) {
                mine = mp;
            } else {
                opponent = mp;
            }
        }
        holder.txtPlayedAt.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(match.getEndedAt()));
        holder.txtOpponent.setText("vs " + opponent.getPlayer().getDisplayName());
        holder.txtScore.setText(mine.getScore() + " - " + opponent.getScore());
        String result = match.getResult(playerId);
        int color = result.equals(Match.WIN) ? R.color.arcade_green
                : result.equals(Match.LOSE) ? R.color.arcade_red : R.color.arcade_amber;
        holder.txtResult.setText(result.equals(Match.WIN) ? "THẮNG" : result.equals(Match.LOSE) ? "THUA" : "HÒA");
        holder.txtResult.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), color));
    }

    @Override
    public int getItemCount() {
        return listMatch == null ? 0 : listMatch.size();
    }
}
