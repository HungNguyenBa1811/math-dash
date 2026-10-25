package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

/**
 * Danh sách người chơi online: tên, tổng điểm, trạng thái. Bấm vào một dòng để thách đấu.
 */
public class PlayerAdapter extends RecyclerView.Adapter<PlayerViewHolder> {
    private List<Player> listPlayer = new ArrayList<>();
    private MainActivity activity;

    public PlayerAdapter(MainActivity activity) {
        this.activity = activity;
    }

    public void setPlayers(List<Player> players) {
        listPlayer = players;
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
        final Player player = listPlayer.get(position);
        boolean busy = Player.BUSY.equals(player.getStatus());
        holder.txtName.setText(player.getDisplayName());
        holder.txtPoints.setText(activity.getString(R.string.label_points, PointFormat.format(player.getTotalPoints())));
        holder.txtStatus.setText(busy ? R.string.status_busy : R.string.status_free);
        holder.txtStatus.setTextColor(activity.getColor(busy ? R.color.arcade_text_dim : R.color.arcade_green));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onPlayerClick(player);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPlayer.size();
    }
}
