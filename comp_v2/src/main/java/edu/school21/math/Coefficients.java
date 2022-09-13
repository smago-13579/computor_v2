package edu.school21.math;

import edu.school21.tokens.Member;
import edu.school21.tokens.Token;
import edu.school21.types.Type;

import java.util.ArrayList;
import java.util.List;

public class Coefficients {
    public static List<Float> getCoefficients(List<Token> list) {
        List<Float> coeffs = new ArrayList<>();
        float a = 0, b = 0, c = 0, disc;

        for (Token token : list) {
            if (token.getType() == Type.MEMBER) {
                if (((Member)token).getPower() == 2) {
                    a = token.getNum();
                } else {
                    b = token.getNum();
                }
            } else {
                c = token.getNum();
            }
        }
        disc = b * b - 4 * a * c;
        coeffs.add(disc);
        coeffs.add(a);
        coeffs.add(b);
        coeffs.add(c);

        return coeffs;
    }
}
