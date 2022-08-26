package edu.school21.tests;

import edu.school21.analyze.Lexer;
import edu.school21.analyze.Parser;
import edu.school21.data.Data;
import edu.school21.exceptions.InvalidFormException;
import edu.school21.service.Service;
import edu.school21.tokens.Function;
import edu.school21.tokens.Variable;
import edu.school21.types.Type;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssignmentTest {
    Lexer lexer = Lexer.getInstance();
    Parser parser = Parser.getInstance();
    Data data = Data.getInstance();
    Service service = Service.getInstance();

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

    @ParameterizedTest
    @MethodSource("variablesFunctions_testA")
    public void assignmentTestA(String form, String expected) {
        service.perform(form);

        if (parser.getTokens().get(0).getType() == Type.FUNCTION) {
            Function function = data.getFunction(String.valueOf(form.charAt(0)));
            assertEquals(expected, function.getValueToString());
        } else {
            Variable variable = data.getVariable(form.substring(0, 4));
            assertEquals(expected, variable.getValueToString());
        }
    }

    @ParameterizedTest
    @MethodSource("functions_testB")
    public void assignmentTestB(String form, String expected) {
        service.perform(form);
        Function function = data.getFunction(String.valueOf(form.charAt(0)));
        assertEquals(expected, function.getValueToString());
    }

    private static Stream<Arguments> variablesFunctions_testA() {
        return Stream.of(
                Arguments.of("f(x) = x ^ 2 + 1", "x^2 + 1 "),
                Arguments.of("varA = 3", "3 "),
                Arguments.of("varB = (1 + f(varA))", "11 "),
                Arguments.of("f(x) = 2x^2 + x * 10 - 125 + 2/i*x + 25 + 2^x + 200 + 10x ",
                        "2x^2 + 20x + 100 + 2 / i * x + 2 ^ x "),
                Arguments.of("varC = f(1)", "124 + 2 / i "),
                Arguments.of("varD = f(2)", "152 + 2 / i * 2 "),
                Arguments.of("z(x) = 4 * i * x", "4i * x "),
                Arguments.of("varC = z(1)", "4i "),
                Arguments.of("varD = z(2)", "8i ")
                );
    }

    private static Stream<Arguments> functions_testB() {
        return Stream.of(
                Arguments.of("f(x) = (2 *x ^2+ 5*x^2 + 3*x + 10 - 5 - x)", "7x^2 + 2x + 5 "),
                Arguments.of("y(x) = (x^3 + 10)", "x^3 + 10 "),
                Arguments.of("y(x) = (2 * x^5 - x^5 + 5)", "x^5 + 5 "),
                Arguments.of("z(x) = (2 * x^6 - 3 * x ^ 6 + x^ 6 + 11)", "11 "),
                Arguments.of("f(x) = (x^2 - x + 3 * x^2 + 10 - i ^2 - 2*i)", "4x^2 - x - 2i + 11 "),
                Arguments.of("f(x) = (10x^2 + 8 i - 11)", "10x^2 + 8i - 11 ")
        );
    }
}
