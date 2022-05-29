package edu.school21.exceptions;

public class InvalidSymbolException extends RuntimeException {
    public InvalidSymbolException(String str) {
        super("Invalid symbol: " + str);
    }
}
