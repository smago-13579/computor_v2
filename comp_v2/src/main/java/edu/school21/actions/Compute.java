package edu.school21.actions;

import edu.school21.analyze.Checker;
import edu.school21.data.Data;
import edu.school21.exceptions.VariableNotFoundException;
import edu.school21.tokens.*;
import edu.school21.tokens.Number;
import edu.school21.types.Type;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Compute {
    private static final Compute compute = new Compute();
    private final Data data = Data.getInstance();
    private final Checker checker = Checker.getInstance();
    private final Assignment assignment = Assignment.getInstance();
    private Printable token;

    private Compute() {}

    public static Compute getInstance() {
        return compute;
    }

    public void compute(List<Token> left, List<Token> right) {
        token = new Variable("TmpTokenForPrintValueOnly");

        if (right.size() == 1) {
            List<Token> value = assignAndCalculate(left, false);
            token.setValue(value);
        } else {
            List<Token> value = new LinkedList<>();

            value.addAll(left);
            value.add(new Operator("-"));
            value.add(new Operator("("));

            for (int i = 0; i < right.size() - 1; i++) {
                value.add(right.get(i));
            }
            value.add(new Operator(")"));
            value = assignAndCalculate(value, true);

            token.setValue(value);
            print(token);

            checker.checkTokensForPolynomial(value);
            token.setValue(value);
        }
        print(token);
    }

    private List<Token> assignAndCalculate(List<Token> tokens, boolean equation) {
        List<Token> tmpTokens = new LinkedList<>();

        for (Token t : tokens) {
            if (t.getType() == Type.VARIABLE) {
                Variable var = data.getVariable(t.getToken());
                tmpTokens.add(new Operator("("));
                tmpTokens.addAll(var.getCopyValue());
                tmpTokens.add(new Operator(")"));
            } else if (t.getType() == Type.FUNCTION) {
                Function func = data.getFunction(((Function)t).getName());
                List<Token> value = func.getCopyValue();
                String varName = ((Function)t).getMemberName();

                if (varName.matches("-?\\d+(\\.\\d+)?")) {
                    value = assignment.injectNumberInsideFunction((Function) t, value);
                } else if (!equation || data.getVariable(varName) != null) {
                    value = assignment.injectVariableInsideFunctionThenCalculate(value, varName);
                } else {
                    value.stream().filter(t2 -> t2.getType() == Type.MEMBER && !((Member)t2).isImaginary())
                            .forEach(t2 -> ((Member)t2).setName(varName));
                }
                tmpTokens.add(new Operator("("));
                tmpTokens.addAll(value);
                tmpTokens.add(new Operator(")"));
            } else {
                tmpTokens.add(t);
            }
        }
        List<Token> value = MathUtils.calculateOnePart(tmpTokens);

        return value;
    }

    public void print(Printable token) {
        System.out.println(token.getValueToString());
    }

    public Printable getToken() {
        return token;
    }
}
