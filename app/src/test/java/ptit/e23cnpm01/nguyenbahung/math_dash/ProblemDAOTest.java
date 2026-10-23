package ptit.e23cnpm01.nguyenbahung.math_dash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashSet;
import java.util.Random;

import ptit.e23cnpm01.nguyenbahung.math_dash.dao.ProblemDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;

/**
 * Câu hỏi sinh ra hợp lệ theo mức, và 2 máy cùng seed sinh cùng một dãy câu.
 */
public class ProblemDAOTest {

    @Test
    public void moiMucSinh1000CauHopLe() {
        ProblemDAO problemDAO = new ProblemDAO(new Random(7));
        for (int level = 1; level <= 4; level++) {
            for (int i = 0; i < 1000; i++) {
                assertValid(problemDAO.generateProblem(level), level);
            }
        }
    }

    @Test
    public void cungSeedSinhCungDayCau() {
        ProblemDAO a = new ProblemDAO(new Random(482915L));
        ProblemDAO b = new ProblemDAO(new Random(482915L));
        for (int i = 0; i < 30; i++) {
            int level = Math.min(1 + i / 5, 4);
            MathProblem p = a.generateProblem(level);
            MathProblem q = b.generateProblem(level);
            assertEquals(p.getExpression(), q.getExpression());
            assertEquals(p.getAnswerOptions(), q.getAnswerOptions());
        }
    }

    private void assertValid(MathProblem p, int level) {
        String op = p.getOperator();
        assertTrue(level >= 3 || !op.equals("×"));
        int expected = op.equals("+") ? p.getOperand1() + p.getOperand2()
                : op.equals("-") ? p.getOperand1() - p.getOperand2() : p.getOperand1() * p.getOperand2();
        assertEquals(expected, p.getCorrectAnswer());
        assertTrue(p.getCorrectAnswer() >= 0 && p.getCorrectAnswer() <= 200);
        int max = op.equals("×") ? (level == 3 ? 9 : 12) : new int[]{0, 10, 30, 50, 100}[level];
        int min = op.equals("×") ? 2 : 1;
        assertTrue(p.getOperand1() >= min && p.getOperand1() <= max);
        assertTrue(p.getOperand2() >= min && p.getOperand2() <= max);
        assertEquals(4, p.getAnswerOptions().size());
        assertEquals(4, new HashSet<>(p.getAnswerOptions()).size());
        assertTrue(p.getAnswerOptions().contains(p.getCorrectAnswer()));
        for (int option : p.getAnswerOptions()) {
            assertTrue(option >= 0);
        }
    }
}
