package io.bradenhart.shifty.database;

import android.content.Context;
import android.util.Log;

import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.ShiftDate.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static io.bradenhart.shifty.domain.ShiftDate.Month.*;
import static io.bradenhart.shifty.domain.ShiftDate.Weekday.*;
import static io.bradenhart.shifty.domain.ShiftTime.Hour.*;
import static io.bradenhart.shifty.domain.ShiftTime.Minute.*;
import static io.bradenhart.shifty.domain.ShiftTime.Period.*;

/**
 * Created by bradenhart on 3/04/17.
 */

public class TestData {

    private static int y = 2017;

    public static Shift[] shifts = new Shift[]{
            // 27 March
            new Shift(y, Calendar.MARCH, 29, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.MARCH, 31, 6, 0, 1, 8, 0, 1),
            new Shift(y, Calendar.APRIL, 1, 6, 0, 1, 8, 0, 1),
            new Shift(y, Calendar.APRIL, 2, 9, 0, 0, 4, 0, 1),

            // 3 April
            new Shift(y, Calendar.APRIL, 5, 9, 0, 0, 5, 30, 1),
            new Shift(y, Calendar.APRIL, 9, 9, 0, 0, 4, 0, 1),

            // 10 April
            new Shift(y, Calendar.APRIL, 10, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.APRIL, 12, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.APRIL, 13, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.APRIL, 15, 9, 0, 0, 5, 30, 1),

            // 17 April
            new Shift(y, Calendar.APRIL, 17, 9, 0, 0, 4, 0, 1),
            new Shift(y, Calendar.APRIL, 18, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.APRIL, 19, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.APRIL, 21, 8, 0, 0, 4, 30, 1),
            new Shift(y, Calendar.APRIL, 23, 9, 0, 0, 4, 0, 1),

            // 24 April
            new Shift(y, Calendar.APRIL, 24, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.APRIL, 26, 8, 0, 0, 4, 30, 1),
            new Shift(y, Calendar.APRIL, 30, 9, 0, 0, 4, 0, 1),

            // 1 May
            new Shift(y, Calendar.MAY, 1, 8, 0, 0, 4, 30, 1),
            new Shift(y, Calendar.MAY, 3, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.MAY, 7, 9, 0, 0, 4, 0, 1),

            // 8 May
            new Shift(y, Calendar.MAY, 9, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.MAY, 14, 9, 0, 0, 4, 0, 1)

            /* fake data */
            // 15 May
            , new Shift(y, Calendar.MAY, 15, 8, 0, 0, 4, 30, 1),
            new Shift(y, Calendar.MAY, 17, 9, 30, 0, 6, 0, 1),

            // 22 May
            new Shift(y, Calendar.MAY, 23, 9, 0, 0, 5, 30, 1),
            new Shift(y, Calendar.MAY, 24, 8, 0, 0, 4, 30, 1),
            new Shift(y, Calendar.MAY, 26, 8, 30, 0, 5, 0, 1),

            // 29 May
            new Shift(y, Calendar.MAY, 30, 9, 30, 0, 6, 0, 1)
    };

//    public static void addDataToDB(Context context) {
//        for (Shift s : shifts) {
//            new DatabaseManager(context).insertShift(s);
//        }
//    }

    public static void addDataToDB(Context context) {
        shifts = generateShifts();
        for (Shift s : shifts) {
            new DatabaseManager(context).insertShift(s);
        }
    }

    public static void deleteAllTestData(Context context) {
        new DatabaseManager(context).deleteAllShifts();
    }

    private static int generateMonth() {
        Random r = new Random();
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        int val = r.nextInt(2);
        int dir = r.nextInt(1);
        if (month == 0 && dir == 0) return month;
        if (dir == 0 && month - val < 0) return 0;
        return dir == 0 ? month - val : month + 1;
    }

    private static int generateStartHour() {
        // 6 -> 11
        // (0 -> 5) + 6
        return new Random().nextInt(5) + 6;
    }

    private static int generateEndHour() {
        // 1 -> 8
        // (0 -> 7) + 1
        int num = new Random().nextInt(7) + 1;
        if (num <= 0) {
            Log.e("RANDOM", num + "");
        }
        return num;
    }

    private static int generateMinute() {
        // 00 -> 55
        // (0 -> 11) * 5
        return new Random().nextInt(11) * 5;
    }

    private static int generateDayOfMonth(int month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, y);
        c.set(Calendar.MONTH, month);
        int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        return new Random().nextInt(max);
    }

    private static int generateMaxShiftsInWeek() {
        return new Random().nextInt(7) + 1;
    }

    public static Shift[] generateShifts() {
        List<Shift> shifts = new ArrayList<>();

        for (int m = 0; m < 12; m++) {
            Integer[] daysOfMonth = getUniqueDaysOfMonth(m);
            for (int d = 0; d < daysOfMonth.length; d++) {
                if (daysOfMonth[d] != 0) {
                    int startHour = generateStartHour();
                    int endHour = generateEndHour();
                    int startMin = generateMinute();
                    int endMin = generateMinute();
                    shifts.add(new Shift(y, m, daysOfMonth[d], startHour, startMin, 0, endHour, endMin, 1));
                    String shift = "Shift(" + y + ", " + m + ", " + daysOfMonth[d] + ", " + startHour + ", "
                            + startMin + ", 0, " + endHour + ", " + endMin + ", 1)";
                    Log.e("GEN", shift);
                }
            }
        }

        Shift[] s = new Shift[shifts.size()];
        shifts.toArray(s);
        return s;
    }

    public static Integer[] getWeekDaysForWorkWeek() {
        Random r1 = new Random();
        int numDays = r1.nextInt(5) + 1;
        Log.e("WEEKDAYS", "numDays: " + numDays + "");

        Set<Integer> weekdays = new HashSet<>();
        Random r2 = new Random();
        while (weekdays.size() < numDays) {
            int num = r2.nextInt(6) + 1;
            Log.e("WEEKDAYS", num + "");
            weekdays.add(num);
        }

        Integer[] output = new Integer[numDays];
        weekdays.toArray(output);
        for (int i = 0; i < numDays; i++) {
            Log.e("WEEKDAYS", i + ": " + "(" + output[i] + ")");
        }

        return output;
    }

    public static Integer[] getUniqueDaysOfMonth(int month) {
        String logTag = "MONTH";
        Log.e(logTag, "month: " + month);
        Random r1 = new Random();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month);
        int lastDayOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int numDays = r1.nextInt(lastDayOfMonth) + 1;
        Log.e(logTag, "numDays: " + numDays + "");

        Set<Integer> weekdays = new HashSet<>();
        Random r2 = new Random();
        while (weekdays.size() < numDays) {
            int num = r2.nextInt(lastDayOfMonth) + 1;
            Log.e(logTag, num + "");
            weekdays.add(num);
        }

        Integer[] output = new Integer[numDays];
        weekdays.toArray(output);
        Arrays.sort(output);
        for (int i = 0; i < numDays; i++) {
            Log.e(logTag, i + ": " + "(" + output[i] + ")");
        }


        for (int j = 4; j < numDays; j++) {
            if (j + 4 < numDays && output[j + 4] == output[j] + 4) {
                output[j] = 0;
                j += 2;
                if (j + 5 < numDays && output[j + 5] == output[j] + 5) {
                    output[j + 1] = 0;
                    j += 1;
                }
            }
        }

        return output;
    }


}
