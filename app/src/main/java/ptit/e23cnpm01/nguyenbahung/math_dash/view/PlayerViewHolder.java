package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;

public class PlayerViewHolder extends RecyclerView.ViewHolder {
    TextView txtName;
    TextView txtPoints;
    TextView txtStatus;

    public PlayerViewHolder(@NonNull View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txtName);
        txtPoints = itemView.findViewById(R.id.txtPoints);
        txtStatus = itemView.findViewById(R.id.txtStatus);
    }
}
