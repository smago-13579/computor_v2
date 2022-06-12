package edu.school21.tests;

import edu.school21.analyze.Lexer;
import edu.school21.analyze.Parser;
import edu.school21.exceptions.InvalidFormException;
import edu.school21.exceptions.InvalidSymbolException;
import edu.school21.tokens.*;
import edu.school21.tokens.Number;
import edu.school21.types.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {
    Lexer lexer = Lexer.getInstance();
    Parser parser = Parser.getInstance();

    @ParameterizedTest
    @ValueSource(strings = {"varA = 1", "y = 0", "x = 1"})
    public void simpleLexerTestA(String form) {
        lexer.processing(form);
        assertEquals(3, lexer.getTokens().size());
        assertSame(Type.VARIABLE, lexer.getTokens().get(0).getType());
    }

    @ParameterizedTest
    @ValueSource(strings = {"varA = 1.5 + x", "y = x - 10"})
    public void simpleLexerTestB(String form) {
        lexer.processing(form);
        assertEquals(5, lexer.getTokens().size());
        assertSame(Type.VARIABLE, lexer.getTokens().get(0).getType());
    }

    @ParameterizedTest
    @ValueSource(strings = {"f(x) = x + 10", "y(x) = x - 1.5", "varA(z) = x - z"})
    public void simpleLexerTestC(String form) {
        lexer.processing(form);
        assertEquals(5, lexer.getTokens().size());
        assertSame(Type.FUNCTION, lexer.getTokens().get(0).getType());
    }

    @ParameterizedTest
    @ValueSource(strings = {"f(x) = 5 _ + 10", "y = \\a + 10", "var!A = 10",
            "x = 10 & 11", "y = 5$"})
    public void errorCheckLexerA(String form) {
        assertThrows(InvalidSymbolException.class, () -> lexer.processing(form));
    }

    @ParameterizedTest
    @ValueSource(strings = {"y(x = 10", "varB = .5.5", "y.= 10", "x .= 1"})
    public void errorCheckLexerB(String form) {
        assertThrows(InvalidFormException.class, () -> lexer.processing(form));
    }

    @ParameterizedTest
    @ValueSource(strings = {"y = ", "varA = ", "= 10", "y = 10 = 2", "varA 10",
            "y = (10 + 2", "(y + 2 = ) 10", "y = ) 10", "varA = ) ( 10", "10= y",
            "f(x) = () + 10", "f(x) = (x + 10)1 + 10", "f(x) = (+ 10) + 10",
            "f(x) = (x + 10) (1 + 10)", "f(x) = (x + 10)x + 10", "y = 10 10", "y = 2* + 10",
            "y = x x", "y = f(1) f(2)"})
    public void errorCheckParser(String form) {
        lexer.processing(form);
        assertThrows(InvalidFormException.class, () -> parser.processing(lexer.getTokens()));
    }

    @Test
    public void extLst() {
        List<Token> list = new LinkedList<>();
        list.add(new Operator('('));
        list.add(new Number(1));
        list.add(new Number(2));
        list.add(new Number(3));
        list.add(new Operator(')'));

        list.forEach(t -> System.out.print(t.getToken() + " "));
        System.out.println();
        List<Token> slub = list.subList(1, 4);

//        while (!slub.isEmpty()) {
//            slub.remove(slub.size() - 1);
//        }
        slub.removeAll(slub);
        System.out.println("---------------");
        list.forEach(t -> System.out.print(t.getToken() + " "));
        System.out.println();

        System.out.println("---------------");
        slub.add(new Number(5));
        slub.add(new Number(6));
        list.forEach(t -> System.out.print(t.getToken() + " "));
        System.out.println();

        list.addAll(1, Arrays.asList(new Number(11), new Number(12), new Number(13)));
        list.add(1, new Operator('*'));
        System.out.println("---------------");
        list.forEach(t -> System.out.print(t.getToken() + " "));
        System.out.println();
    }

    @ParameterizedTest
    @ValueSource(strings = {"f(x) = (2 *x ^2+ 5*x^2 + 3*x + 10 - 5 - x)",
            "y(x) = (x^3 + 10)", "y(x) = (2 * x^5 - x^5 + 5)", "z(x) = (2 * x^6 - 3 * x ^ 6 + x^ 6 + 11)",
            "f(x) = (x^2 - x + 3 * x^2 + 10 - i ^2 - 2*i)", "f(x) = (10x^2 + 8 i - 11)"})
    public void assignmentFunctionTestA(String form) {
        lexer.processing(form);
        parser.processing(lexer.getTokens());
    }

    @ParameterizedTest
    @ValueSource(strings = {"f(x) = x ^ 2 + 1", "varA = 3", "varB = (1 + f(varA))",
            "f(x) = 2x^2 + x * 10 - 125 + 2/i*x + 25 + 2^x + 200 + 10x", "varC = f(1)",
            "z(x) = 4 * i * x", "varC = z(1)"})
    public void assignmentTestA(String form) {
        lexer.processing(form);
        parser.processing(lexer.getTokens());
    }
}
