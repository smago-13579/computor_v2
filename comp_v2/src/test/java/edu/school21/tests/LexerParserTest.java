package edu.school21.tests;

import edu.school21.analyze.Lexer;
import edu.school21.analyze.Parser;
import edu.school21.exceptions.InvalidFormException;
import edu.school21.exceptions.InvalidSymbolException;
import edu.school21.types.Type;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class LexerParserTest {
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
    @ValueSource(strings = {"varA = 1.5 + i", "varB = i - 10"})
    public void simpleLexerTestB(String form) {
        lexer.processing(form);
        assertEquals(5, lexer.getTokens().size());
        assertSame(Type.VARIABLE, lexer.getTokens().get(0).getType());
    }

    @ParameterizedTest
    @ValueSource(strings = {"f(x) = x + 10", "y(x) = x - 1.5", "a(z) = i - z"})
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
    @ValueSource(strings = {"matrix =[[1,2];]", "matrix= [[1];]", "m = [[]]", "m = [;[ 1 ]]",
            "m = [ [ 1 , 2] ; [2,3];[3]]", "m = [ [2]; [2,3];[3]]", "m = [ [2]; [2];[3];]",
            "m = [ []; [2];[3]]", "m = [ [2]; [2];[3];]", "m = [ ]", "m=[", "m=[]", "m = [ [2]; [2];[3];]]" })
    public void errorCheckMatrixA(String form) {
        assertThrows(InvalidFormException.class, () -> lexer.processing(form));
    }

    @ParameterizedTest
    @ValueSource(strings = {"y = ", "varA = ", "= 10", "y = 10 = 2", "varA 10",
            "y = (10 + 2", "(y + 2 = ) 10", "y = ) 10", "varA = ) ( 10", "10= y",
            "f(x) = () + 10", "f(x) = (x + 10)1 + 10", "f(x) = (+ 10) + 10",
            "f(x) = (x + 10) (1 + 10)", "f(x) = (x + 10)x + 10", "y = 10 10", "y = 2* + 10",
            "y = x x", "y = f(1) f(2)", "10 = x", "f(i) = i + 10", "varA = ??",
            "varA + 5 + ? = ?", "? = 10", "varA? = ?", "f(x) = ? 10"})
    public void errorCheckParser(String form) {
        lexer.processing(form);
        assertThrows(InvalidFormException.class, () -> parser.processing(lexer.getTokens()));
    }
}
