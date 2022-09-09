package edu.school21.actions;

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
    private Printable token;

    private Compute() {}

    public static Compute getInstance() {
        return compute;
    }

    public void compute(List<Token> left, List<Token> right) {
        if (right.size() == 1) {
            token = new Variable("TmpTokenForPrintValueOnly");
            List<Token> value = assignAndCalculate(left);
            token.setValue(value);
            print(token);
        }
    }

    private List<Token> assignAndCalculate(List<Token> tokens) {
        List<Token> newRight = new LinkedList<>();

        for (Token t : tokens) {
            if (t.getType() == Type.VARIABLE) {
                Variable var = data.getVariable(t.getToken());
                newRight.add(new Operator("("));
                newRight.addAll(var.getCopyValue());
                newRight.add(new Operator(")"));
            } else if (t.getType() == Type.FUNCTION) {
                Function func = data.getFunction(((Function)t).getName());
                List<Token> value = func.getCopyValue();
                String varName = ((Function)t).getMemberName();

                if (varName.matches("-?\\d+(\\.\\d+)?")) {
                    float num = Float.parseFloat(((Function)t).getMemberName());
                    value = value.stream().map(token -> {
                        if (token.getType() == Type.MEMBER && !((Member)token).isImaginary()) {
                            Member member = (Member)token;
                            float f = Power.power(num, member.getPower()) * member.getNum();
                            return new Number(f);
                        }
                        return token;
                    }).collect(Collectors.toList());
                } else {
                    List<Token> nValue = new LinkedList<>();
                    Optional<Variable> var = data.getVariables().stream()
                            .filter(v -> v.getToken().equalsIgnoreCase(varName))
                            .findAny();

                    if (var.isEmpty()) {
                        throw new VariableNotFoundException(varName);
                    }
                    value.forEach(token -> {
                        if (token.getType() == Type.MEMBER && !((Member)token).isImaginary()) {
                            nValue.addAll(MathUtils.injectValueAndCalculate(var.get().getCopyValue(), token));
                        } else {
                            nValue.add(token);
                        }
                    });
                    value = nValue;
                }
                newRight.add(new Operator("("));
                newRight.addAll(value);
                newRight.add(new Operator(")"));
            } else {
                newRight.add(t);
            }
        }
        List<Token> value = MathUtils.calculateOnePart(newRight);

        return value;
    }

    public void print(Printable token) {
        System.out.println(token.getValueToString());
    }

    public Printable getToken() {
        return token;
    }
}
