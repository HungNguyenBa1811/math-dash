package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;

public class FriendViewHolder extends RecyclerView.ViewHolder {
    View viewOnlineDot;
    TextView txtName;
    TextView txtPoints;
    TextView txtStatus;
    ImageButton btnUnfriend;

    public FriendViewHolder(@NonNull View itemView) {
        super(itemView);
        viewOnlineDot = itemView.findViewById(R.id.viewOnlineDot);
        txtName = itemView.findViewById(R.id.txtName);
        txtPoints = itemView.findViewById(R.id.txtPoints);
        txtStatus = itemView.findViewById(R.id.txtStatus);
        btnUnfriend = itemView.findViewById(R.id.btnUnfriend);
    }
}
