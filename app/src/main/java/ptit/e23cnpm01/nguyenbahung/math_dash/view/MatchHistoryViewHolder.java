package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;

public class MatchHistoryViewHolder extends RecyclerView.ViewHolder {
    View viewResultBar;
    TextView txtOpponent;
    TextView txtResult;
    TextView txtPoints;
    TextView txtScore;
    TextView txtEndedAt;

    public MatchHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        viewResultBar = itemView.findViewById(R.id.viewResultBar);
        txtOpponent = itemView.findViewById(R.id.txtOpponent);
        txtResult = itemView.findViewById(R.id.txtResult);
        txtPoints = itemView.findViewById(R.id.txtPoints);
        txtScore = itemView.findViewById(R.id.txtScore);
        txtEndedAt = itemView.findViewById(R.id.txtEndedAt);
    }
}
