package ptit.e23cnpm01.nguyenbahung.math_dash.entity;

// Bài làm một câu của một người chơi trong trận
public class Answer {
    public static final int NONE = -1;

    private int id;
    // NONE là bỏ trống
    private int selectedOption = NONE;
    // Mili giây từ lúc câu hiện ra tới lúc chọn
    private long answerTime;
    private MathProblem problem;

    public Answer() {
    }

    public Answer(int selectedOption, long answerTime) {
        this.selectedOption = selectedOption;
        this.answerTime = answerTime;
    }

    public boolean isAnswered() {
        return selectedOption != NONE;
    }

    public boolean isCorrect() {
        return problem != null && problem.isCorrect(selectedOption);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public long getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(long answerTime) {
        this.answerTime = answerTime;
    }

    public MathProblem getProblem() {
        return problem;
    }

    public void setProblem(MathProblem problem) {
        this.problem = problem;
    }
}
