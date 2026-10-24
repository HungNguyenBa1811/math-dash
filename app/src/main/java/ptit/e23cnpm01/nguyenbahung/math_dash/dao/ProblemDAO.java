package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;

public class ProblemDAO {
    private static final int OPTION_COUNT = 4;
    private static final int MAX_OFFSET = 10;

    private final Random random;

    public ProblemDAO() {
        this(new Random());
    }

    public ProblemDAO(Random random) {
        this.random = random;
    }

    // Mức 1: + - (1–10); mức 2: + - (1–30); mức 3: + - (1–50), × (2–9); mức 4: + - (1–100), × (2–12)
    public MathProblem generateProblem(int level) {
        String operator = pickOperator(level);
        if (operator.equals("×")) {
            int max = level == 3 ? 9 : 12;
            int a = nextInt(2, max);
            int b = nextInt(2, max);
            return new MathProblem(a, b, operator, a * b, createOptions(a * b));
        }
        int max = level == 1 ? 10 : level == 2 ? 30 : level == 3 ? 50 : 100;
        int a = nextInt(1, max);
        int b = nextInt(1, max);
        if (operator.equals("-") && a < b) {
            // Đổi chỗ để kết quả phép trừ không âm
            int tmp = a;
            a = b;
            b = tmp;
        }
        int answer = operator.equals("+") ? a + b : a - b;
        return new MathProblem(a, b, operator, answer, createOptions(answer));
    }

    // Mức 1, 2 chỉ có + -; từ mức 3 có thêm ×
    private String pickOperator(int level) {
        String[] operators = level >= 3 ? new String[]{"+", "-", "×"} : new String[]{"+", "-"};
        return operators[random.nextInt(operators.length)];
    }

    // 4 phương án khác nhau, không âm, có đáp án đúng, đã xáo trộn
    private List<Integer> createOptions(int answer) {
        List<Integer> options = new ArrayList<>();
        options.add(answer);
        while (options.size() < OPTION_COUNT) {
            int candidate = answer + nextInt(-MAX_OFFSET, MAX_OFFSET);
            if (candidate >= 0 && !options.contains(candidate)) {
                options.add(candidate);
            }
        }
        Collections.shuffle(options, random);
        return options;
    }

    // Số nguyên ngẫu nhiên trong đoạn [min, max]
    private int nextInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
