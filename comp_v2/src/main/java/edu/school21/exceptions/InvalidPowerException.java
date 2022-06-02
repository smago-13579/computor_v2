package edu.school21.exceptions;

public class InvalidPowerException extends RuntimeException {
    public InvalidPowerException(int i) {
        super("Power can't be negative: " + i);
    }
}
