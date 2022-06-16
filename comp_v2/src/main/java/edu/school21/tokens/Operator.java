package edu.school21.tokens;

import edu.school21.types.Mark;
import edu.school21.types.Type;

public class Operator extends Token {
    private Mark mark;

    public Operator(char c) {
        this.type = Type.OPERATOR;

        if (c == '+') {
            this.mark = Mark.PLUS;
        } else if (c == '-') {
            this.mark = Mark.MINUS;
        } else if (c == '*') {
            this.mark = Mark.MULTIPLY;
        } else if (c == '/') {
            this.mark = Mark.DIVIDE;
        } else if (c == '%') {
            this.mark = Mark.MODULO;
        } else if (c == '^') {
            this.mark = Mark.POWER;
        } else if (c == '(') {
            this.mark = Mark.OPEN_PARENTHESIS;
        } else if (c == ')') {
            this.mark = Mark.CLOSE_PARENTHESIS;
        }
    }

    public Mark getMark() {
        return mark;
    }

    @Override
    public String getToken() {
        return this.mark + " ";
    }
}
