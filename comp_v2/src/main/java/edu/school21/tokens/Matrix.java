package edu.school21.tokens;

import edu.school21.exceptions.InvalidFormException;
import edu.school21.types.Type;

import java.util.ArrayList;
import java.util.List;

public class Matrix extends Token {
    private List<List<Integer>> matrix;

    public Matrix(String str) {
        matrix = new ArrayList<>();
        this.type = Type.MATRIX;
        str = str.replace(" ", "");
        String[] arr = str.split(";");

        try {
            for (String s : arr) {
                if (s.charAt(0) != '[' || s.charAt(s.length() - 1) != ']') {
                    throw new RuntimeException(s);
                }
                s = s.replace("[", "").replace("]", "");

                String[] tmp = s.split(",");
                ArrayList<Integer> list = new ArrayList<>();

                for (String value : tmp) {
                    list.add(Integer.parseInt(value));
                }
                matrix.add(list);
            }
            int size = matrix.get(0).size();

            for (List<Integer> list : matrix) {
                if (list.size() != size) {
                    throw new RuntimeException("matrix size incorrect");
                }
            }
        } catch (RuntimeException e) {
            throw new InvalidFormException("Invalid matrix: " + e.getMessage());
        }
    }

    public List<List<Integer>> getMatrix() {
        return matrix;
    }

    public void setMatrix(List<List<Integer>> matrix) {
        this.matrix = matrix;
    }

    @Override
    public String getToken() {
        StringBuilder str = new StringBuilder();

        for (List<Integer> list : matrix) {
            str.append("[ ");

            for (int i = 0; i < list.size(); i++) {
                str.append(list.get(i)).append(" ");

                if (i + 1 < list.size()) {
                    str.append(", ");
                }
            }
            str.append("]\n");
        }
        return str.toString();
    }
}
