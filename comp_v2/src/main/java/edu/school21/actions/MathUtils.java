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
//        List<Token> newTokens = new LinkedList<>();
        int first, last;

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);

            if (t.getType() == Type.OPERATOR && ((Operator) t).getMark() == Mark.CLOSE_PARENTHESIS) {
                last = i--;

                while (tokens.get(i).getType() != Type.OPERATOR
                        || ((Operator) tokens.get(i)).getMark() != Mark.OPEN_PARENTHESIS) {
                    i--;
                }
                first = i;
                tokens = calculateSegment(tokens, first, last);
                last = first + 1;

                while (tokens.get(last).getType() != Type.OPERATOR
                        || ((Operator) tokens.get(last)).getMark() != Mark.CLOSE_PARENTHESIS) {
                    last++;
                }

                if (!openParenthesis(tokens, first, last)) {
                    i = last;
                }
            }
        }
        tokens = calculate(tokens);
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

        if (last - first == 2 || (bef == null && aft == null)
                || ((bef == null || ((Operator) bef).getMark() == Mark.PLUS
                || ((Operator) bef).getMark() == Mark.OPEN_PARENTHESIS)
                && (aft == null || ((Operator) aft).getMark() == Mark.PLUS
                || ((Operator) aft).getMark() == Mark.MINUS
                || ((Operator) aft).getMark() == Mark.CLOSE_PARENTHESIS))) {
            tokens.remove(last);
            tokens.remove(first);
            return true;
        }

        if ((aft == null || ((Operator) aft).getMark() == Mark.PLUS || ((Operator) aft).getMark() == Mark.MINUS)
                && (bef != null && ((Operator) bef).getMark() == Mark.MINUS)) {
            for (int i = first + 1; i < last; i++) {
                i = modifyTokensWithMinus(tokens, i, last);
            }
            tokens.remove(last);
            tokens.remove(first);
            return true;
        }
        return false;
    }

    private static int modifyTokensWithMinus(List<Token> tokens, int i, int last) {
        if (tokens.get(i).getType() != Type.OPERATOR) {
            tokens.get(i++).setNegative();

            if (tokens.get(i).getType() != Type.OPERATOR) {
                return i - 1;
            }
        }

        while (i < last && tokens.get(i).getType() == Type.OPERATOR) {
            Operator token = ((Operator) tokens.get(i));

            //TODO is it possible
            if (token.getMark() == Mark.PLUS || token.getMark() == Mark.MINUS) {
                return i;
            }

            if (token.getMark() == Mark.OPEN_PARENTHESIS) {
                while (((Operator) tokens.get(++i)).getMark() != Mark.CLOSE_PARENTHESIS);
                return i;
            }
            i++;
        }
        return i;
    }

    private static List<Token> calculateSegment(List<Token> tokens, int first, int last) {
        calculate(tokens.subList(first + 1, last));
        return tokens;
    }

    private static List<Token> calculate(List<Token> tokens) {
        while (true) {
            Optional<Token> optToken = tokens.stream().filter(t -> t.getType() == Type.OPERATOR
                    && ((Operator) t).getMark() == Mark.POWER).findFirst();

            if (optToken.isPresent()) {
                int i = tokens.indexOf(optToken.get());
                Token token = power(tokens.get(i - 1), tokens.get(i + 1));
                tokens.set(i - 1, token);
                tokens.remove(i + 1);
                tokens.remove(i);
                continue;
            }
            optToken = tokens.stream().filter(t -> t.getType() == Type.OPERATOR
                    && (((Operator) t).getMark() == Mark.MULTIPLY
                    || ((Operator) t).getMark() == Mark.DIVIDE
                    || ((Operator) t).getMark() == Mark.MODULO)).findFirst();

            if (optToken.isPresent()) {
                if (canBeDivided(tokens, optToken.get())) {
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
                    continue;
                } else {
                    int i = tokens.indexOf(optToken.get());

                    for (; i < tokens.size(); i++) {
                        Token token = tokens.get(i);

                        if (token.getType() == Type.OPERATOR && (((Operator) token).getMark() == Mark.PLUS
                                || ((Operator) token).getMark() == Mark.MINUS)
                                && (tokens.get(i + 1).getType() == Type.NUMBER
                                || tokens.get(i + 1).getType() == Type.MEMBER)) {
                            if (((Operator) token).getMark() == Mark.MINUS) {
                                tokens.get(i + 1).setNegative();
                                tokens.set(i, new Operator('+'));
                            }
                            tokens = calculateSegment(tokens, i, tokens.size());
                            break;
                        }
                    }
                    break;
                }//TODO calculate all segment parts
            }
            tokens = tokens.stream().map(t -> {
                if (t.getType() == Type.MEMBER && ((Member)t).isImaginary()) {
                    int power = ((Member) t).getPower() % 4;

                    if (power == 0) {
                        return new Number(1 * t.getNum());
                    } else if (power == 2) {
                        return new Number(-1 * t.getNum());
                    } else if (power == 3) {
                        t.setNegative();
                    }
                    ((Member) t).setPower(1);
                }
                return t;
            }).collect(Collectors.toList());

            List<Token> tmpList = tokens.stream().filter(t -> t.getType() == Type.OPERATOR
                    && (((Operator) t).getMark() == Mark.MINUS
                    || ((Operator) t).getMark() == Mark.PLUS)).collect(Collectors.toList());

            for (Token t : tmpList) {
                int i = tokens.indexOf(t);

                if (((Operator) t).getMark() == Mark.MINUS) {
                    tokens.get(i + 1).setNegative();
                }
                tokens.remove(i);
            }
            tmpList = addition(tokens);

            for (Object t : tokens.toArray()) {
                tokens.remove(t);
            }
            tokens.addAll(tmpList);
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

    public static List<Token> powerAndCalculate(List<Token> value, Token t2) {
        if (t2.getType() != Type.MEMBER || ((Member) t2).isImaginary()) {
            throw new InvalidPowerException("Power must be positive integer or zero: " + t2.getToken());
        }
        int power = ((Member) t2).getPower();
        float f = t2.getNum();
        List<Token> result = power(value, power);
        result.forEach(t -> t.setNum(f * t.getNum()));

        return result;
    }

    public static List<Token> power(List<Token> value, int power) {
        List<Token> result = new LinkedList<>(value);
        List<Token> tmp = new LinkedList<>();

        while (power-- > 1) {
            for (int i = 0; i < value.size(); i++) {
                for (int j = 0; j < result.size(); j++) {
                    tmp.add(multiply(result.get(j), value.get(i)));
                }
            }
            result = addition(tmp);
            tmp = new LinkedList<>();
        }
        return result;
    }

    public static Token power(Token t1, Token t2) {
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
