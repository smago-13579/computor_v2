package edu.school21.analyze;

import edu.school21.data.Data;
import edu.school21.exceptions.InvalidFormException;
import edu.school21.tokens.*;
import edu.school21.types.Mark;
import edu.school21.types.Type;

import java.util.List;
import java.util.Optional;

public class Parser {
    private static final Parser parser = new Parser();
    private final Data data = Data.getInstance();
    private final Checker checker = Checker.getInstance();
    private List<Token> tokens, left, right;
    private boolean hasQuestion;

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
        findImaginaryAndCreate();
        splitTokensWithEquality();

        if (tokens.stream().noneMatch(t -> t.getType() == Type.QUESTION)) {
            checkVariablesBeforeAssignment();
            hasQuestion = false;
        } else {
            checkVariablesBeforeCalculate();
            hasQuestion = true;
        }
    }

    private void checkVariablesBeforeCalculate() {
        if (tokens.stream().filter(t -> t.getType() == Type.QUESTION).count() > 1
                || right.get(right.size() - 1).getType() != Type.QUESTION) {
            throw new InvalidFormException("Incorrect expression: question mark \"?\" must be last");
        }

        if (right.size() == 1) {
            checkBeforeCalculate();
        } else {
            checkBeforeDecision();
        }
    }

    private void checkBeforeDecision() {
        checker.checkFunctions(left);
        checker.checkFunctions(right);
        findMemberThenMap(left);
        findMemberThenMap(right);
        findOtherMembers();

        Optional<Token> tokenO = left.stream().filter(t -> t.getType() == Type.MEMBER && !((Member)t).isImaginary()).findAny();

        if (tokenO.isEmpty()) {
            tokenO = right.stream().filter(t -> t.getType() == Type.MEMBER && !((Member)t).isImaginary()).findAny();
        }
        tokenO.ifPresent(t -> checker.checkMembers(left, right, t.getToken()));
    }

    private void findMemberThenMap(List<Token> tokens) {
        tokens.stream().filter(t -> t.getType() == Type.FUNCTION).forEach(t -> {
            String varName = ((Function) t).getMemberName();

            if (!varName.matches("-?\\d+(\\.\\d+)?") && data.getVariable(varName) == null) {
                checker.checkVariableNameInsideFunction(varName);
                checker.checkVariablesInsideFunctions(varName, left);
                checker.checkVariablesInsideFunctions(varName, right);

                left = left.stream().map(lt -> {
                    if (lt.getType() == Type.VARIABLE && lt.getToken().equalsIgnoreCase(varName)) {
                        return new Member(varName);
                    }
                    return lt;
                }).toList();

                right = right.stream().map(rt -> {
                    if (rt.getType() == Type.VARIABLE && rt.getToken().equalsIgnoreCase(varName)) {
                        return new Member(varName);
                    }
                    return rt;
                }).toList();
            }
        });
    }

    private void findOtherMembers() {
        left = left.stream().map(t -> {
            if (t.getType() == Type.VARIABLE && data.getVariable(t.getToken()) == null) {
                return new Member(t.getToken());
            }
            return t;
        }).toList();

        right = right.stream().map(t -> {
            if (t.getType() == Type.VARIABLE && data.getVariable(t.getToken()) == null) {
                return new Member(t.getToken());
            }
            return t;
        }).toList();
    }

    private void checkBeforeCalculate() {
        checker.checkVariables(left);
        checker.checkVariables(right);
        checker.checkFunctions(left);
        checker.checkFunctions(right);
    }

    private void checkVariablesBeforeAssignment() {
        if (left.size() != 1) {
            throw new InvalidFormException("Incorrect expression: " + Token.getTokens(tokens));
        }

        if (left.get(0).getType() == Type.MEMBER) {
            throw new InvalidFormException("Can't assign to variable: " + left.get(0).getToken());
        }

        if (left.get(0).getType() != Type.VARIABLE && left.get(0).getType() != Type.FUNCTION) {
            throw new InvalidFormException("Incorrect expression: " + left.get(0).getToken());
        }

        if (left.get(0).getType() == Type.FUNCTION) {
            String memberName = ((Function) left.get(0)).getMemberName();
            checker.checkVariableNameInsideFunction(memberName);

            right = right.stream().map(t -> {
                if (t.getType() == Type.VARIABLE && t.getToken().equalsIgnoreCase(memberName)) {
                    return new Member(memberName);
                }
                return t;
            }).toList();
        }
        checker.checkVariables(right);
        checker.checkFunctions(right);
    }

    private void findImaginaryAndCreate() {
        tokens = tokens.stream().map(t -> {
            if (t.getToken().equalsIgnoreCase("i")) {
                return new Member("i");
            }
            return t;
        }).toList();
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

        for (int i = 0; i < left.size(); i++) {
            if (i + 1 < left.size()) {
                checkTokensForValidity(left.get(i), left.get(i + 1));
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

        if (t1.getType() == Type.MATRIX && t2.getType() == Type.OPERATOR && (((Operator)t2).getMark() == Mark.DIVIDE
                || ((Operator)t2).getMark() == Mark.MODULO)) {
            throw new InvalidFormException("Incorrect expression: " + t1.getToken() + " " + t2.getToken());
        }

        if (t2.getType() == Type.MATRIX && t1.getType() == Type.OPERATOR && (((Operator)t1).getMark() == Mark.DIVIDE
                || ((Operator)t1).getMark() == Mark.MODULO)) {
            throw new InvalidFormException("Incorrect expression: " + t1.getToken() + " " + t2.getToken());
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
                (t.getType() == Type.EQUALITY || t.getType() == Type.OPERATOR)).toList();

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

    public List<Token> getLeft() {
        return left;
    }

    public List<Token> getRight() {
        return right;
    }

    public boolean hasQuestion() {
        return hasQuestion;
    }
}
