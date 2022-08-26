package edu.school21.tests;

import edu.school21.tokens.Number;
import edu.school21.tokens.Operator;
import edu.school21.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ExampleTest {

    @Test
    public void extLst() {
        List<Token> list = new LinkedList<>();
        list.add(new Operator("("));
        list.add(new Number(1));
        list.add(new Number(2));
        list.add(new Number(3));
        list.add(new Operator(")"));

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
        list.add(1, new Operator("*"));
        System.out.println("---------------");
        list.forEach(t -> System.out.print(t.getToken() + " "));
        System.out.println();

        int[] A = new int[2];
        int[] B = new int[2];
        A[0] = 10;
        B[0] = 10;
        A[1] = 20;
        B[1] = 20;
        System.out.println("EQUALS: " + (Arrays.equals(A, B)));
    }
}
