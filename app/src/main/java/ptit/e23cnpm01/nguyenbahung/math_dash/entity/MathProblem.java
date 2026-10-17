package ptit.e23cnpm01.nguyenbahung.math_dash.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Entity một câu hỏi toán: 2 toán hạng, toán tử, đáp án đúng và 4 lựa chọn.
 */
public class MathProblem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int operand1;
    private int operand2;
    private String operator; // "+", "-", "×"
    private int correctAnswer;
    private List<Integer> options;

    // TODO: constructor rỗng + constructor đầy đủ tham số
    // TODO: getter / setter

    // Trả về chuỗi hiển thị, ví dụ "12 + 7"
    public String getExpression() {
        // TODO
        return "";
    }

    // Kiểm tra đáp án người chơi chọn có đúng không
    public boolean isCorrect(int candidate) {
        // TODO
        return false;
    }
}
