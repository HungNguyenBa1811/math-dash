package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder {
    TextView txtName;
    Button btnAccept;
    Button btnReject;

    public FriendRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txtName);
        btnAccept = itemView.findViewById(R.id.btnAccept);
        btnReject = itemView.findViewById(R.id.btnReject);
    }
}
