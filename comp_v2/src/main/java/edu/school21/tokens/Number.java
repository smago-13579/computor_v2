package edu.school21.tokens;

public class Number extends Token {

    public Number(float f) {
        super(f);
    }

    @Override
    public String getToken() {
        if (this.num % 1 == 0) {
            return String.valueOf((long)this.num);
        }
        return String.valueOf(this.num);
    }
}
