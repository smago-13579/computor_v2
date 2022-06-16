package edu.school21.actions;

import edu.school21.exceptions.UnknownFormatException;
import edu.school21.tokens.Member;
import edu.school21.tokens.Number;
import edu.school21.tokens.Token;
import edu.school21.types.Type;

import java.util.LinkedList;
import java.util.List;

public class Multiply {
    public static List<Token> calculate(List<Token> tokens, Token token) {
        List<Token> tmp = new LinkedList<>();

        if (token.getType() == Type.OPERATOR) {
            return null;
        }

        if (token.getType() != Type.NUMBER) {
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

}
