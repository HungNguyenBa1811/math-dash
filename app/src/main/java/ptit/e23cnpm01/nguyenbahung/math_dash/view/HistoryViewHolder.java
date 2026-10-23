package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;

/**
 * Giữ 4 TextView của một dòng lịch sử đấu.
 */
public class HistoryViewHolder extends RecyclerView.ViewHolder {
    TextView txtPlayedAt;
    TextView txtOpponent;
    TextView txtScore;
    TextView txtResult;

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        txtPlayedAt = itemView.findViewById(R.id.txtPlayedAt);
        txtOpponent = itemView.findViewById(R.id.txtOpponent);
        txtScore = itemView.findViewById(R.id.txtScore);
        txtResult = itemView.findViewById(R.id.txtResult);
    }
}
