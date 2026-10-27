package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;

/**
 * Một câu trong đề của trận đã xong: số thứ tự và phép tính kèm đáp án đúng, ví dụ "12 + 7 = 19".
 */
public class ProblemAdapter extends RecyclerView.Adapter<ProblemViewHolder> {
    private List<MathProblem> listProblem;

    public ProblemAdapter(List<MathProblem> problems) {
        this.listProblem = problems;
    }

    @NonNull
    @Override
    public ProblemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_problem, parent, false);
        return new ProblemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
        MathProblem p = listProblem.get(position);
        holder.txtIndex.setText(String.valueOf(position + 1));
        holder.txtExpression.setText(p.getExpression().replace("?", String.valueOf(p.getCorrectAnswer())));
    }

    @Override
    public int getItemCount() {
        return listProblem.size();
    }
}
