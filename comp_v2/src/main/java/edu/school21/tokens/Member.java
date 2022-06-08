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

        if (this.num < 0) {
            value.append(" - ");
        }

        if (abs(this.num) != 1) {
            if (this.num % 1 == 0) {
                value.append(abs((long) this.num));
            } else {
                value.append(abs(this.num));
            }
        }
        value.append(this.name);

        if (this.power > 1) {
            value.append("^" + this.power);
        }
        return value.toString();
    }

    private float abs(float f) {
        if (f < 0) {
            return -f;
        }
        return f;
    }

    private long abs(long l) {
        if (l < 0) {
            return -l;
        }
        return l;
    }
}
