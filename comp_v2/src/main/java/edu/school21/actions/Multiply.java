package edu.school21.actions;

import edu.school21.exceptions.InvalidFormException;
import edu.school21.exceptions.UnknownFormatException;
import edu.school21.tokens.*;
import edu.school21.tokens.Number;
import edu.school21.types.Type;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Multiply {
    public static List<Token> calculate(List<Token> left, List<Token> right) {
        List<Token> tokens = new LinkedList<>();

        for (Token t : left) {
            List<Token> tmp = calculate(right, t);

            if (tmp == null) {
                return null;
            }
            tokens.addAll(tmp);
        }
        tokens = MathUtils.addition(tokens);
        return tokens;
    }

    public static List<Token> calculate(List<Token> tokens, Token token) {
        List<Token> tmp = new LinkedList<>();

        if (token.getType() == Type.OPERATOR) {
            return null;
        }

        if (token.getType() != Type.NUMBER && !tokens.stream().allMatch(t -> t.getType() == Type.NUMBER)) {
            if (tokens.stream().filter(t -> t.getType() == Type.MEMBER).anyMatch(t -> ((Member)t).isImaginary())
                    && tokens.stream().filter(t -> t.getType() == Type.MEMBER).anyMatch(t -> !((Member)t).isImaginary())) {
                return null;
            }

            if (((Member)token).isImaginary() != ((Member)tokens.stream().filter(t -> t.getType() == Type.MEMBER)
                    .findAny().get()).isImaginary()) {
                return null;
            }
        }

        for (Token t : tokens) {
            tmp.add(multiply(t, token));
        }
        tmp = MathUtils.simplifyNumbers(tmp);
        return tmp;
    }

    public static Token multiply(Token t1, Token t2) {
        if (t1.getType() == Type.NUMBER && t2.getType() == Type.NUMBER) {
            return new Number(t1.getNum() * t2.getNum());
        }

        if (t1.getType() == Type.MATRIX || t2.getType() == Type.MATRIX) {
            if (t1.getType() == Type.MATRIX && t2.getType() == Type.MATRIX) {
                return multiplyMatrix((Matrix) t1, (Matrix) t2);
            }

            if ((t1.getType() == Type.MATRIX && t2.getNum() % 1 != 0)
                    || (t2.getType() == Type.MATRIX && t1.getNum() % 1 != 0)) {
                throw new InvalidFormException("Matrix can only be multiplied by integer values");
            }
            List<List<Integer>> newMatrix = new ArrayList<>();
            List<List<Integer>> matrix;
            int num;

            if (t1.getType() == Type.MATRIX) {
                matrix = ((Matrix) t1).getMatrix();
                num = (int)t2.getNum();
            } else {
                matrix = ((Matrix) t2).getMatrix();
                num = (int)t1.getNum();
            }

            for (List<Integer> list : matrix) {
                List<Integer> newList = new ArrayList<>();

                for (Integer i : list) {
                    newList.add(i * num);
                }
                newMatrix.add(newList);
            }
            return new Matrix(newMatrix);
        }

        if (t1.getType() == Type.NUMBER) {
            Member member = new Member((Member)t2);
            member.setNum(t1.getNum() * t2.getNum());
            return member;
        }

        if (t2.getType() == Type.NUMBER) {
            Member member = new Member((Member)t1);
            member.setNum(t1.getNum() * t2.getNum());
            return member;
        }

        if ((!((Member)t1).isImaginary() && !((Member)t2).isImaginary())
                || (((Member)t1).isImaginary() && ((Member)t2).isImaginary())) {
            Member member = new Member((Member)t1);
            member.setNum(t1.getNum() * t2.getNum());
            member.setPower(((Member) t1).getPower() + ((Member) t2).getPower());

            if (member.getPower() == 0) {
                return new Number(member.getNum());
            }
            return member;
        }

        throw new UnknownFormatException("Unknown format in MathUtils.multiply() : " + t1.getToken()
                + " and " + t2.getToken());
    }

    public static Matrix multiplyMatrix(Matrix m1, Matrix m2) {
        List<List<Integer>> matrixOne = m1.getMatrix();
        List<List<Integer>> matrixTwo = m2.getMatrix();

        if (matrixOne.get(0).size() != matrixTwo.size()) {
            throw new UnknownFormatException("Matrices can't be multiplied. Column and row size are not equal : "
                    + m1.getToken() + " and " + m2.getToken());
        }
        ArrayList<List<Integer>> matrixNew = new ArrayList<>();

        for (List<Integer> listOne : matrixOne) {
            ArrayList<Integer> listNew = new ArrayList<>();

            for (int j = 0; j < matrixTwo.get(0).size(); j++) {
                int result = 0;

                for (int k = 0; k < listOne.size(); k++) {
                    result += listOne.get(k) * matrixTwo.get(k).get(j);
                }
                listNew.add(result);
            }
            matrixNew.add(listNew);
        }
        return new Matrix(matrixNew);
    }
}
