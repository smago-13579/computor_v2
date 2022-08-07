package edu.school21.actions;

import edu.school21.exceptions.InvalidPowerException;
import edu.school21.exceptions.UnknownFormatException;
import edu.school21.tokens.Member;
import edu.school21.tokens.Number;
import edu.school21.tokens.Operator;
import edu.school21.tokens.Token;
import edu.school21.types.Mark;
import edu.school21.types.Type;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Power {
    public static List<Token> calculate(List<Token> tokens) {
        List<Token> tmp = tokens.stream().filter(t -> t.getType() == Type.OPERATOR
                        && ((Operator) t).getMark() == Mark.POWER).collect(Collectors.toList());

        for (Token t : tmp) {
            int i = tokens.indexOf(t);
            Token token = power(tokens.get(i - 1), tokens.get(i + 1));

            if (token == null) {
                continue;
            }
            tokens.set(i - 1, token);
            tokens.remove(i + 1);
            tokens.remove(i);
        }
        tokens = MathUtils.simplifyNumbers(tokens);
        return tokens;
    }

    public static List<Token> power(List<Token> value, Token token) {
        if (token.getType() != Type.NUMBER) {
            return null;
        }

        if (token.getType() == Type.NUMBER && token.getNum() % 1 != 0) {
            throw new InvalidPowerException("Power must be positive integer or zero: "
                    + token.getToken());
        }
        return power(value, (int)token.getNum());
    }

    public static List<Token> power(List<Token> value, int power) {
        List<Token> result = new LinkedList<>(value);
        List<Token> tmp = new LinkedList<>();

        if (power < 0) {
            throw new InvalidPowerException("Power must be positive integer or zero: " + power);
        }

        if (power == 0) {
            tmp.add(new Number(1));
            return tmp;
        }

        while (power-- > 1) {
            for (int i = 0; i < value.size(); i++) {
                for (int j = 0; j < result.size(); j++) {
                    tmp.add(Multiply.multiply(result.get(j), value.get(i)));
                }
            }
            result = MathUtils.addition(tmp);
            tmp = new LinkedList<>();
        }
        return result;
    }

    public static Token power(Token t1, Token t2) {
        if (t2.getType() != Type.NUMBER || t1.getType() == Type.OPERATOR) {
            return null;
        }

        if (t2.getNum() % 1 != 0 || t2.getNum() < 0) {
            throw new InvalidPowerException("Power must be positive integer or zero: " + t2.getToken());
        }

        if (t1.getType() == Type.NUMBER) {
            return new Number(power(t1.getNum(), (int)t2.getNum()));
        }

        if (t1.getType() == Type.MEMBER) {
            if (t2.getNum() == 0) {
                return new Number(1);
            }
            Member member = new Member((Member) t1);
            member.setPower(member.getPower() * (int) t2.getNum());
            return member;
        }

        //power matrix
        throw new UnknownFormatException("Unknown format in MathUtils.power() : " + t1.getToken());
    }

    public static float power(float f, int i) {
        if (i < 0) {
            throw new InvalidPowerException("Power can't be negative: " + i);
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
