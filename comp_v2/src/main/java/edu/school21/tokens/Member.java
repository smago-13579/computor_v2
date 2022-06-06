package edu.school21.tokens;

import edu.school21.types.Type;

public class Member extends Token {
    private String name;
    private int power;
    private boolean isImaginary;

    public Member(String name) {
        this.name = name;
        this.type = Type.MEMBER;
        this.power = 1;
        this.num = 1;

        if (name.equalsIgnoreCase("i")) {
            isImaginary = true;
        }
    }

    public boolean isImaginary() {
        return isImaginary;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String getToken() {
        StringBuilder value = new StringBuilder();

        if (this.num != 0) {
            if (this.num % 1 == 0) {
                value.append((long) this.num);
            } else {
                value.append(this.num);
            }
        }
        value.append(this.name);

        if (this.power > 1) {
            value.append("^" + this.power);
        }
        return value.toString();
    }
}
