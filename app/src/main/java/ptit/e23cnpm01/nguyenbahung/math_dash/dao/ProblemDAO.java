package ptit.e23cnpm01.nguyenbahung.math_dash.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;

// Giống hệt ProblemDAO phía server
public class ProblemDAO {
    private static final String[] OPERATORS = {"+", "-", "×"};
    private static final int OPTION_COUNT = 4;
    private static final int MAX_OFFSET = 10;

    private final Random random;

    public ProblemDAO() {
        this(new Random());
    }

    public ProblemDAO(Random random) {
        this.random = random;
    }

    public List<MathProblem> generateProblems(int count) {
        List<MathProblem> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(generateProblem());
        }
        return list;
    }

    // + -: toán hạng 1–99, kết quả không âm; ×: toán hạng 2–15
    public MathProblem generateProblem() {
        String operator = OPERATORS[random.nextInt(OPERATORS.length)];
        if (operator.equals("×")) {
            int a = nextInt(2, 15);
            int b = nextInt(2, 15);
            return new MathProblem(a, b, operator, a * b, createOptions(a * b));
        }
        int a = nextInt(1, 99);
        int b = nextInt(1, 99);
        if (operator.equals("-") && a < b) {
            int tmp = a;
            a = b;
            b = tmp;
        }
        int answer = operator.equals("+") ? a + b : a - b;
        return new MathProblem(a, b, operator, answer, createOptions(answer));
    }

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

    private int nextInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
