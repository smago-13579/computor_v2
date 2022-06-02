package edu.school21.tokens;

import edu.school21.types.Type;

public class Question extends Token {
    public Question() {
        this.type = Type.QUESTION;
    }

    @Override
    public String getToken() {
        return "?";
    }
}
