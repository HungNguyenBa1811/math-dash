package ptit.e23cnpm01.nguyenbahung.mathdash.entity;

import java.util.ArrayList;
import java.util.List;

public class MathProblem {
    private int id;
    private int operand1;
    private int operand2;
    private String operator;
    private int correctAnswer;
    private List<Integer> answerOptions = new ArrayList<>();

    public MathProblem() {
    }

    public MathProblem(int operand1, int operand2, String operator, int correctAnswer, List<Integer> answerOptions) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operator = operator;
        this.correctAnswer = correctAnswer;
        this.answerOptions = answerOptions;
    }

    public boolean isCorrect(int answer) {
        return answer == correctAnswer;
    }

    // Phép trừ hiện bằng dấu "−" cho dễ đọc; operator vẫn lưu là "-"
    public String getExpression() {
        String sign = "-".equals(operator) ? "−" : operator;
        return operand1 + " " + sign + " " + operand2 + " = ?";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOperand1() {
        return operand1;
    }

    public void setOperand1(int operand1) {
        this.operand1 = operand1;
    }

    public int getOperand2() {
        return operand2;
    }

    public void setOperand2(int operand2) {
        this.operand2 = operand2;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<Integer> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(List<Integer> answerOptions) {
        this.answerOptions = answerOptions;
    }
}
