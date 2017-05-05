package io.bradenhart.shifty.database;

import android.content.Context;
import android.util.Log;

import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.ShiftDate.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

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

    public static Shift[] shifts = new Shift[] {
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
            ,new Shift(y, Calendar.MAY, 15, 8, 0, 0, 4, 30, 1),
            new Shift(y, Calendar.MAY, 17, 9, 30, 0, 6, 0, 1),

            // 22 May
            new Shift(y, Calendar.MAY, 23, 9, 0, 0, 5, 30, 1),
            new Shift(y, Calendar.MAY, 24, 8, 0, 0, 4, 30, 1),
            new Shift(y, Calendar.MAY, 26, 8, 30, 0, 5, 0, 1),

            // 29 May
            new Shift(y, Calendar.MAY, 30, 9, 30, 0, 6, 0, 1)
    };

    public static void addDataToDB(Context context) {
        for (Shift s : shifts) {
            new DatabaseManager(context).insertShift(s);
        }
    }

    public static void addDataToDB(Context context, int weeks) {
        shifts = generateShifts(weeks);
        addDataToDB(context);
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
        return new Random().nextInt(7);
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

    public static Shift[] generateShifts(int weeks) {
        List<Shift> shifts = new ArrayList<>();

        for (int w = 0; w < weeks; w++) {
            int maxShifts = generateMaxShiftsInWeek();
            for (int d = 1; d <= maxShifts; d++) {
                int month = generateMonth();
                int startHour = generateStartHour();
                int endHour = generateEndHour();
                int startMin = generateMinute();
                int endMin = generateMinute();
                int day = generateDayOfMonth(month);
                shifts.add(new Shift(y, month, day, startHour, startMin, 0, endHour, endMin, 1));
                String shift = "Shift(" + y + ", " + month + ", " + day + ", " + startHour + ", "
                        + startMin + ", 0, " + endHour + ", " + endMin + ", 1)";
                Log.e("GEN", shift);
            }
        }

        Shift[] s = new Shift[shifts.size()];
        shifts.toArray(s);
        return s;
    }

}
