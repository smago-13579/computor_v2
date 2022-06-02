package edu.school21.exceptions;

public class FunctionNotFoundException extends RuntimeException {
    public FunctionNotFoundException(String func) {
        super("Function: \"" + func + "\" not found");
    }
}
