package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.Friendship;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestViewHolder> {
    private List<Friendship> listFriendship = new ArrayList<>();
    private FriendActivity activity;

    public FriendRequestAdapter(FriendActivity activity) {
        this.activity = activity;
    }

    public void setFriendships(List<Friendship> friendships) {
        listFriendship = friendships;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        final Friendship friendship = listFriendship.get(position);
        holder.txtName.setText(friendship.getSender().getDisplayName());
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onAccept(friendship);
            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onReject(friendship);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listFriendship.size();
    }
}
