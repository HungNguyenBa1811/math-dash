package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Friendship;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Player;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

// Lời mời đã gửi nằm cuối danh sách, hiện "Đang chờ trả lời"
public class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {
    private List<Friendship> listFriendship = new ArrayList<>();
    private int playerId;
    private Set<Integer> onlineIds = new HashSet<>();
    private FriendActivity activity;

    public FriendAdapter(FriendActivity activity, int playerId) {
        this.activity = activity;
        this.playerId = playerId;
    }

    public void setFriendships(List<Friendship> friendships) {
        listFriendship = friendships;
        notifyDataSetChanged();
    }

    public void setOnlineIds(Set<Integer> onlineIds) {
        this.onlineIds = onlineIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        final Friendship friendship = listFriendship.get(position);
        Player friend = friendship.getFriend(playerId);
        boolean pending = Friendship.PENDING.equals(friendship.getStatus());
        boolean online = onlineIds.contains(friend.getId());
        holder.txtName.setText(friend.getDisplayName());
        if (pending) {
            holder.txtPoints.setVisibility(View.GONE);
            holder.txtStatus.setText(R.string.friend_pending);
            holder.txtStatus.setTextColor(activity.getColor(R.color.arcade_text_dim));
            holder.viewOnlineDot.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.arcade_panel_stroke)));
            holder.btnUnfriend.setContentDescription(activity.getString(R.string.friend_cancel_request));
        } else {
            holder.txtPoints.setVisibility(View.VISIBLE);
            holder.txtPoints.setText(activity.getString(R.string.label_points, PointFormat.format(friend.getTotalPoints())));
            holder.txtStatus.setText(online ? R.string.friend_online : R.string.friend_offline);
            int color = activity.getColor(online ? R.color.arcade_green : R.color.arcade_text_dim);
            holder.txtStatus.setTextColor(color);
            holder.viewOnlineDot.setBackgroundTintList(ColorStateList.valueOf(color));
            holder.btnUnfriend.setContentDescription(activity.getString(R.string.friend_unfriend));
        }
        holder.btnUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onUnfriend(friendship);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listFriendship.size();
    }
}
