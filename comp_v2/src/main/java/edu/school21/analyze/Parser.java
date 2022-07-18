package edu.school21.analyze;

import edu.school21.actions.Assignment;
import edu.school21.data.Data;
import edu.school21.exceptions.FunctionNotFoundException;
import edu.school21.exceptions.InvalidFormException;
import edu.school21.exceptions.VariableNotFoundException;
import edu.school21.tokens.*;
import edu.school21.types.Mark;
import edu.school21.types.Type;

import java.util.List;
import java.util.stream.Collectors;

public class Parser {
    private static final Parser parser = new Parser();
    private final Data data = Data.getInstance();
    private List<Token> tokens, left, right;

    private Parser() {
    }

    public static Parser getInstance() {
        return parser;
    }

    public void processing(List<Token> tokens) {
        data.addHistory(Token.getTokens(tokens));
        this.tokens = tokens;
        checkEqualityAndParenthesis();
        checkExpression();

        if (tokens.stream().noneMatch(t -> t.getType() == Type.QUESTION)) {
            checkVariables();
            Assignment.getInstance().assign(left, right);
        }
    }

    private void checkVariables() {
        tokens = tokens.stream().map(t -> {
            if (t.getToken().equalsIgnoreCase("i")) {
                return new Member("i");
            }
            return t;
        }).collect(Collectors.toList());
        splitTokensWithEquality();

        if (left.size() != 1) {
            throw new InvalidFormException("Incorrect expression: " + Token.getTokens(tokens));
        }

        //TODO is it possible?
        if (left.get(0).getType() == Type.MEMBER) {
            throw new InvalidFormException("Can't assign to variable: " + left.get(0).getToken());
        }

        if (left.get(0).getType() == Type.FUNCTION) {
            for (char c : ((Function) left.get(0)).getMemberName().toCharArray()) {
                if ((c < 65 || c > 90) && (c < 97 || c > 122)) {
                    throw new InvalidFormException("Incorrect variable name inside function: "
                            + left.get(0).getToken());
                }
            }

            right = right.stream().map(t -> {
                String name = ((Function) left.get(0)).getMemberName();

                if (t.getType() == Type.VARIABLE && t.getToken().equalsIgnoreCase(name)) {
                    return new Member(name);
                }
                return t;
            }).collect(Collectors.toList());
        }
        right.stream().filter(t -> t.getType() == Type.VARIABLE).forEach(t -> {
            if (data.getVariables().stream().noneMatch(v -> v.getToken().equalsIgnoreCase(t.getToken()))) {
                throw new VariableNotFoundException(t.getToken());
            }
        });

        right.stream().filter(t -> t.getType() == Type.FUNCTION).forEach(t -> {
            if (data.getFunctions().stream().noneMatch(f -> f.getName().equalsIgnoreCase(((Function)t).getName()))) {
                throw new FunctionNotFoundException(t.getToken());
            }
        });
    }

    private void checkExpression() {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == Type.NUMBER && i + 1 < tokens.size()
                    && (tokens.get(i + 1).getType() == Type.VARIABLE
                    || tokens.get(i + 1).getType() == Type.FUNCTION)) {
                tokens.add(i + 1, new Operator("*"));
            }
        }
        splitTokensWithEquality();

        if (left.size() == 1 && left.get(0).getType() != Type.VARIABLE
                && left.get(0).getType() != Type.FUNCTION) {
            throw new InvalidFormException("Incorrect expression: " + left.get(0).getToken());
        }

        if (right.size() == 1 && right.get(0).getType() == Type.OPERATOR) {
            throw new InvalidFormException("Incorrect expression: " + right.get(0).getToken());
        }

        if (right.get(0).getType() == Type.OPERATOR && ((Operator)right.get(0)).getMark() != Mark.OPEN_PARENTHESIS
                && ((Operator)right.get(0)).getMark() != Mark.MINUS) {
            throw new InvalidFormException("Incorrect expression: " + right.get(0).getToken());
        }

        if (right.get(right.size() - 1).getType() == Type.OPERATOR
                && ((Operator)right.get(right.size() - 1)).getMark() != Mark.CLOSE_PARENTHESIS) {
            throw new InvalidFormException("Incorrect expression: " + right.get(right.size() - 1).getToken());
        }

        for (int i = 0; i < right.size(); i++) {
            if (i + 1 < right.size()) {
                checkTokensForValidity(right.get(i), right.get(i + 1));
            }
        }
    }

    private void checkTokensForValidity(Token t1, Token t2) {
        if (t1.getType() == Type.OPERATOR) {
            if (((Operator)t1).getMark() == Mark.OPEN_PARENTHESIS && t2.getType() == Type.OPERATOR
                    && ((Operator)t2).getMark() != Mark.OPEN_PARENTHESIS
                    && ((Operator)t2).getMark() != Mark.MINUS) {
                throw new InvalidFormException("Incorrect expression: " + t1.getToken() + " " + t2.getToken());
            }

            if (((Operator)t1).getMark() == Mark.CLOSE_PARENTHESIS && ((t2.getType() == Type.OPERATOR
                    && ((Operator)t2).getMark() == Mark.OPEN_PARENTHESIS)
                    || t2.getType() == Type.NUMBER || t2.getType() == Type.VARIABLE
                    || t2.getType() == Type.FUNCTION || t2.getType() == Type.MATRIX)) {
                throw new InvalidFormException("Incorrect expression: " + t1.getToken() + " " + t2.getToken());
            }

            if (((Operator)t1).getMark() != Mark.OPEN_PARENTHESIS && ((Operator)t1).getMark() != Mark.CLOSE_PARENTHESIS
                    && t2.getType() == Type.OPERATOR && ((Operator)t2).getMark() != Mark.OPEN_PARENTHESIS) {
                throw new InvalidFormException("Incorrect expression: " + t1.getToken() + " " + t2.getToken());
            }
        }

        if (t1.getType() == Type.NUMBER) {
            if (t2.getType() == Type.NUMBER || t2.getType() == Type.MATRIX
                    || (t2.getType() == Type.OPERATOR && ((Operator)t2).getMark() == Mark.OPEN_PARENTHESIS)) {
                throw new InvalidFormException("Incorrect expression: " + t1.getToken() + " " + t2.getToken());
            }
        }

        if (t1.getType() == Type.VARIABLE || t1.getType() == Type.FUNCTION || t1.getType() == Type.MATRIX) {
            if (t2.getType() == Type.NUMBER || t2.getType() == Type.VARIABLE
                    || t2.getType() == Type.FUNCTION || t2.getType() == Type.MATRIX
                    || (t2.getType() == Type.OPERATOR && ((Operator)t2).getMark() == Mark.OPEN_PARENTHESIS)) {
                throw new InvalidFormException("Incorrect expression: " + t1.getToken() + " " + t2.getToken());
            }
        }
    }

    private void checkEqualityAndParenthesis() {
        int count = 0;

        if (tokens.stream().filter(t -> t.getType() == Type.EQUALITY).count() != 1) {
            throw new InvalidFormException("Incorrect expression: " + Token.getTokens(tokens));
        }
        List<Token> operators = tokens.stream().filter(t ->
                (t.getType() == Type.EQUALITY || t.getType() == Type.OPERATOR)).collect(Collectors.toList());

        for (Token token : operators) {
            if (token.getType() == Type.OPERATOR) {
                if (((Operator)token).getMark() == Mark.OPEN_PARENTHESIS) {
                    count++;
                } else if (((Operator)token).getMark() == Mark.CLOSE_PARENTHESIS) {
                    count--;
                }

                if (count < 0) {
                    throw new InvalidFormException("The open parenthesis is missing - \"(\" ");
                }
            }

            if (token.getType() == Type.EQUALITY && count != 0) {
                throw new InvalidFormException("The closed parenthesis is missing - \")\" ");
            }
        }

        if (count != 0) {
            throw new InvalidFormException("The closed parenthesis is missing - \")\" ");
        }
    }

    private void splitTokensWithEquality() {
        Token token = tokens.stream().filter(t -> t.getType() == Type.EQUALITY).findAny().get();
        int index = tokens.indexOf(token);

        if (index == 0 || index + 1 == tokens.size()) {
            throw new InvalidFormException("Incorrect equality - \"=\" ");
        }
        left = tokens.subList(0, index);
        right = tokens.subList(index + 1, tokens.size());
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
