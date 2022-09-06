package edu.school21.tests;

import edu.school21.data.Data;
import edu.school21.exceptions.InvalidFormException;
import edu.school21.service.Service;
import edu.school21.tokens.Function;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComplexTest {
    Service service = Service.getInstance();
    Data data = Data.getInstance();

    @BeforeAll
    static void createVars() {
        Service.getInstance().perform("varA = [[1, 1, 1] ; [1, 1, 1];[1,1,1]]");
        Service.getInstance().perform("varB = [[2,2,2];[2,2,2];[2,2,2]]");
    }

    @ParameterizedTest
    @MethodSource("functionsForComplexTestA")
    public void complexTestA(String form, String expected) {
        service.perform(form);
        Function function = data.getFunction(form.substring(0, 1));
        assertEquals(expected, function.getValueToString());
    }

    @ParameterizedTest
    @MethodSource("functionsForComplexTestB")
    public void errorCheckComplex(String form) {
        assertThrows(InvalidFormException.class, () -> service.perform(form));
    }

    private static Stream<Arguments> functionsForComplexTestA() {
        return Stream.of(
                Arguments.of("f(x) = (x ^ 2 + x + 1) / (x ^ 2 + x + 1)", "1 "),
                Arguments.of("f(x) = (x^2 -i) / (x ^2 - i) * 3", "3 "),
                Arguments.of("f(x) = (x^2 - x - i + 1 + 3) / (2x^2 + 5x - i + 4 - 6X - x^2) * 2.5", "2.5 "),
                Arguments.of("f(x) = (x + x /i * 3 - 3) / (x + x / i * 3 -3) * 5", "5 "),
                Arguments.of("f(x) = (x + i) / (x ^ 2 + x + 1) / (x ^ 2 + x + 1)",
                        "( x + i ) / ( x^2 + x + 1 ) / ( x^2 + x + 1 ) "),
                Arguments.of("f(x) = (x + i) * (x ^ 2 + x + 1) / (x ^ 2 + x + 1)", "x + i "),
                Arguments.of("f(x) = (x + i) ^ (x ^ 2 + x + 1) / (x ^ 2 + x + 1)",
                        "( x + i ) ^ ( x^2 + x + 1 ) / ( x^2 + x + 1 ) "),
                Arguments.of("f(x) = (x + i) * (i - 1) * (i + 1)", "- 2x - 2i "),
                Arguments.of("f(x) = (x + i) / (i - 1) * (i + 1)", "( x + i ) / ( i - 1 ) * ( i + 1 ) "),
                Arguments.of("f(x) = (10x - 11)", "10x - 11 "),
                Arguments.of("f(x) = 2 ^ f(x)", "2 ^ ( 10x - 11 ) ")
        );
    }

    private static Stream<Arguments> functionsForComplexTestB() {
        return Stream.of(
                Arguments.of("f(x) = varA + 2 ^ ( 10x - 11 )"),
                Arguments.of("f(x) = varB - (x + i) / (x + 1)")
        );
    }
}
