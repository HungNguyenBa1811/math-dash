package ptit.e23cnpm01.nguyenbahung.mathdash.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

// One player's answer to one question of a match
public class Answer {
    public static final int NONE = -1;

    private int id;
    // NONE means left blank
    private int selectedOption = NONE;
    // Milliseconds from showing the question to the pick
    private long answerTime;
    // Not sent in JSON
    private MathProblem problem;

    public Answer() {
    }

    public Answer(int selectedOption, long answerTime) {
        this.selectedOption = selectedOption;
        this.answerTime = answerTime;
    }

    @JsonIgnore
    public boolean isAnswered() {
        return selectedOption != NONE;
    }

    @JsonIgnore
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

    @JsonIgnore
    public MathProblem getProblem() {
        return problem;
    }

    @JsonIgnore
    public void setProblem(MathProblem problem) {
        this.problem = problem;
    }
}
