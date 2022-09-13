package edu.school21.analyze;

import edu.school21.data.Data;
import edu.school21.exceptions.*;
import edu.school21.tokens.Function;
import edu.school21.tokens.Member;
import edu.school21.tokens.Operator;
import edu.school21.tokens.Token;
import edu.school21.types.Mark;
import edu.school21.types.Type;

import java.util.LinkedList;
import java.util.List;

public class Checker {
    private static final Checker checker = new Checker();
    private final Data data = Data.getInstance();

    private Checker() {}

    public static Checker getInstance() {
        return checker;
    }

    public void checkVariables(List<Token> tokens) {
        tokens.stream().filter(t -> t.getType() == Type.VARIABLE).forEach(t -> {
            if (data.getVariable(t.getToken()) == null) {
                throw new VariableNotFoundException(t.getToken());
            }
        });
    }

    public void checkFunctions(List<Token> tokens) {
        tokens.stream().filter(t -> t.getType() == Type.FUNCTION).forEach(t -> {
            if (data.getFunction(((Function)t).getName()) == null) {
                throw new FunctionNotFoundException(t.getToken());
            }
        });
    }

    public void checkMembers(List<Token> left, List<Token> right, String name) {
        List<Token> list = new LinkedList<>();
        list.addAll(left);
        list.addAll(right);

        list.stream().filter(t -> t.getType() == Type.MEMBER && !((Member)t).isImaginary()).forEach(token -> {
            if (!token.getToken().equalsIgnoreCase(name)) {
                throw new InvalidFormException("Incorrect member names: " + name + "and " + token.getToken());
            }
        });
    }

    public void checkVariablesInsideFunctions(String name, List<Token> tokens) {
        tokens.stream().filter(t -> t.getType() == Type.FUNCTION).forEach(t -> {
            String varName = ((Function)t).getMemberName();

            if (!varName.matches("-?\\d+(\\.\\d+)?") && data.getVariable(varName) == null) {
                if (!name.equalsIgnoreCase(varName)) {
                    throw new InvalidFormException("Incorrect member names: " + name + " and " + varName);
                }
            }
        });
    }

    public void checkVariableNameInsideFunction(String name) {
        if (name.equalsIgnoreCase("i")) {
            throw new InvalidFormException("Incorrect variable name inside function: " + name);
        }

        for (char c : name.toCharArray()) {
            if ((c < 65 || c > 90) && (c < 97 || c > 122)) {
                throw new InvalidFormException("Incorrect variable name inside function: " + name);
            }
        }
    }

    public void checkTokensForPolynomial(List<Token> tokens) {
        tokens.stream().filter(t -> t.getType() == Type.MEMBER).forEach(t -> {
            int power = ((Member)t).getPower();

            if (power > 2 || power < 0) {
                throw new InvalidPolynomialException("Could not convert to a polynomial. Power: " + power);
            }

            if (((Member)t).isImaginary()) {
                throw new InvalidPolynomialException("Could not convert to a polynomial with imaginary number: i");
            }
        });

        tokens.stream().filter(t -> t.getType() == Type.OPERATOR).forEach(t -> {
            Mark mark = ((Operator)t).getMark();

            if (mark == Mark.MODULO || mark == Mark.MULTIPLY || mark == Mark.DIVIDE || mark == Mark.POWER) {
                throw new InvalidPolynomialException("Could not convert to a polynomial. Operator: " + mark);
            }
        });
    }
}
