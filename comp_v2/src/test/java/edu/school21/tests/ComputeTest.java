package edu.school21.tests;

import edu.school21.actions.Compute;
import edu.school21.analyze.Checker;
import edu.school21.analyze.Lexer;
import edu.school21.analyze.Parser;
import edu.school21.data.Data;
import edu.school21.exceptions.InvalidFormException;
import edu.school21.exceptions.InvalidPolynomialException;
import edu.school21.exceptions.VariableNotFoundException;
import edu.school21.service.Service;
import edu.school21.tokens.Function;
import edu.school21.tokens.Printable;
import edu.school21.tokens.Variable;
import edu.school21.types.Type;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComputeTest {
    Parser parser = Parser.getInstance();
    Data data = Data.getInstance();
    Lexer lexer = Lexer.getInstance();
    Checker checker = Checker.getInstance();
    Compute compute = Compute.getInstance();
    Service service = Service.getInstance();


    @BeforeAll
    static void createVars() {
        Service.getInstance().perform("varA = 10 + i");
        Service.getInstance().perform("varB = i - 5");
        Service.getInstance().perform("varC = 2");
        Service.getInstance().perform("f(x) = x ^ 2 + 2x + 1");
        Service.getInstance().perform("y(x) = x ^ 3 + 2x + 1");
        Service.getInstance().perform("z(y) = y");
    }

    @ParameterizedTest
    @ValueSource(strings = {"f(x) + x = y + 1 ?", "f(x) + z(y) + x = y ?", "f(x1) = x ?", "f(x) + z(y) = 1 ?",
            "f(x) = z(y) ?"})
    public void errorCheckParserA(String form) {
        lexer.processing(form);
        assertThrows(InvalidFormException.class, () -> parser.processing(lexer.getTokens()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"f(x) ^ 2 = 10 ?", "f(x)+i=x?", "y(x) = 1?",
            "x / i = 0?", "x % 10 + 1 = 0 ?", "x * i + 1 = 1 ?", "10 / x = 0?", "10 %x = 0?"})
    public void errorChecker(String form) {
        assertThrows(InvalidPolynomialException.class, () -> service.perform(form));
    }

    @ParameterizedTest
    @MethodSource("variables_testA")
    public void calculateTestA(String form, String expected) {
        service.perform(form);

        Printable token = compute.getToken();
        assertEquals(expected, token.getValueToString());
    }

    private static Stream<Arguments> variables_testA() {
        return Stream.of(
                Arguments.of("1 = ?", "1 "),
                Arguments.of("varA = ? ", "i + 10 "),
                Arguments.of("varA + i = ?", "2i + 10 "),
                Arguments.of("varB = ?", "i - 5 "),
                Arguments.of("vara + varB = ?", "2i + 5 "),
                Arguments.of("vara - varb = ? ", "15 "),
                Arguments.of("varB - varA = ?", "- 15 "),
                Arguments.of("f(varC) = ?", "9 "),
                Arguments.of("y(varC) = ?", "13 "),
                Arguments.of("f(varc) + y(varC) = ?", "22 "),
                Arguments.of("f(1) + y(3) = ?", "38 ")
        );
    }
}
