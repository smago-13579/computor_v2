package edu.school21.tokens;

import edu.school21.types.Type;

import java.util.List;

public class Function extends Token {
    private String name;
    private String memberName;
    private List<Token> value;

    public Function(String name) {
        String[] names = name.split("\\(");
        this.name = names[0];
        this.memberName = names[1].split("\\)")[0];
        this.type = Type.FUNCTION;
    }

    public String getName() {
        return name;
    }

    public String getMemberName() {
        return memberName;
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

            if (i + 1 < value.size() && value.get(i + 1).getNum() >= 0) {
                builder.append(" + ");
            }
        }
        return builder.toString();
    }

    @Override
    public String getToken() {
        return this.name + "(" + this.memberName + ")";
    }
}
