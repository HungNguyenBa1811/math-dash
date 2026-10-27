package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;

public class ProblemViewHolder extends RecyclerView.ViewHolder {
    TextView txtIndex;
    TextView txtExpression;

    public ProblemViewHolder(@NonNull View itemView) {
        super(itemView);
        txtIndex = itemView.findViewById(R.id.txtIndex);
        txtExpression = itemView.findViewById(R.id.txtExpression);
    }
}
