package edu.school21.actions;

import edu.school21.tokens.Member;
import edu.school21.tokens.Operator;
import edu.school21.tokens.Token;
import edu.school21.types.Mark;
import edu.school21.types.Type;

import java.util.List;

public class Parenthesis {
    public static int findOpenParenthesis(List<Token> tokens, int i) {
        int count = 0;

        while (i >= 0) {
            if (tokens.get(i).getType() == Type.OPERATOR) {
                if (((Operator)tokens.get(i)).getMark() == Mark.CLOSE_PARENTHESIS) {
                    count++;
                }

                if (((Operator)tokens.get(i)).getMark() == Mark.OPEN_PARENTHESIS) {
                    if (count == 0) {
                        return i;
                    }
                    count--;
                }
            }
            i--;
        }
        return i;
    }

    public static int findCloseParenthesis(List<Token> tokens, int i) {
        int count = 0;

        while (i < tokens.size()) {
            if (tokens.get(i).getType() == Type.OPERATOR) {
                if (((Operator)tokens.get(i)).getMark() == Mark.OPEN_PARENTHESIS) {
                    count++;
                }

                if (((Operator)tokens.get(i)).getMark() == Mark.CLOSE_PARENTHESIS) {
                    if (count == 0) {
                        return i;
                    }
                    count--;
                }
            }
            i++;
        }
        return i;
    }

    public static boolean openParenthesis(List<Token> tokens, int first, int last) {
        Token bef = null;
        Token aft = null;

        if (first > 0) {
            bef = tokens.get(first - 1);
        }

        if (last != tokens.size() - 1) {
            aft = tokens.get(last + 1);
        }

        if ((last - first == 2 && (tokens.get(first + 1).getNum() >= 0 || bef == null))
                || ((bef == null || ((Operator) bef).getMark() == Mark.PLUS
                || ((Operator) bef).getMark() == Mark.OPEN_PARENTHESIS)
                && (aft == null || ((Operator) aft).getMark() == Mark.PLUS
                || ((Operator) aft).getMark() == Mark.MINUS
                || ((Operator) aft).getMark() == Mark.CLOSE_PARENTHESIS))) {
            tokens.remove(last);
            tokens.remove(first);
            return true;
        }

        if (bef != null && ((Operator) bef).getMark() == Mark.MINUS
                && (aft == null || ((Operator) aft).getMark() != Mark.POWER)) {
            for (int i = first + 1; i < last; i++) {
                i = modifyTokensWithMinus(tokens, i, last);
            }

            if (aft == null || ((Operator) aft).getMark() == Mark.PLUS || ((Operator) aft).getMark() == Mark.MINUS) {
                tokens.remove(last);
                tokens.remove(first);
                tokens.remove(first - 1);
                return true;
            } else {
                tokens.set(first - 1, new Operator('+'));
                return openParenthesis(tokens, first, last);
            }
        }

        if (aft != null && aft.getType() == Type.OPERATOR && ((Operator) aft).getMark() == Mark.POWER
                && tokens.subList(first + 1, last).stream().noneMatch(t -> t.getType() == Type.OPERATOR)
                && (tokens.subList(first + 1, last).stream().filter(t -> t.getType() == Type.MEMBER)
                .allMatch(t -> ((Member)t).isImaginary()) || tokens.subList(first + 1, last).stream()
                .filter(t -> t.getType() == Type.MEMBER).noneMatch(t -> ((Member)t).isImaginary()))) {
            List<Token> tmp = Power.power(tokens.subList(first + 1, last), tokens.get(last + 2));

            if (tmp == null) {
                return false;
            }
            tokens.remove(last + 2);
            tokens.remove(last + 1);
            List<Token> subList = tokens.subList(first + 1, last);
            subList.removeAll(subList);
            subList.addAll(tmp);
            return openParenthesis(tokens, first, first + tmp.size() + 1);
        }

        if (bef != null && bef.getType() == Type.OPERATOR && ((Operator) bef).getMark() == Mark.DIVIDE
                && tokens.get(first - 2).getType() == Type.OPERATOR) {
            int close = first - 2;
            int open = findOpenParenthesis(tokens, close - 1);
            Token tmp = Divide.calculate(tokens.subList(open + 1, close), tokens.subList(first + 1, last));

            if (tmp != null) {
                List<Token> subList = tokens.subList(open, last + 1);
                subList.removeAll(subList);
                subList.add(tmp);
                return true;
            }
        }

        if (aft != null && aft.getType() == Type.OPERATOR
                && (((Operator) aft).getMark() == Mark.MULTIPLY || ((Operator) aft).getMark() == Mark.DIVIDE)
                && tokens.subList(first + 1, last).stream().noneMatch(t -> t.getType() == Type.OPERATOR)
                && tokens.get(last + 2).getType() != Type.OPERATOR) {
            List<Token> tmp;

            if (((Operator) aft).getMark() == Mark.MULTIPLY) {
                tmp = Multiply.calculate(tokens.subList(first + 1, last), tokens.get(last + 2));
            } else {
                tmp = Divide.calculate(tokens.subList(first + 1, last), tokens.get(last + 2));
            }

            if (tmp != null) {
                tokens.remove(last + 2);
                tokens.remove(last + 1);
                List<Token> subList = tokens.subList(first + 1, last);
                subList.removeAll(subList);
                subList.addAll(tmp);
                return openParenthesis(tokens, first, first + tmp.size() + 1);
            }
        }

        if (bef != null && bef.getType() == Type.OPERATOR && ((Operator) bef).getMark() == Mark.MULTIPLY
                && tokens.subList(first + 1, last).stream().noneMatch(t -> t.getType() == Type.OPERATOR)) {
            if (tokens.get(first - 2).getType() != Type.OPERATOR) {
                List<Token> tmp = Multiply.calculate(tokens.subList(first + 1, last), tokens.get(first - 2));

                if (tmp != null) {
                    List<Token> subList = tokens.subList(first + 1, last);
                    subList.removeAll(subList);
                    subList.addAll(tmp);
                    tokens.remove(bef);
                    tokens.remove(first - 2);
                    return openParenthesis(tokens, first - 2, first + tmp.size() - 1);
                }
            } else {
                int close = first - 2;
                int open = findOpenParenthesis(tokens, close - 1);
                List<Token> tmp = Multiply.calculate(tokens.subList(open + 1, close), tokens.subList(first + 1, last));

                if (tmp != null) {
                    List<Token> subList = tokens.subList(open + 1, last);
                    subList.removeAll(subList);
                    subList.addAll(tmp);
                    return openParenthesis(tokens, open, open + tmp.size() + 1);
                }
            }
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
}
