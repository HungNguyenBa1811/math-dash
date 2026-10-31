package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Player;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

public class PlayerAdapter extends BaseAdapter<Player, PlayerViewHolder> {
    private OnlineActivity activity;

    public PlayerAdapter(OnlineActivity activity) {
        this.activity = activity;
    }

    public void setPlayers(List<Player> players) {
        items = players;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        final Player player = items.get(position);
        boolean busy = Player.BUSY.equals(player.getStatus());
        holder.txtName.setText(player.getDisplayName());
        holder.txtPoints.setText(activity.getString(R.string.label_points, PointFormat.format(player.getTotalPoints())));
        holder.txtStatus.setText(busy ? R.string.status_busy : R.string.status_free);
        holder.txtStatus.setTextColor(activity.getColor(busy ? R.color.arcade_text_dim : R.color.arcade_green));
        holder.txtStatus.setTypeface(Typeface.MONOSPACE, busy ? Typeface.NORMAL : Typeface.BOLD);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onPlayerClick(player);
            }
        });
    }
}
