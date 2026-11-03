package ptit.e23cnpm01.nguyenbahung.math_dash.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ptit.e23cnpm01.nguyenbahung.math_dash.R;
import ptit.e23cnpm01.nguyenbahung.math_dash.dao.MatchApiDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.util.PointFormat;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Answer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.Match;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MatchPlayer;
import ptit.e23cnpm01.nguyenbahung.mathdash.entity.MathProblem;

public class MatchDetailActivity extends BaseActivity {
    private Match match;
    private int matchId;
    private TextView txtTitle;
    private TextView txtResult;
    private TextView txtPoints;
    private TextView txtMyName;
    private TextView txtMyStats;
    private TextView txtOpponentName;
    private TextView txtOpponentStats;
    private TextView txtMyColumn;
    private TextView txtOpponentColumn;
    private RecyclerView rvProblems;
    private MatchApiDAO matchApiDAO = new MatchApiDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);
        txtTitle = findViewById(R.id.txtTitle);
        txtResult = findViewById(R.id.txtResult);
        txtPoints = findViewById(R.id.txtPoints);
        txtMyName = findViewById(R.id.txtMyName);
        txtMyStats = findViewById(R.id.txtMyStats);
        txtOpponentName = findViewById(R.id.txtOpponentName);
        txtOpponentStats = findViewById(R.id.txtOpponentStats);
        txtMyColumn = findViewById(R.id.txtMyColumn);
        txtOpponentColumn = findViewById(R.id.txtOpponentColumn);
        rvProblems = findViewById(R.id.rvProblems);
        rvProblems.setLayoutManager(new LinearLayoutManager(this));
        matchId = getIntent().getIntExtra(HistoryActivity.EXTRA_MATCH_ID, 0);
        txtTitle.setText(getString(R.string.detail_title, matchId));
        loadMatch();
    }

    private void loadMatch() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Match m = matchApiDAO.getById(matchId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMatch(m);
                        }
                    });
                } catch (IOException | JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MatchDetailActivity.this, R.string.msg_detail_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void showMatch(Match m) {
        match = m;
        MatchPlayer opponent = match.getOpponent(me.getId());
        // Our own row is the opponent's opponent
        MatchPlayer mine = match.getOpponent(opponent.getPlayer().getId());
        if (match.getEndedAt() != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            txtTitle.setText(getString(R.string.detail_title_time, match.getId(), format.format(match.getEndedAt())));
        }
        showResult(match.getResult(me.getId()));
        txtPoints.setText(getString(R.string.detail_points, PointFormat.format(match.getPoints(me.getId()))));
        txtMyName.setText(mine.getPlayer().getDisplayName());
        txtMyStats.setText(stats(mine));
        txtOpponentName.setText(opponent.getPlayer().getDisplayName());
        txtOpponentStats.setText(stats(opponent));
        txtMyColumn.setText(mine.getPlayer().getDisplayName());
        txtOpponentColumn.setText(opponent.getPlayer().getDisplayName());
        rvProblems.setAdapter(new ProblemAdapter(match.getQuestions(), mine.getAnswers(), opponent.getAnswers()));
    }

    private void showResult(String result) {
        int color = Match.WIN.equals(result) ? R.color.win
                : Match.DRAW.equals(result) ? R.color.draw : R.color.lose;
        int text = Match.WIN.equals(result) ? R.string.result_win
                : Match.DRAW.equals(result) ? R.string.result_draw : R.string.result_lose;
        txtResult.setText(text);
        txtResult.setTextColor(getColor(color));
    }

    // "10/10 câu · 38,2 giây", or "Bỏ trận"
    private String stats(MatchPlayer mp) {
        if (mp.isForfeited()) {
            return getString(R.string.detail_forfeited);
        }
        long time = match.getFinishTime(mp.getPlayer().getId());
        String seconds = time == Long.MAX_VALUE ? getString(R.string.label_no_time)
                : getString(R.string.label_seconds, PointFormat.formatSeconds(time / 1000.0));
        return getString(R.string.detail_stats, mp.getCorrectCount(), seconds);
    }

    // One row per question
    private static class ProblemAdapter extends RecyclerView.Adapter<ProblemViewHolder> {
        private List<MathProblem> items = new ArrayList<>();
        private List<Answer> myAnswers;
        private List<Answer> opponentAnswers;

        public ProblemAdapter(List<MathProblem> problems, List<Answer> myAnswers, List<Answer> opponentAnswers) {
            this.items = problems;
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
            MathProblem p = items.get(position);
            holder.txtIndex.setText(String.valueOf(position + 1));
            holder.txtExpression.setText(p.getExpression().replace("?", String.valueOf(p.getCorrectAnswer())));
            Answer mine = answerAt(myAnswers, position);
            Answer opponent = answerAt(opponentAnswers, position);
            showAnswer(holder.txtMyAnswer, mine);
            showAnswer(holder.txtOpponentAnswer, opponent);
        }

        // A forfeit before submitting has no answers
        private Answer answerAt(List<Answer> answers, int position) {
            return position < answers.size() ? answers.get(position) : null;
        }

        // Green "✓ 19" or red "✗ 18", with the time taken below
        private void showAnswer(TextView view, Answer answer) {
            Context context = view.getContext();
            if (answer == null || !answer.isAnswered()) {
                view.setText(answer == null ? R.string.label_no_time : R.string.answer_blank);
                view.setTextColor(context.getColor(R.color.ink_dim));
                return;
            }
            int text = answer.isCorrect() ? R.string.answer_correct : R.string.answer_wrong;
            int color = answer.isCorrect() ? R.color.win : R.color.lose;
            String seconds = context.getString(R.string.label_seconds_short,
                    PointFormat.formatSeconds(answer.getAnswerTime() / 1000.0));
            view.setText(context.getString(text, answer.getSelectedOption()) + "\n" + seconds);
            view.setTextColor(context.getColor(color));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class ProblemViewHolder extends RecyclerView.ViewHolder {
        TextView txtIndex;
        TextView txtExpression;
        TextView txtMyAnswer;
        TextView txtOpponentAnswer;

        public ProblemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIndex = itemView.findViewById(R.id.txtIndex);
            txtExpression = itemView.findViewById(R.id.txtExpression);
            txtMyAnswer = itemView.findViewById(R.id.txtMyAnswer);
            txtOpponentAnswer = itemView.findViewById(R.id.txtOpponentAnswer);
        }
    }
}
