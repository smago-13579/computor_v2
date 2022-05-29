package edu.school21.analyze;

import edu.school21.exceptions.InvalidFormException;
import edu.school21.tokens.Operator;
import edu.school21.tokens.Token;
import edu.school21.tokens.Variable;
import edu.school21.types.Mark;
import edu.school21.types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {
    private static final Parser parser = new Parser();
    private final List<Variable> variables = new ArrayList<>();
    private final List<String> history = new ArrayList<>();

    private Parser() {
    }

    public static Parser getInstance() {
        return parser;
    }

    public void processing(List<Token> tokens) {
        checkEqualityAndParenthesis(tokens);
        checkExpression(tokens);
    }

    private void checkExpression(List<Token> tokens) {
        List<Token> left, right;
        Token token = tokens.stream().filter(t -> t.getType() == Type.EQUALITY).findAny().get();
        int index = tokens.indexOf(token);

        if (index == 0 || index + 1 == tokens.size()) {
            throw new InvalidFormException("Incorrect equality - \"=\" ");
        }
        left = tokens.subList(0, index);
        right = tokens.subList(index + 1, tokens.size());

        if (left.size() == 1 && left.get(0).getType() != Type.VARIABLE) {
            throw new InvalidFormException("Incorrect expression: " + left.get(0).getToken());
        }

        if (right.size() == 1 && right.get(0).getType() != Type.VARIABLE
                && right.get(0).getType() != Type.NUMBER) {
            throw new InvalidFormException("Incorrect expression: " + right.get(0).getToken());
        }
    }

    private void checkEqualityAndParenthesis(List<Token> tokens) {
        int count = 0;

        if (tokens.stream().filter(t -> t.getType() == Type.EQUALITY).count() != 1) {
            throw new InvalidFormException("Incorrect equality - \"=\" ");
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
}
