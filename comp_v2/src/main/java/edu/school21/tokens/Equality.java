package edu.school21.tokens;

import edu.school21.types.Type;

public class Equality extends Token {

    public Equality() {
        this.type = Type.EQUALITY;
    }

    @Override
    public String getToken() {
        return "=";
    }
}
