package ptit.e23cnpm01.nguyenbahung.math_dash.entity;

import java.util.List;


public class MathProblem {
    private int operand1;
    private int operand2;
    private String operator;
    private int correctAnswer;
    private List<Integer> answerOptions;

    public MathProblem(int operand1, int operand2, String operator, int correctAnswer, List<Integer> answerOptions) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operator = operator;
        this.correctAnswer = correctAnswer;
        this.answerOptions = answerOptions;
    }

    // Chuỗi hiển thị, ví dụ "12 + 7"
    public String getExpression() {
        return operand1 + " " + operator + " " + operand2;
    }

    // Đáp án người chơi nhập / chọn có đúng không
    public boolean isCorrect(int answer) {
        return answer == correctAnswer;
    }

    public int getOperand1() {
        return operand1;
    }

    public int getOperand2() {
        return operand2;
    }

    public String getOperator() {
        return operator;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public List<Integer> getAnswerOptions() {
        return answerOptions;
    }
}
