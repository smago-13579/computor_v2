package edu.school21.tests;

import edu.school21.analyze.Lexer;
import edu.school21.analyze.Parser;
import edu.school21.service.Service;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PreCalculateTest {
    Lexer lexer = Lexer.getInstance();
    Parser parser = Parser.getInstance();
    Service service = Service.getInstance();

    @ParameterizedTest
    @ValueSource(strings = {"f(x) = x ^ 2 + 1", "y(x) = x - 1", "a(x) = f(x) * y(x)",
            "f(x) = x * (x + i + 1)", "f(x) = 10 * (x + i + 2)", "f(x) = (i + 1) * (i - 1) + x /i",
            "f(x) = 4 * i * (i + 1) - 2 * (x + 10) / 2 * x", "f(x) = (x + 1) ^ 2", "f(x) = (x - 1) ^ 4",
            "f(x) = (x + 1) * (i + 1)" , "f(x) = (x + 1) * (x - 1) + x / i", "f(x) = (x^2 - x + 1) * (x - 1)"})
    public void multiplyTestA(String form) {
        service.perform(form);
    }

    @ParameterizedTest
    @ValueSource(strings = {"f(x) = (x ^ 2 + x + 1) / (x ^ 2 + x + 1)", "f(x) = (x^2 -i) / (x ^2 - i) * 3",
            "f(x) = (x^2 - x - i + 1 + 3) / (2x^2 + 5x - i + 4 - 6X - x^2) * 2.5",
            "f(x) = (x + x /i * 3 - 3) / (x + x / i * 3 -3) * 5"})
    public void divideTestA(String form) {
        service.perform(form);
    }

    @ParameterizedTest
    @ValueSource(strings = {"f(x) = (x + i) / (x ^ 2 + x + 1) / (x ^ 2 + x + 1)",
            "f(x) = (x + i) * (x ^ 2 + x + 1) / (x ^ 2 + x + 1)",
            "f(x) = (x + i) ^ (x ^ 2 + x + 1) / (x ^ 2 + x + 1)",
            "f(x) = (x + i) * (i - 1) * (i + 1)", "f(x) = (x + i) / (i - 1) * (i + 1)",
            "f(x) = (x + i) ^ (i - 1) * (i + 1)"})
    public void divideTestB(String form) {
        service.perform(form);
    }
}
