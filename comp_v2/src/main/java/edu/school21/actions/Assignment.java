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

public class Assignment {
    private static final Assignment assignment = new Assignment();
    private final Data data = Data.getInstance();

    private Assignment() {}

    public static Assignment getInstance() {
        return assignment;
    }

    public void assign(List<Token> left, List<Token> right) {
        List<Token> newRight = new LinkedList<>();

        right.forEach(t -> {
            if (t.getType() == Type.VARIABLE) {
                Variable var = data.getVariables().stream()
                        .filter(v -> v.getToken().equalsIgnoreCase(t.getToken())).findAny().get();
                newRight.add(new Operator('('));
                newRight.addAll(var.getValue());
                newRight.add(new Operator(')'));
            } else if (t.getType() == Type.FUNCTION) {
                Function func = data.getFunctions().stream()
                        .filter(f -> f.getName().equalsIgnoreCase(((Function)t).getName())).findAny().get();
                List<Token> value = func.getValue();
                String varName = ((Function)t).getMemberName();

                if (varName.matches("-?\\d+(\\.\\d+)?")) {
                    float num = Float.parseFloat(((Function)t).getMemberName());
                    value = value.stream().map(token -> {
                        if (token.getType() == Type.MEMBER && !((Member)token).isImaginary()) {
                            Member member = (Member)token;
                            float f = MathUtils.power(num, member.getPower()) * member.getNum();
                            return new Number(f);
                        }
                        return token;
                    }).collect(Collectors.toList());
                } else if (left.get(0).getType() == Type.VARIABLE
                        || !((Function)left.get(0)).getMemberName().equalsIgnoreCase(varName)) {
                    List<Token> nValue = new LinkedList<>();
                    Optional<Variable> var = data.getVariables().stream()
                            .filter(v -> v.getToken().equalsIgnoreCase(varName))
                            .findAny();

                    if (var.isEmpty()) {
                        throw new VariableNotFoundException(varName);
                    }
                    value.forEach(token -> {
                        if (token.getType() == Type.MEMBER && !((Member)token).isImaginary()) {
                            nValue.addAll(MathUtils.powerAndCalculate(var.get().getValue(), token));
                        }
                        nValue.add(token);
                    });
                    value = nValue;
                    //TODO calculate this part
                }
                //TODO varB = f(varA) + 10;
                newRight.add(new Operator('('));
                newRight.addAll(value);
                newRight.add(new Operator(')'));
            } else {
                newRight.add(t);
            }
        });
        if (left.get(0).getType() == Type.VARIABLE) {
            assignToVariable((Variable)left.get(0), newRight);
        }

        if (left.get(0).getType() == Type.FUNCTION) {
            assignToFunction((Function)left.get(0), newRight);
        }
    }

    private void assignToVariable(Variable var, List<Token> right) {
        List<Token> value = MathUtils.calculateOnePart(right);

        if (data.getVariables().stream().noneMatch(v -> v.getToken().equalsIgnoreCase(var.getToken()))) {
            var.setValue(value);
            data.addVariable(var);
        } else {
            data.getVariables().forEach(v -> {
                if (v.getToken().equalsIgnoreCase(var.getToken())) {
                    v.setValue(value);
                }
            });
        }
    }

    private void assignToFunction(Function func, List<Token> right) {
        List<Token> value = MathUtils.calculateOnePart(right);

        if (data.getFunctions().stream().noneMatch(f -> f.getName().equalsIgnoreCase(func.getName()))) {
            func.setValue(value);
            data.addFunction(func);
        } else {
            data.getFunctions().forEach(f -> {
                if (f.getName().equalsIgnoreCase(func.getName())) {
                    f.setValue(value);
                }
            });
        }
    }
}
