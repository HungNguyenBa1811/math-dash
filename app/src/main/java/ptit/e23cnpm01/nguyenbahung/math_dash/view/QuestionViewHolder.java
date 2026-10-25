package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;

public class QuestionViewHolder extends RecyclerView.ViewHolder {
    TextView txtExpression;
    RadioGroup rgOptions;
    RadioButton[] rbOptions = new RadioButton[4];

    public QuestionViewHolder(@NonNull View itemView) {
        super(itemView);
        txtExpression = itemView.findViewById(R.id.txtExpression);
        rgOptions = itemView.findViewById(R.id.rgOptions);
        rbOptions[0] = itemView.findViewById(R.id.rbOption1);
        rbOptions[1] = itemView.findViewById(R.id.rbOption2);
        rbOptions[2] = itemView.findViewById(R.id.rbOption3);
        rbOptions[3] = itemView.findViewById(R.id.rbOption4);
    }
}
