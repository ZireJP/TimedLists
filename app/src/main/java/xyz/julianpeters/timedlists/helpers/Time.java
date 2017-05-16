package xyz.julianpeters.timedlists.helpers;

/**
 * Created by julian on 10.05.17.
 */

public class Time {
    public static int getTimeInSeconds(int hour, int minute, int second) {
        minute = minute + hour * 60;
        return second + minute * 60;
    }

    public static int getSeconds(int seconds) {
        return seconds % 60;
    }

    public static int getMinutes(int seconds) {
        seconds = seconds / 60;
        return seconds % 60;
    }

    public static int getHours(int seconds) {
        return seconds / 3600;
    }

    public static String getTimeString(int seconds) {
        int second = seconds % 60;
        int minutes = seconds / 60;
        int minute = minutes % 60;
        int hour = minutes / 60;
        return valueAsString(hour) + ":" + valueAsString(minute) + ":" + valueAsString(second);
    }

    private static String valueAsString(int value) {
        if (value < 10) {
            return "0" + value;
        } else {
            return Integer.toString(value);
        }
    }
}
