package edu.school21.tokens;

public class Number extends Token {

    public Number(float f) {
        super(f);
    }

    @Override
    public String getToken() {
        StringBuilder value = new StringBuilder();

        if (this.num < 0) {
            value.append("- ");
        }

        if (this.num % 1 == 0) {
            return value.append(abs((long)this.num) + " ").toString();
        }
        return value.append(abs(this.num) + " ").toString();
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
