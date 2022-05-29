package edu.school21.tokens;

import edu.school21.types.Type;

import java.util.List;

public abstract class Token {
    protected Type type;
    protected float num;

    protected Token() {

    }

    protected Token(float f) {
        this.num = f;
        this.type = Type.NUMBER;
    }

//    protected Token(String name) {
//        if (name.length() == 1) {
//            this.type = Type.X1;
//        } else if (name.charAt(2) == '0') {
//            this.type = Type.X0;
//        } else if (name.charAt(2) == '1') {
//            this.type = Type.X1;
//        } else {
//            this.type = Type.X2;
//        }
//        this.num = 1;
//
//        if (this.type == Type.X1)
//            this.name = name.substring(0, 1);
//        else
//            this.name = name;
//    }

    public Type getType() {
        return type;
    }

    public float getNum() {
        return num;
    }

//    public void setNum(float num) {
//        this.num = num;
//
//        if (this.num == 0) {
//            this.type = Type.NUMBER;
//            this.name = "";
//        }
//    }

    public void setNum(char c) {
        if (c == '-')
            this.num = -this.num;
    }

    public void setType(Type type) {
        this.type = type;
    }

//    public void setType(int x) {
//        if (x == 0) {
//            this.type = Type.NUMBER;
//            this.name = "";
//            return;
//        }
//
//        if (x == 1) {
//            this.type = Type.X1;
//            this.name = this.name.substring(0, 1);
//            return;
//        }
//        this.type = Type.X2;
//        this.name = this.name.substring(0, 1) + "^" + x;
//    }

    public abstract String getToken();
//    public String getToken() {
//        if (type == Type.OPERATOR) {
//            return Character.toString(op);
//        }
//
//        if (type == Type.NUMBER) {
//            return Float.toString(num);
//        }
//
//        if (this.num == -1) {
//            return "- " + this.name;
//        }
//
//        if (this.num != 1) {
//            String string = Float.toString(num) + " * " + this.name;
//            return string;
//        }
//        return this.name;
//    }

    public static void printTokens(List<Token> list) {
        for (int i = 0; i < list.size(); i++) {
//            if (i != 0 && list.get(i - 1).getType() != Type.OPERATOR &&
//                    list.get(i).getNum() > 0) {
//                System.out.print("+ " + list.get(i).getToken() + " ");
//            } else {
                System.out.print(list.get(i).getToken() + " ");
//            }
        }
        System.out.println();
    }
}