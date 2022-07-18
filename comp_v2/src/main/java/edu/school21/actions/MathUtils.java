package edu.school21.actions;

import edu.school21.data.Data;
import edu.school21.exceptions.InvalidPowerException;
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
        int first, last;

        tokens = Power.calculate(tokens);

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);

            if (t.getType() == Type.OPERATOR && ((Operator) t).getMark() == Mark.CLOSE_PARENTHESIS) {
                last = i--;
                first = Parenthesis.findOpenParenthesis(tokens, i);
                tokens = calculateSegment(tokens, first, last);
                last = Parenthesis.findCloseParenthesis(tokens, first + 1);
                i = first;

                if (!Parenthesis.openParenthesis(tokens, first, last)) {
                    i = last;
                }
            }
        }
        tokens = calculate(tokens);
        return tokens;
    }

    private static List<Token> calculateSegment(List<Token> tokens, int first, int last) {
        List<Token> subList = tokens.subList(first + 1, last);
        List<Token> tmpList = calculate(subList);
        subList.removeAll(subList);
        subList.addAll(tmpList);
        return tokens;
    }

    private static List<Token> calculate(List<Token> tokens) {
        tokens = Power.calculate(tokens);

        List<Token> tmp = tokens.stream().filter(t -> t.getType() == Type.OPERATOR
                && (((Operator) t).getMark() == Mark.MULTIPLY
                || ((Operator) t).getMark() == Mark.DIVIDE
                || ((Operator) t).getMark() == Mark.MODULO)).collect(Collectors.toList());

        for (Token t : tmp) {
            if (isValidAction(tokens, t)) {
                int i = tokens.indexOf(t);
                Token token;

                if (((Operator) t).getMark() == Mark.MULTIPLY) {
                    token = Multiply.multiply(tokens.get(i - 1), tokens.get(i + 1));
                } else if (((Operator) t).getMark() == Mark.DIVIDE) {
                    token = Divide.divide(tokens.get(i - 1), tokens.get(i + 1));
                } else {
                    token = new Number(tokens.get(i - 1).getNum() % tokens.get(i + 1).getNum());
                }
                tokens.set(i - 1, token);
                tokens.remove(i + 1);
                tokens.remove(i);
                tokens = simplifyNumbers(tokens);
            }
        }

        tmp = createTemporaryList(tokens);
        tokens.removeAll(tmp);

        List<Token> operators = tokens.stream().filter(t -> t.getType() == Type.OPERATOR
                && (((Operator) t).getMark() == Mark.MINUS
                || ((Operator) t).getMark() == Mark.PLUS)).collect(Collectors.toList());

        for (Token t : operators) {
            int i = tokens.indexOf(t);

            if (((Operator) t).getMark() == Mark.MINUS) {
                tokens.get(i + 1).setNegative();
            }
            tokens.remove(i);
        }
        tokens = addition(tokens);
        tokens.addAll(tmp);
        return tokens;
    }

    private static List<Token> createTemporaryList(List<Token> tokens) {
        int i, j;
        List<Token> operators = tokens.stream().filter(t -> t.getType() == Type.OPERATOR
                && ((Operator) t).getMark() != Mark.MINUS
                && ((Operator) t).getMark() != Mark.PLUS).collect(Collectors.toList());
        List<Token> tmp = new LinkedList<>();

        for (Token t : operators) {
            if (!tmp.contains(t)) {
                i = tokens.indexOf(t);

                if (((Operator) t).getMark() == Mark.OPEN_PARENTHESIS) {
                    if (i - 1 >= 0 && tokens.get(i - 1).getType() == Type.OPERATOR
                            && (((Operator) tokens.get(i - 1)).getMark() == Mark.MINUS
                            || ((Operator) tokens.get(i - 1)).getMark() == Mark.PLUS)) {
                        tmp.add(tokens.get(i - 1));
                    }
                    j = findLastTmpElement(tokens, i);
                } else {
                    if (i - 2 >= 0 && tokens.get(i - 2).getType() == Type.OPERATOR
                            && (((Operator) tokens.get(i - 2)).getMark() == Mark.MINUS
                            || ((Operator) tokens.get(i - 2)).getMark() == Mark.PLUS)) {
                        tmp.add(tokens.get(i - 2));
                    }
                    tmp.add(tokens.get(i - 1));
                    j = findLastTmpElement(tokens, i + 1);
                }
                tmp.addAll(tokens.subList(i, j));
            }
        }
        return tmp;
    }

    private static int findLastTmpElement(List<Token> tokens, int i) {
        int num = 0;

        while (i < tokens.size()) {
            if (tokens.get(i).getType() == Type.OPERATOR) {
                Operator t = (Operator) tokens.get(i);

                if (t.getMark() == Mark.OPEN_PARENTHESIS) {
                    num++;
                } else if (t.getMark() == Mark.CLOSE_PARENTHESIS) {
                    num--;
                } else if ((t.getMark() == Mark.PLUS || t.getMark() == Mark.MINUS) && num == 0) {
                    return i;
                }
            }
            i++;
        }
        return i;
    }

    public static List<Token> addition(List<Token> tokens) {
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

    public static List<Token> injectValueAndCalculate(List<Token> value, Token t2) {
        if (t2.getType() != Type.MEMBER || ((Member) t2).isImaginary()) {
            throw new InvalidPowerException("Power must be positive integer or zero: " + t2.getToken());
        }
        int power = ((Member) t2).getPower();
        float f = t2.getNum();
        List<Token> result = Power.power(value, power);
        result.forEach(t -> t.setNum(f * t.getNum()));

        return result;
    }

    public static List<Token> simplifyNumbers(List<Token> tokens) {
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

        List<Token> tmp = tokens.stream().filter(t -> t.getType() == Type.NUMBER && t.getNum() == 1)
                .collect(Collectors.toList());

        for (Token t : tmp) {
            int i = tokens.indexOf(t);

            if (i > 0) {
                Token token = tokens.get(i - 1);

                if (token.getType() == Type.OPERATOR
                        && (((Operator)token).getMark() == Mark.MULTIPLY
                        || ((Operator)token).getMark() == Mark.DIVIDE
                        || ((Operator)token).getMark() == Mark.POWER)) {
                    tokens.remove(i);
                    tokens.remove(i - 1);
                }
            }
        }
        return tokens;
    }

    private static boolean isValidAction(List<Token> tokens, Token token) {
        int i = tokens.indexOf(token);

        if (i - 1 < 0 || i + 1 >= tokens.size() || tokens.get(i - 1).getType() == Type.OPERATOR
                || tokens.get(i + 1).getType() == Type.OPERATOR) {
            return false;
        }

        if (i - 2 >= 0 && tokens.get(i - 2).getType() == Type.OPERATOR
                && ((Operator)tokens.get(i - 2)).getMark() != Mark.PLUS
                && ((Operator)tokens.get(i - 2)).getMark() != Mark.MINUS) {
            return false;
        }

        if (i + 2 < tokens.size() && tokens.get(i + 2).getType() == Type.OPERATOR
                && ((Operator)tokens.get(i + 2)).getMark() == Mark.POWER) {
            return false;
        }

        if ((((Operator) token).getMark() == Mark.MULTIPLY || ((Operator) token).getMark() == Mark.DIVIDE)
                && tokens.get(i - 1).getType() == Type.MEMBER
                && tokens.get(i + 1).getType() == Type.MEMBER
                && (((Member)tokens.get(i - 1)).isImaginary() != ((Member)tokens.get(i + 1)).isImaginary()
                || (((Operator) token).getMark() == Mark.DIVIDE
                && ((Member)tokens.get(i - 1)).getPower() < ((Member)tokens.get(i + 1)).getPower()))) {
            return false;
        }

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
