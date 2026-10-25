package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;

/**
 * Danh sách câu hỏi trắc nghiệm; giữ phương án người chơi chọn cho từng câu (-1 là chưa chọn).
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionViewHolder>
        implements RadioGroup.OnCheckedChangeListener {
    private List<MathProblem> listQuestion = new ArrayList<>();
    private int[] answers = new int[0];
    private boolean locked;

    public void setQuestions(List<MathProblem> questions) {
        listQuestion = questions;
        answers = new int[questions.size()];
        Arrays.fill(answers, -1);
        notifyDataSetChanged();
    }

    // Bài làm theo thứ tự câu
    public List<Integer> getAnswers() {
        List<Integer> list = new ArrayList<>();
        for (int answer : answers) {
            list.add(answer);
        }
        return list;
    }

    // Khóa mọi phương án sau khi nộp bài
    public void setLocked(boolean locked) {
        this.locked = locked;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        MathProblem problem = listQuestion.get(position);
        holder.txtExpression.setText((position + 1) + ".  " + problem.getExpression());
        // Gỡ listener trước khi đặt lại trạng thái chọn, để không ghi nhầm đáp án khi tái sử dụng dòng
        holder.rgOptions.setOnCheckedChangeListener(null);
        holder.rgOptions.clearCheck();
        holder.rgOptions.setTag(position);
        for (int i = 0; i < holder.rbOptions.length; i++) {
            int option = problem.getAnswerOptions().get(i);
            holder.rbOptions[i].setText(String.valueOf(option));
            holder.rbOptions[i].setEnabled(!locked);
            holder.rbOptions[i].setChecked(answers[position] == option);
        }
        holder.rgOptions.setOnCheckedChangeListener(this);
    }

    // Người chơi chọn một phương án: lưu giá trị phương án vào câu đó
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton selected = group.findViewById(checkedId);
        if (selected == null || group.getTag() == null) {
            return;
        }
        answers[(Integer) group.getTag()] = Integer.parseInt(selected.getText().toString());
    }

    @Override
    public int getItemCount() {
        return listQuestion.size();
    }
}
