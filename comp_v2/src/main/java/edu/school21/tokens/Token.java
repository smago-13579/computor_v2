package edu.school21.tokens;

import edu.school21.types.Type;

import java.util.List;

public abstract class Token {
    protected Type type;
    protected float num;

    protected Token() {

    }

    protected Token(float f) {
        this.num = f;
        this.type = Type.NUMBER;
    }

    public Type getType() {
        return type;
    }

    public float getNum() {
        return num;
    }

    public void setNum(float num) {
        this.num = num;
    }

    public void setNegative() {
        this.num = -this.num;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public abstract String getToken();

    public static void printTokens(List<Token> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i).getToken() + " ");
        }
        System.out.println();
    }

    public static String getTokens(List<Token> tokens) {
        StringBuilder value = new StringBuilder();

        for (Token token : tokens) {
            value.append(token.getToken()).append(" ");
        }
        return value.toString();
    }

    public static String getTokens(List<Token> left, List<Token> right) {
        StringBuilder value = new StringBuilder();

        for (Token token : left) {
            value.append(token.getToken()).append(" ");
        }
        value.append("= ");

        for (Token token : right) {
            value.append(token.getToken()).append(" ");
        }
        return value.toString();
    }
}