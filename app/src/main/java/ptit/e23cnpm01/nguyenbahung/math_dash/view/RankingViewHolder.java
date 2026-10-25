package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;

public class RankingViewHolder extends RecyclerView.ViewHolder {
    TextView txtRank;
    TextView txtName;
    TextView txtPoints;
    TextView txtAvgOpponent;
    TextView txtAvgWinTime;

    public RankingViewHolder(@NonNull View itemView) {
        super(itemView);
        txtRank = itemView.findViewById(R.id.txtRank);
        txtName = itemView.findViewById(R.id.txtName);
        txtPoints = itemView.findViewById(R.id.txtPoints);
        txtAvgOpponent = itemView.findViewById(R.id.txtAvgOpponent);
        txtAvgWinTime = itemView.findViewById(R.id.txtAvgWinTime);
    }
}
