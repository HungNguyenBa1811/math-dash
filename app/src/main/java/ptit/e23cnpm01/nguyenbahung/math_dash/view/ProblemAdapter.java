package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Answer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MathProblem;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;

// Mỗi dòng một câu: đáp án đúng, bài làm của mình và của đối thủ
public class ProblemAdapter extends RecyclerView.Adapter<ProblemViewHolder> {
    private List<MathProblem> listProblem;
    private List<Answer> myAnswers;
    private List<Answer> opponentAnswers;

    public ProblemAdapter(List<MathProblem> problems, List<Answer> myAnswers, List<Answer> opponentAnswers) {
        this.listProblem = problems;
        this.myAnswers = myAnswers;
        this.opponentAnswers = opponentAnswers;
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
        showAnswer(holder.txtMyAnswer, answerAt(myAnswers, position));
        showAnswer(holder.txtOpponentAnswer, answerAt(opponentAnswers, position));
    }

    @Override
    public int getItemCount() {
        return listProblem.size();
    }

    // Bỏ trận khi chưa nộp thì không có bài làm
    private Answer answerAt(List<Answer> answers, int position) {
        return position < answers.size() ? answers.get(position) : null;
    }

    // "✓ 19" xanh hoặc "✗ 18" đỏ, dòng dưới là thời gian làm câu
    private void showAnswer(TextView view, Answer answer) {
        Context context = view.getContext();
        if (answer == null || !answer.isAnswered()) {
            view.setText(answer == null ? R.string.label_no_time : R.string.answer_blank);
            view.setTextColor(context.getColor(R.color.arcade_text_dim));
            return;
        }
        int text = answer.isCorrect() ? R.string.answer_correct : R.string.answer_wrong;
        int color = answer.isCorrect() ? R.color.arcade_green : R.color.arcade_red;
        String seconds = context.getString(R.string.label_seconds_short,
                PointFormat.format(Math.round(answer.getAnswerTime() / 100.0) / 10.0));
        view.setText(context.getString(text, answer.getSelectedOption()) + "\n" + seconds);
        view.setTextColor(context.getColor(color));
    }
}
