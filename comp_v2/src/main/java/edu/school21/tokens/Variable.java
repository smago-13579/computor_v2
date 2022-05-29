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

    @Override
    public String getToken() {
        return this.name;
    }
}