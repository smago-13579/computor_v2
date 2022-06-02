package edu.school21.exceptions;

public class VariableNotFoundException extends RuntimeException {
    public VariableNotFoundException(String var) {
        super("Variable: \"" + var + "\" not found");
    }
}
