package edu.school21.tokens;

import edu.school21.types.Type;

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

    public void setValue(List<Token> value) {
        this.value = value;
    }

    public String getValueToString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < value.size(); i++) {
            builder.append(value.get(i).getToken());

            if (i + 1 < value.size() && value.get(i).getType() != Type.OPERATOR
                    && value.get(i + 1).getType() != Type.OPERATOR && value.get(i + 1).getNum() >= 0) {
                builder.append(" + ");
            }
        }
        return builder.toString();
    }

    @Override
    public String getToken() {
        return this.name;
    }
}
