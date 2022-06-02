package edu.school21.actions;

import edu.school21.data.Data;
import edu.school21.exceptions.InvalidPowerException;
import edu.school21.tokens.Token;

import java.util.List;

public class MathUtils {
    private static Data data = Data.getInstance();

    public static List<Token> calculateOnePart(List<Token> tokens) {

        return null;
    }

    public static float power(float f, int i) {
        if (i < 0) {
            throw new InvalidPowerException(i);
        }

        if (i == 0) {
            return 1;
        }

        while (i-- > 1) {
            f *= f;
        }
        return f;
    }

}
