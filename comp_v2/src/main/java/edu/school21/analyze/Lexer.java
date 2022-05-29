package edu.school21.analyze;

import edu.school21.exceptions.InvalidFormException;
import edu.school21.exceptions.InvalidSymbolException;
import edu.school21.tokens.Equality;
import edu.school21.tokens.Operator;
import edu.school21.tokens.Token;
import edu.school21.tokens.Number;
import edu.school21.tokens.Variable;

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
        Token.printTokens(tokens);
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
                } else {
                    tokens.add(new Operator(c));
                }
                continue;
            }

            if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
                i = parseVar(i);
                continue;
            }

            if (c == '.') {
                throw new InvalidFormException("Invalid form: \".\"");
            }
        }
    }

    private int parseVar(int i) {
        String name = "";
        int len = i;
        name += form.charAt(len++);

        while (len != form.length() && ((form.charAt(len) >= 65 && form.charAt(len) <= 90)
                || (form.charAt(len) >= 97 && form.charAt(len) <= 122))) {
            name += form.charAt(len++);
        }
        tokens.add(new Variable(name));

//        while (len != form.length() && form.charAt(len) == ' ') {
//            len++;
//        }

//        if (len == form.length() || form.charAt(len) != '^') {
//            tokens.add(new Variable(token));
//        } else if (len != form.length() && form.charAt(len) == '^') {
//            token += form.charAt(len++);
//
//            while (len != form.length() && form.charAt(len) == ' ')
//                len++;
//
//            while (len != form.length() && "1234567890".indexOf(form.charAt(len)) != -1)
//                token += form.charAt(len++);
//
//            if (token.length() != 3)
//                throw new InvalidFormException("Invalid equation degree");
//            tokens.add(new Token(token));
//        }
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
        String validSymbols = "()[] +-*/^%=1234567890;,.";
        return validSymbols.indexOf(c) != -1 || (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
