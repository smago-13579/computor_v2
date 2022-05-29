package edu.school21.exceptions;

public class InvalidFormException extends RuntimeException {
    public InvalidFormException(String str) {
        super(str);
    }
}
