package edu.school21.tests;

import edu.school21.data.Data;
import edu.school21.exceptions.InvalidFormException;
import edu.school21.exceptions.InvalidPowerException;
import edu.school21.service.Service;
import edu.school21.tokens.Variable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatrixTest {
    Service service = Service.getInstance();
    Data data = Data.getInstance();

    @BeforeAll
    static void createVars() {
        Service.getInstance().perform("varA = [[1, 1, 1] ; [1, 1, 1];[1,1,1]]");
        Service.getInstance().perform("varB = [[2,2,2];[2,2,2];[2,2,2]]");
        Service.getInstance().perform("f(x) = 2 * x");
        Service.getInstance().perform("varT = [[1, 1, 1]]");
    }

    @ParameterizedTest
    @ValueSource(strings = {"C = varA * varB", "C = varA * i", "c = [[1, 1, 1]] * i", "c = varA + 10",
            "c = [[1, 1, 1]] + 5", "c = varA % 2"})
    public void errorCheckMatrixA(String form) {
        assertThrows(InvalidFormException.class, () -> service.perform(form));
    }

    @ParameterizedTest
    @ValueSource(strings = {"C = 2 ^ varA"})
    public void errorCheckMatrixB(String form) {
        assertThrows(InvalidPowerException.class, () -> service.perform(form));
    }

    @ParameterizedTest
    @MethodSource("matrix_testA")
    public void calcMatrixA(String form, String expected) {
        service.perform(form);
        Variable variable = data.getVariable(form.substring(0, 4));
        assertEquals(expected, variable.getValueToString());
    }

    private static Stream<Arguments> matrix_testA() {
        return Stream.of(
                Arguments.of("varC = varA ** varB", "[ 6 , 6 , 6 ]\n[ 6 , 6 , 6 ]\n[ 6 , 6 , 6 ]\n"),
                Arguments.of("varD = varB - varA", "[ 1 , 1 , 1 ]\n[ 1 , 1 , 1 ]\n[ 1 , 1 , 1 ]\n"),
                Arguments.of("varD = varA + varB", "[ 3 , 3 , 3 ]\n[ 3 , 3 , 3 ]\n[ 3 , 3 , 3 ]\n"),
                Arguments.of("varX = varB * 2", "[ 4 , 4 , 4 ]\n[ 4 , 4 , 4 ]\n[ 4 , 4 , 4 ]\n"),
                Arguments.of("varY = varX * 2", "[ 8 , 8 , 8 ]\n[ 8 , 8 , 8 ]\n[ 8 , 8 , 8 ]\n"),
                Arguments.of("varC = varB ^ 2", "[ 12 , 12 , 12 ]\n[ 12 , 12 , 12 ]\n[ 12 , 12 , 12 ]\n"),
                Arguments.of("varC = f(varT)", "[ 2 , 2 , 2 ]\n")
                );
    }
}
