package edu.school21.tests;

import edu.school21.service.Service;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ComplexTest {
    Service service = Service.getInstance();

    @ParameterizedTest
    @MethodSource("functionsForComplexTestA")
    public void complexTestA(String form) {
        service.perform(form);
    }

    private static Stream<Arguments> functionsForComplexTestA() {
        return Stream.of(
                Arguments.of("f(x) = (x ^ 2 + x + 1) / (x ^ 2 + x + 1)", "1 "),
                Arguments.of("f(x) = (x^2 -i) / (x ^2 - i) * 3", "3 "),
                Arguments.of("f(x) = (x^2 - x - i + 1 + 3) / (2x^2 + 5x - i + 4 - 6X - x^2) * 2.5", "2.5 "),
                Arguments.of("f(x) = (x + x /i * 3 - 3) / (x + x / i * 3 -3) * 5", "5 "),
                Arguments.of("f(x) = (x + i) / (x ^ 2 + x + 1) / (x ^ 2 + x + 1)",
                        "(x + i) / (x ^ 2 + x + 1) / (x ^ 2 + x + 1) "),
                Arguments.of("f(x) = (x + i) * (x ^ 2 + x + 1) / (x ^ 2 + x + 1)", "x + i "),
                Arguments.of("f(x) = (x + i) ^ (x ^ 2 + x + 1) / (x ^ 2 + x + 1)",
                        "( x + i ) ^ ( x^2 + x + 1 ) / ( x^2 + x + 1 ) "),
                Arguments.of("f(x) = (x + i) * (i - 1) * (i + 1)", "- 2x - 2i "),
                Arguments.of("f(x) = (x + i) / (i - 1) * (i + 1)", "( x + i ) / ( i - 1 ) * ( i + 1 ) "),
                Arguments.of("f(x) = (x + i) ^ (i - 1) * (i + 1)", "( x + i ) ^ ( i - 1 ) * ( i + 1 ) ")
        );
    }
}
