package edu.school21.math;

import java.util.ArrayList;

public class SquareRoot {

    public static float square(float disc) {
        ArrayList<Long> numbers = new ArrayList<>();
        ArrayList<Long> fraction = new ArrayList<>();
        float value = 0;
        float interim = 0;

        /*       System.out.println("discriminant: " + disc);   **/
        String[] str = String.valueOf(disc).split("\\.");
        if (str[0].length() % 2 != 0)
            str[0] = "0" + str[0];
        for (int i = 0; i + 1 < str[0].length(); i += 2) {
            String tmp = str[0].substring(i, i + 2);
            numbers.add(Long.parseLong(tmp));
        }
        if (str[1].length() % 2 != 0)
            str[1] += "0";
        for (int i = 0; i + 1 < str[1].length(); i += 2) {
            String tmp = str[1].substring(i, i + 2);
            fraction.add(Long.parseLong(tmp));
        }

        for (int i = 0; i < numbers.size(); i++) {
            if (i == 0) {
                for (value = 1; (value + 1) * (value + 1) <= numbers.get(i); value++);
                interim = numbers.get(i) - (value * value);
            } else {
                interim = interim * 100 + numbers.get(i);
                float tmp = value * 20;
                float num = 9;
                for ( ; (tmp + num) * num > interim; num--);
                value = value * 10 + num;
                interim = interim - (tmp + num) * num;
            }
        }
        int i = 0;
        for ( ; i < fraction.size(); i++) {
            interim = interim * 100 + fraction.get(i);
            float tmp = value * 20;
            float num = 9;
            for ( ; (tmp + num) * num > interim; num--);
            value = value * 10 + num;
            interim = interim - (tmp + num) * num;
        }
        if (interim != 0) {
            for (int count = 0; count < 10; count++, i++) {
                interim = interim * 100;
                float tmp = value * 20;
                float num = 9;
                for ( ; (tmp + num) * num > interim; num--);
                value = value * 10 + num;
                interim = interim - (tmp + num) * num;
            }
        }
        for ( ; i > 0; i--) {
            value /= 10;
        }
        /*        System.out.println("square root: " + value);     **/

        return value;
    }
}
