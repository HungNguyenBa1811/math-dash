package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;

public class AdminPlayerViewHolder extends RecyclerView.ViewHolder {
    TextView txtUsername;
    TextView txtDisplayName;
    TextView txtPoints;
    TextView txtCreatedAt;

    public AdminPlayerViewHolder(@NonNull View itemView) {
        super(itemView);
        txtUsername = itemView.findViewById(R.id.txtUsername);
        txtDisplayName = itemView.findViewById(R.id.txtDisplayName);
        txtPoints = itemView.findViewById(R.id.txtPoints);
        txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
    }
}
