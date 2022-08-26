package edu.school21.tests;

import edu.school21.analyze.Lexer;
import edu.school21.analyze.Parser;
import edu.school21.exceptions.InvalidFormException;
import edu.school21.service.Service;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatrixTest {
    Service service = Service.getInstance();

    @BeforeAll
    static void createVars() {
        Service.getInstance().perform("varA = [[1, 1, 1] ; [1, 1, 1];[1,1,1]]");
        Service.getInstance().perform("varB = [[2,2,2];[2,2,2];[2,2,2]]");
    }

    @ParameterizedTest
    @ValueSource(strings = {"varA = [[1]; [1]]\tvarB = [[2]; [2]]\tvarC = varA*varB"})
    public void errorCheckMatrix(String form) {
        String[] parts = form.split("\t");

        service.perform(parts[0]);
        service.perform(parts[1]);
        assertThrows(InvalidFormException.class, () -> service.perform(parts[2]));
    }

    @ParameterizedTest
    @ValueSource(strings = {"varC = varA ** varB", "varD = varB - varA", "varD = varA + varB",
            "varX = varB * 2", "varY = varX * 2"})
    public void calcMatrixA(String form) {
        service.perform(form);
    }
}
