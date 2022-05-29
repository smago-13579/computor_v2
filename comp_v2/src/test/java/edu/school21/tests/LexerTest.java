package edu.school21.tests;

import edu.school21.analyze.Lexer;
import edu.school21.analyze.Parser;
import edu.school21.exceptions.InvalidFormException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {
    Lexer lexer = Lexer.getInstance();
    Parser parser = Parser.getInstance();

    @Test
    public void simpleLexerTestA() {
        lexer.processing("varA = 1");
        assertEquals(3, lexer.getTokens().size());
    }

    @Test
    public void simpleLexerTestB() {
        lexer.processing("varA = 1.5 + x");
        assertEquals(5, lexer.getTokens().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"y = ", "varA = ", "= 10", "y = 10 = 2", "varA 10",
        "y = (10 + 2", "(y + 2 = ) 10", "y = ) 10", "varA = ) ( 10", "10= y"})
    public void errorCheckParser(String form) {
        lexer.processing(form);
        assertThrows(InvalidFormException.class, () -> parser.processing(lexer.getTokens()));
    }
}
