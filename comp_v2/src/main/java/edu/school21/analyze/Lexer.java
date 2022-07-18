package edu.school21.analyze;

import edu.school21.exceptions.InvalidFormException;
import edu.school21.exceptions.InvalidSymbolException;
import edu.school21.tokens.*;
import edu.school21.tokens.Number;

import java.util.LinkedList;
import java.util.List;

public class Lexer {
    private static final Lexer lexer = new Lexer();
    private String form;
    private List<Token> tokens;

    private Lexer() {}

    public static Lexer getInstance() {
        return lexer;
    }

    public void processing(String form) {
        this.form = form;
        checkForm();
        createTokens();
    }

    private void createTokens() {
        tokens = new LinkedList<>();

        for (int i = 0; i < form.length(); i++) {
            char c = form.charAt(i);

            if (c >= '0' && c <= '9') {
                i = parseNum(i);
                continue;
            }

            if ("+-*/%^()=".indexOf(c) != -1) {
                if (c == '=') {
                    tokens.add(new Equality());
                } else if (c == '*' && i + 1 < form.length() && form.charAt(i + 1) == '*') {
                    tokens.add(new Operator("**"));
                } else {
                    tokens.add(new Operator(Character.toString(c)));
                }
                continue;
            }

            if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
                i = parseVar(i);
                continue;
            }

            if (c == '[') {
                i = parseMatrix(i + 1);
                continue;
            }

            if (c == '?') {
                tokens.add(new Question());
            }

            if (c == '.') {
                throw new InvalidFormException("Invalid form: \".\"");
            }
        }
    }

    private int parseMatrix(int i) {
        while (i < form.length() && form.charAt(i) == ' ') {
            i++;
        }

        if (i == form.length() || form.charAt(i) != '[') {
            throw new InvalidFormException("Invalid matrix: missing opening bracket \"[\"");
        }
        int start = i;
        int end = form.length();

        while (i < form.length()) {
            if (form.charAt(i) == ']') {
                end = ++i;

                while (i < form.length() && form.charAt(i) == ' ') {
                    i++;
                }

                if (i < form.length() && form.charAt(i) == ']') {
                    break;
                }
            } else {
                i++;
            }
        }

        if (i == form.length()) {
            throw new InvalidFormException("Invalid matrix: missing closing bracket \"]\"");
        }
        tokens.add(new Matrix(form.substring(start, end)));

        return i;
    }

    private int parseVar(int i) {
        StringBuilder name = new StringBuilder();
        int len = i;
        name.append(form.charAt(len++));

        while (len != form.length() && ((form.charAt(len) >= 65 && form.charAt(len) <= 90)
                || (form.charAt(len) >= 97 && form.charAt(len) <= 122))) {
            name.append(form.charAt(len++));
        }

        if (len == form.length() || form.charAt(len) != '(') {
            tokens.add(new Variable(name.toString()));
        } else {
            while (len != form.length() && form.charAt(len) != ')') {
                name.append(form.charAt(len++));
            }

            if (len == form.length()) {
                throw new InvalidFormException("Invalid form: \"" + name + "\"");
            }
            name.append(form.charAt(len++));
            tokens.add(new Function(name.toString()));
        }
        return (len - 1);
    }

    private int parseNum(int i) {
        float f;
        int len = i;
        char c = form.charAt(len);

        while ((c >= '0' && c <= '9') || c == '.') {
            len++;

            if (form.length() == len) {
                break;
            }
            c = form.charAt(len);
        }
        f = Float.parseFloat(form.substring(i, len));
        tokens.add(new Number(f));
        return (len - 1);
    }

    private void checkForm() {
        for (char c : form.toCharArray()) {
            if (!isValid(c)) {
                throw new InvalidSymbolException(Character.toString(c));
            }
        }
    }

    private boolean isValid(char c) {
        String validSymbols = "()[] +-*/^%=1234567890;,.?";
        return validSymbols.indexOf(c) != -1 || (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
