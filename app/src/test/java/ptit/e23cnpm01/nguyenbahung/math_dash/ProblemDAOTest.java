package ptit.e23cnpm01.nguyenbahung.math_dash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import ptit.e23cnpm01.nguyenbahung.math_dash.dao.ProblemDAO;
import ptit.e23cnpm01.nguyenbahung.math_dash.entity.MathProblem;

public class ProblemDAOTest {

    @Test
    public void sinh1000CauHopLe() {
        ProblemDAO problemDAO = new ProblemDAO(new Random(7));
        for (int i = 0; i < 1000; i++) {
            assertValid(problemDAO.generateProblem());
        }
    }

    @Test
    public void deCoDungSoCau() {
        List<MathProblem> list = new ProblemDAO(new Random(37)).generateProblems(10);
        assertEquals(10, list.size());
    }

    @Test
    public void bieuThucCoDauHoi() {
        MathProblem p = new ProblemDAO(new Random(1)).generateProblem();
        assertTrue(p.getExpression().endsWith(" = ?"));
    }

    private void assertValid(MathProblem p) {
        String op = p.getOperator();
        assertTrue(op.equals("+") || op.equals("-") || op.equals("×"));
        int expected = op.equals("+") ? p.getOperand1() + p.getOperand2()
                : op.equals("-") ? p.getOperand1() - p.getOperand2() : p.getOperand1() * p.getOperand2();
        assertEquals(expected, p.getCorrectAnswer());
        assertTrue(p.getCorrectAnswer() >= 0);
        List<Integer> options = p.getAnswerOptions();
        assertEquals(4, options.size());
        assertEquals(4, new HashSet<>(options).size());
        assertTrue(options.contains(p.getCorrectAnswer()));
        for (int option : options) {
            assertTrue(option >= 0);
        }
    }
}
