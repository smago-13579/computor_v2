package edu.school21.tokens;

import edu.school21.types.Mark;
import edu.school21.types.Type;

public class Operator extends Token {
    private Mark mark;

    public Operator(String s) {
        this.type = Type.OPERATOR;

        if (s.equals("+")) {
            this.mark = Mark.PLUS;
        } else if (s.equals("-")) {
            this.mark = Mark.MINUS;
        } else if (s.equals("*")) {
            this.mark = Mark.MULTIPLY;
        } else if (s.equals("**")) {
            this.mark = Mark.MATRIX_MULTIPLY;
        } else if (s.equals("/")) {
            this.mark = Mark.DIVIDE;
        } else if (s.equals("%")) {
            this.mark = Mark.MODULO;
        } else if (s.equals("^")) {
            this.mark = Mark.POWER;
        } else if (s.equals("(")) {
            this.mark = Mark.OPEN_PARENTHESIS;
        } else if (s.equals(")")) {
            this.mark = Mark.CLOSE_PARENTHESIS;
        }
    }

    public Operator(Operator o) {
        this.type = Type.OPERATOR;
        this.mark = o.getMark();
    }

    public Mark getMark() {
        return mark;
    }

    @Override
    public String getToken() {
        return this.mark + " ";
    }
}
