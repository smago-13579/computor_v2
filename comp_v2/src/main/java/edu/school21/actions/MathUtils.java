package edu.school21.actions;

import edu.school21.data.Data;
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
import java.util.Optional;
import java.util.stream.Collectors;

public class MathUtils {
    private static Data data = Data.getInstance();

    public static List<Token> calculateOnePart(List<Token> tokens) {
        List<Token> newTokens = new LinkedList<>();
        boolean progress;
        int first, last;

        do {
            progress = false;

            for (int i = 0; i < tokens.size(); i++) {
                Token t = tokens.get(i);

                if (t.getType() == Type.OPERATOR && ((Operator)t).getMark() == Mark.CLOSE_PARENTHESIS) {
                    last = i--;

                    while (tokens.get(i).getType() != Type.OPERATOR
                            || ((Operator)tokens.get(i)).getMark() != Mark.OPEN_PARENTHESIS) {
                        i--;
                    }
                    first = i;
                    tokens = calcInsideParenthesis(tokens, first, last);
                    last = first + 1;

                    while (tokens.get(last).getType() != Type.OPERATOR
                            || ((Operator)tokens.get(last)).getMark() != Mark.CLOSE_PARENTHESIS) {
                        last++;
                    }

                    if (!openParenthesis(tokens, first, last)) {
                        i = last;
                    }
                    progress = true;
                    break;
                }
            }

        } while (progress);

        return tokens;
    }

    private static boolean openParenthesis(List<Token> tokens, int first, int last) {
        Token bef = null;
        Token aft = null;

        if (first > 0) {
            bef = tokens.get(first - 1);
        }

        if (last != tokens.size() - 1) {
            aft = tokens.get(last + 1);
        }

        if (last - first == 2 || (first == 0 && last == tokens.size() -1)
                || (first == 0 && (((Operator)aft).getMark() == Mark.PLUS || ((Operator)aft).getMark() == Mark.MINUS))
                || (last == tokens.size() - 1 && ((Operator)bef).getMark() == Mark.PLUS)) {
            tokens.remove(last);
            tokens.remove(first);
            return true;
        }

        return false;
    }

    private static List<Token> calcInsideParenthesis(List<Token> tokens, int first, int last) {
        while (true) {
            Optional<Token> optToken = tokens.subList(first + 1, last).stream().filter(t -> t.getType() == Type.OPERATOR
                    && ((Operator)t).getMark() == Mark.POWER).findFirst();

            if (optToken.isPresent()) {
                int i = tokens.indexOf(optToken.get());
                Token token = power(tokens.get(i - 1), tokens.get(i + 1));
                tokens.set(i - 1, token);
                tokens.remove(i + 1);
                tokens.remove(i);
                last -= 2;
                continue;
            }
            optToken = tokens.subList(first + 1, last).stream().filter(t -> t.getType() == Type.OPERATOR
                    && (((Operator)t).getMark() == Mark.MULTIPLY
                    || ((Operator)t).getMark() == Mark.DIVIDE
                    || ((Operator)t).getMark() == Mark.MODULO)).findFirst();

            if (optToken.isPresent() && canBeDivided(tokens, optToken.get())) {
                int i = tokens.indexOf(optToken.get());
                Token token;

                if (((Operator) optToken.get()).getMark() == Mark.MULTIPLY) {
                    token = multiply(tokens.get(i - 1), tokens.get(i + 1));
                } else if (((Operator) optToken.get()).getMark() == Mark.DIVIDE) {
                    token = divide(tokens.get(i - 1), tokens.get(i + 1));
                } else {
                    token = new Number(tokens.get(i - 1).getNum() % tokens.get(i + 1).getNum());
                }
                tokens.set(i - 1, token);
                tokens.remove(i + 1);
                tokens.remove(i);
                last -= 2;
                continue;
            }
            List<Token> tmpList = tokens.subList(first + 1, last).stream().filter(t -> t.getType() == Type.OPERATOR
                    && (((Operator)t).getMark() == Mark.MINUS
                    || ((Operator)t).getMark() == Mark.PLUS)).collect(Collectors.toList());

            for (Token t : tmpList) {
                int i = tokens.indexOf(t);

                if (((Operator)t).getMark() == Mark.MINUS) {
                    tokens.get(i + 1).setNegative();
                }
                tokens.remove(i);
                last--;
            }
            tmpList = addition(tokens.subList(first + 1, last));

            for (Object t : tokens.subList(first + 1, last).toArray()) {
                tokens.remove(t);
            }
            tokens.addAll(first + 1, tmpList);
            break;
        }
        return tokens;
    }

    private static List<Token> addition(List<Token> tokens) {
        List<Token> members = new LinkedList<>();
        List<Token> tmpList = tokens.stream().filter(t -> t.getType() == Type.MEMBER && !((Member)t).isImaginary())
                .sorted((t1, t2) -> ((Member)t2).getPower() - ((Member)t1).getPower()).collect(Collectors.toList());

        for (Token t1 : tmpList) {
            Optional<Token> optToken = members.stream()
                    .filter(t2 -> ((Member)t2).getPower() == ((Member)t1).getPower()).findAny();

            if (optToken.isPresent()) {
                Token t2 = optToken.get();
                t2.setNum(t2.getNum() + t1.getNum());
            } else {
                members.add(t1);
            }
        }
        tmpList = tokens.stream().filter(t -> t.getType() == Type.MEMBER && ((Member)t).isImaginary())
                .sorted((t1, t2) -> ((Member)t2).getPower() - ((Member)t1).getPower()).collect(Collectors.toList());

        for (Token t1 : tmpList) {
            Optional<Token> optToken = members.stream()
                    .filter(t2 -> ((Member)t2).getPower() == ((Member)t1).getPower()
                            && ((Member)t2).isImaginary()).findAny();

            if (optToken.isPresent()) {
                Token t2 = optToken.get();
                t2.setNum(t2.getNum() + t1.getNum());
            } else {
                members.add(t1);
            }
        }
        float sum = tokens.stream().filter(t -> t.getType() == Type.NUMBER)
                .map(Token::getNum).reduce((float) 0, Float::sum);
        members.add(new Number(sum));
        members = members.stream().filter(t -> t.getNum() != 0).collect(Collectors.toList());
        return members;
    }

    private static Token divide(Token t1, Token t2) {
        if (t1.getType() == Type.NUMBER && t2.getType() == Type.NUMBER) {
            return new Number(t1.getNum() / t2.getNum());
        }

        if (t2.getType() == Type.NUMBER) {
            t1.setNum(t1.getNum() / t2.getNum());
            return t1;
        }

        if ((!((Member)t1).isImaginary() && !((Member)t2).isImaginary())
                || (((Member)t1).isImaginary() && ((Member)t2).isImaginary())) {
            t1.setNum(t1.getNum() / t2.getNum());
            ((Member) t1).setPower(((Member) t1).getPower() - ((Member) t2).getPower());

            if (((Member) t1).getPower() == 0) {
                return new Number(t1.getNum());
            }
            return t1;
        }

        throw new UnknownFormatException("Unknown format in MathUtils.divide() : " + t1.getToken()
                + " and " + t2.getType());
    }

    private static Token multiply(Token t1, Token t2) {
        if (t1.getType() == Type.NUMBER && t2.getType() == Type.NUMBER) {
            return new Number(t1.getNum() * t2.getNum());
        }

        if (t1.getType() == Type.NUMBER) {
            t2.setNum(t1.getNum() * t2.getNum());
            return t2;
        }

        if (t2.getType() == Type.NUMBER) {
            t1.setNum(t1.getNum() * t2.getNum());
            return t1;
        }

        if ((!((Member)t1).isImaginary() && !((Member)t2).isImaginary())
                || (((Member)t1).isImaginary() && ((Member)t2).isImaginary())) {
            t1.setNum(t1.getNum() * t2.getNum());
            ((Member) t1).setPower(((Member) t1).getPower() + ((Member) t2).getPower());

            if (((Member) t1).getPower() == 0) {
                return new Number(t1.getNum());
            }
            return t1;
        }

        throw new UnknownFormatException("Unknown format in MathUtils.multiply() : " + t1.getToken()
                + " and " + t2.getType());
    }

    private static Token power(Token t1, Token t2) {
        if (t2.getType() != Type.NUMBER || t2.getNum() % 1 != 0) {
            throw new InvalidPowerException("Power must be positive integer or zero: " + t2.getToken());
        }

        if (t1.getType() == Type.NUMBER) {
            return new Number(power(t1.getNum(), (int)t2.getNum()));
        }

        if (t1.getType() == Type.MEMBER) {
            if (t2.getNum() == 0) {
                return new Number(1);
            }
            ((Member) t1).setPower((int) t2.getNum());
            return t1;
        }

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

    private static boolean canBeDivided(List<Token> tokens, Token token) {
        int i = tokens.indexOf(token);

        if (((Operator) token).getMark() == Mark.DIVIDE
                && tokens.get(i - 1).getType() == Type.NUMBER
                && tokens.get(i + 1).getType() != Type.NUMBER) {
            return false;
        }

        if (((Operator) token).getMark() == Mark.MODULO
                && (tokens.get(i - 1).getType() != Type.NUMBER
                || tokens.get(i + 1).getType() != Type.NUMBER)) {
            return false;
        }
        return true;
    }
}
