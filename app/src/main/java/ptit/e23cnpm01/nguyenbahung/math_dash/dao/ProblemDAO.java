package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import java.util.Random;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;

/**
 * Sinh câu hỏi toán ngẫu nhiên.
 * Nhận Random từ ngoài vào: 2 máy dùng chung seed sẽ sinh ra cùng một dãy câu.
 */
public class ProblemDAO {
    private final Random random;

    public ProblemDAO() {
        this(new Random());
    }

    public ProblemDAO(Random random) {
        this.random = random;
    }

    // Sinh một câu hỏi theo độ khó level
    public MathProblem generateProblem(int level) {
        // TODO
        return null;
    }
}
