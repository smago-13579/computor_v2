package edu.school21.actions;

import edu.school21.data.Data;
import edu.school21.tokens.*;
import edu.school21.tokens.Number;
import edu.school21.types.Type;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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

                if (((Function)t).getMemberName().matches("-?\\d+(\\.\\d+)?")) {
                    float num = Float.parseFloat(((Function)t).getMemberName());
                    value = value.stream().map(token -> {
                        if (token.getType() == Type.MEMBER && !((Member)token).isImaginary()) {
                            Member member = (Member)token;
                            float f = MathUtils.power(num, member.getPower()) * member.getNum();
                            return new Number(f);
                        }
                        return token;
                    }).collect(Collectors.toList());
                }
                newRight.add(new Operator('('));
                newRight.addAll(value);
                newRight.add(new Operator(')'));
            } else {
                newRight.add(t);
            }
        });
        if (left.get(0).getType() == Type.VARIABLE) {
            assignToVariable(left, right);
        }

        if (left.get(0).getType() == Type.FUNCTION) {
            assignToFunction(left, right);
        }
    }

    private void assignToVariable(List<Token> left, List<Token> right) {

    }

    private void assignToFunction(List<Token> left, List<Token> right) {
    }
}
