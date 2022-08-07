package edu.school21.tokens;

import edu.school21.exceptions.VariableNotFoundException;
import edu.school21.types.Mark;
import edu.school21.types.Type;

import java.util.LinkedList;
import java.util.List;

public class Variable extends Token {
    private String name;
    private List<Token> value;

    public Variable(String name) {
        this.name = name;
        this.type = Type.VARIABLE;
    }

    public List<Token> getValue() {
        return this.value;
    }

    public List<Token> getCopyValue() {
        List<Token> copy = new LinkedList<>();

        for (int i = 0; i < value.size(); i++) {
            if (value.get(i) instanceof Matrix) {
                copy.add(new Matrix((Matrix)value.get(i)));
            } else if (value.get(i) instanceof Member) {
                copy.add(new Member((Member)value.get(i)));
            } else if (value.get(i) instanceof Number) {
                copy.add(new Number((Number)value.get(i)));
            } else if (value.get(i) instanceof Operator) {
                copy.add(new Operator((Operator)value.get(i)));
            } else {
                throw new VariableNotFoundException(value.get(i).getToken());
            }
        }
        return copy;
    }

    public void setValue(List<Token> value) {
        this.value = value;
    }

    public String getValueToString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < value.size(); i++) {
            builder.append(value.get(i).getToken());

            if (i + 1 < value.size() && value.get(i).getType() != Type.OPERATOR
                    && (value.get(i + 1).getType() != Type.OPERATOR
                    || ((Operator)value.get(i + 1)).getMark() == Mark.OPEN_PARENTHESIS)
                    && value.get(i + 1).getNum() >= 0) {
                builder.append("+ ");
            }
        }
        return builder.toString();
    }

    @Override
    public String getToken() {
        return this.name;
    }
}
