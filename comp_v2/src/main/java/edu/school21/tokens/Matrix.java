package edu.school21.tokens;

import edu.school21.exceptions.InvalidFormException;
import edu.school21.types.Type;

import java.util.ArrayList;
import java.util.List;

public class Matrix extends Token {
    private List<List<Integer>> matrix;

    public Matrix(Matrix m) {
        copyMatrix(m.getMatrix());
        this.type = Type.MATRIX;
        this.num = 1;
    }

    public Matrix(List<List<Integer>> m) {
        copyMatrix(m);
        this.type = Type.MATRIX;
        this.num = 1;
    }

    public Matrix(String str) {
        matrix = new ArrayList<>();
        this.type = Type.MATRIX;
        this.num = 1;
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

    public void copyMatrix(List<List<Integer>> m) {
        this.matrix = new ArrayList<>();

        for (List<Integer> tmp : m) {
            List<Integer> list = new ArrayList<>(tmp);
            this.matrix.add(list);
        }
    }

    public List<List<Integer>> getMatrix() {
        return matrix;
    }

    public void setMatrix(List<List<Integer>> matrix) {
        this.matrix = matrix;
    }

    public int[] getSize() {
        int[] size = new int[2];

        size[0] = this.matrix.size();
        size[1] = this.matrix.get(0).size();
        return size;
    }

    public List<List<Integer>> addition(List<List<Integer>> tmp) {
        if (this.matrix.size() != tmp.size() || this.matrix.get(0).size() != tmp.get(0).size()) {
            return null;
        }
        List<List<Integer>> newList = new ArrayList<>();

        for (int i = 0; i < matrix.size(); i++) {
            List<Integer> list = new ArrayList<>();

            for (int j = 0; j < matrix.get(0).size(); j++) {
                list.add(matrix.get(i).get(j) + tmp.get(i).get(j));
            }
            newList.add(list);
        }
        setMatrix(newList);
        return newList;
    }

    @Override
    public void setNegative() {
        for (List<Integer> list : matrix) {
            for (int i = 0; i < list.size(); i++) {
                list.set(i, -list.get(i));
            }
        }
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
