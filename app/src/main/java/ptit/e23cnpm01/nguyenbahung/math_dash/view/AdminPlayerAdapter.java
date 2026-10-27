package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

/**
 * Danh sách người chơi ở màn quản trị: tên đăng nhập, tên hiển thị, tổng điểm, ngày tạo.
 * Bấm vào một dòng để xem và sửa thông tin người chơi đó.
 */
public class AdminPlayerAdapter extends RecyclerView.Adapter<AdminPlayerViewHolder> {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    private List<Player> listPlayer = new ArrayList<>();
    private AdminActivity activity;

    public AdminPlayerAdapter(AdminActivity activity) {
        this.activity = activity;
    }

    public void setPlayers(List<Player> players) {
        listPlayer = players;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_player, parent, false);
        return new AdminPlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPlayerViewHolder holder, int position) {
        final Player player = listPlayer.get(position);
        holder.txtUsername.setText(player.getUsername());
        holder.txtDisplayName.setText(player.getDisplayName());
        holder.txtPoints.setText(PointFormat.format(player.getTotalPoints()));
        holder.txtCreatedAt.setText(player.getCreatedAt() == null
                ? activity.getString(R.string.label_no_time) : dateFormat.format(player.getCreatedAt()));
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
