package xyz.julianpeters.timedlists.helpers;

/**
 * Created by julian on 20.05.17.
 */

public class StaticValues {
    public static int nestedLevel = 0;

    public static float hue() {
        if (nestedLevel == 0) {
            //CYAN 00BCD4#
            return 186.79f;
        } else if (nestedLevel == 1) {
            // ORANGE #FF9800
            return 35.67f;
        } else {
            //AMBER #FFC107
            return 45;
        }
    }

    public static float sat() {
        if (nestedLevel == 0) {
            return 1;
        } else if (nestedLevel == 1) {
            return 1;
        } else {
            return 0.9725f;
        }
    }

    public static float bright() {
        if (nestedLevel == 0) {
            return 0.8314f;
        } else if (nestedLevel == 1) {
            return 1;
        } else {
            return 1;
        }
    }

    public static float[] hsvValues(int position) {
        float ch = (float)position/20;
        int max = 7;
        float chm = (float)max/20;
        if (position < max) {
            return new float[]{hue(), sat() - ch, bright()};
        } else {
            return new float[]{hue(), sat() - chm, bright()};
        }
    }
}
