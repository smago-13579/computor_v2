package edu.school21.math;

import edu.school21.data.Data;
import edu.school21.tokens.Member;
import edu.school21.tokens.Token;
import edu.school21.types.Type;

import java.util.ArrayList;
import java.util.List;

public class Maths {
    private static final Maths maths = new Maths();
    private final Data data = Data.getInstance();

    private Maths() {

    }

    public static Maths getInstance() {
        return maths;
    }

    public void solution(List<Token> list) {
        if (list.get(0).getType() == Type.MEMBER && ((Member)list.get(0)).getPower() == 2) {
            findDiscriminantAndSolve(list);
        } else if (list.get(0).getType() == Type.MEMBER) {
            solveWithoutDiscriminant(list, 1);
        } else {
            solveWithoutDiscriminant(list, 0);
        }
    }

    private void findDiscriminantAndSolve(List<Token> list) {
        List<Float> coeffs = Coefficients.getCoefficients(list);
        float disc = coeffs.get(0);

        if (disc > 0) {
            System.out.println("Discriminant is strictly positive, the two solutions are:");
            data.addHistory("Discriminant is strictly positive, the two solutions are:");
            float sqrt = SquareRoot.square(disc);
            float solve = (-1 * coeffs.get(2) + sqrt) / (2 * coeffs.get(1));
            System.out.println(solve);
            data.addHistory(String.valueOf(solve));
            solve = (-1 * coeffs.get(2) - sqrt) / (2 * coeffs.get(1));
            System.out.println(solve);
            data.addHistory(String.valueOf(solve));
        } else if (disc == 0) {
            System.out.println("Discriminant = 0, the solution is:");
            data.addHistory("Discriminant = 0, the solution is:");
            float solve = (-1 * coeffs.get(2)) / (2 * coeffs.get(1));
            System.out.println(solve);
            data.addHistory(String.valueOf(solve));
        } else {
            String str1, str2;
            System.out.println("Discriminant is strictly negative, the two complex solutions are:");
            data.addHistory("Discriminant is strictly negative, the two complex solutions are:");
            float sqrt = SquareRoot.square(-disc);
            float solve = (-1 * coeffs.get(2)) / (2 * coeffs.get(1));

            if (solve != 0) {
                str1 = solve + " + i * " + (sqrt / (2 * coeffs.get(1)));
                str2 = solve + " - i * " + (sqrt / (2 * coeffs.get(1)));
            } else {
                str1 = "i * " + (sqrt / (2 * coeffs.get(1)));
                str2 = "-i * " + (sqrt / (2 * coeffs.get(1)));
            }
            System.out.println(str1);
            System.out.println(str2);
            data.addHistory(str1);
            data.addHistory(str2);
        }
    }

    private void solveWithoutDiscriminant(List<Token> list, int i) {
        float solve;
        String str;

        if (i == 1) {
            if (list.size() > 1) {
                solve = (-1 * list.get(1).getNum()) / list.get(0).getNum();
            } else {
                solve = 0;
            }
            str = "The solution is: " + solve;
        } else if (list.get(0).getNum() == 0) {
            str = "Any real number is a solution!!!";
        } else {
            str = "There is no solution!!!";
        }
        System.out.println(str);
        data.addHistory(str);
    }
}
