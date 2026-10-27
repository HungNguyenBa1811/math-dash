package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

public class RankingAdapter extends RecyclerView.Adapter<RankingViewHolder> {
    private List<Player> listPlayer;
    private int myId;

    public RankingAdapter(List<Player> players, int myId) {
        this.listPlayer = players;
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
        Player p = listPlayer.get(position);
        Context context = holder.itemView.getContext();
        holder.txtRank.setText(String.valueOf(position + 1));
        holder.txtName.setText(p.getDisplayName());
        holder.txtPoints.setText(PointFormat.format(p.getTotalPoints()));
        holder.txtAvgOpponent.setText(PointFormat.format(Math.round(p.getAvgOpponentPoints() * 100) / 100.0));
        holder.txtAvgWinTime.setText(p.getAvgWinTime() == 0 ? context.getString(R.string.label_no_time)
                : context.getString(R.string.label_seconds_short, PointFormat.format(Math.round(p.getAvgWinTime() * 10) / 10.0)));
        boolean mine = p.getId() == myId;
        holder.itemView.setBackgroundResource(mine ? R.drawable.bg_retro_button_accent : R.drawable.bg_retro_card);
        holder.txtName.setTextColor(context.getColor(mine ? R.color.arcade_bg : R.color.arcade_text_bright));
    }

    @Override
    public int getItemCount() {
        return listPlayer.size();
    }
}
