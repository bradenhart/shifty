package io.bradenhart.shifty.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import io.bradenhart.shifty.domain.Shift;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    public static ArrayList<String[]> shiftData = new ArrayList<String[]>() {};

    private static void createShiftData() {
        // [0] week start datetime YYYY-MM-DD 00:00:00.000
        // [1] week end datetime    YYYY-MM-DD 11:59:59.999
        // [2] shift start datetime YYYY-MM-DD HH:MM:SS.sss
        // [3] shift end datetime   YYYY-MM-DD HH:MM:SS.sss
        shiftData.add(new String[] { "2017-03-27 00:00:00.000", "2017-04-02 23:59:59.999", "2017-03-29 09:30:00.000", "2017-03-29 18:00:00.000" });
        shiftData.add(new String[] { "2017-03-27 00:00:00.000", "2017-04-02 23:59:59.999", "2017-03-31 18:00:00.000", "2017-03-31 20:00:00.000" });
        shiftData.add(new String[] { "2017-03-27 00:00:00.000", "2017-04-02 23:59:59.999", "2017-04-01 18:00:00.000", "2017-04-01 20:00:00.000" });
        shiftData.add(new String[] { "2017-03-27 00:00:00.000", "2017-04-02 23:59:59.999", "2017-04-02 09:00:00.000", "2017-04-02 16:00:00.000" });

        shiftData.add(new String[] { "2017-04-03 00:00:00.000", "2017-04-09 23:59:59.999", "2017-04-05 09:00:00.000", "2017-04-04 17:30:00.000" });
        shiftData.add(new String[] { "2017-04-03 00:00:00.000", "2017-04-09 23:59:59.999", "2017-04-09 09:00:00.000", "2017-04-09 16:00:00.000" });

        shiftData.add(new String[] { "2017-04-10 00:00:00.000", "2017-04-16 23:59:59.999", "2017-04-10 09:30:00.000", "2017-04-10 18:00:00.000" });
        shiftData.add(new String[] { "2017-04-10 00:00:00.000", "2017-04-16 23:59:59.999", "2017-04-12 09:30:00.000", "2017-14-12 18:00:00.000" });
        shiftData.add(new String[] { "2017-04-10 00:00:00.000", "2017-04-16 23:59:59.999", "2017-04-13 09:30:00.000", "2017-04-13 18:00:00.000" });
        shiftData.add(new String[] { "2017-04-10 00:00:00.000", "2017-04-16 23:59:59.999", "2017-04-15 09:00:00.000", "2017-04-15 17:30:00.000" });

        shiftData.add(new String[] { "2017-04-17 00:00:00.000", "2017-04-23 23:59:59.999", "2017-04-17 09:00:00.000", "2017-04-17 16:00:00.000" });
        shiftData.add(new String[] { "2017-04-17 00:00:00.000", "2017-04-23 23:59:59.999", "2017-04-18 09:30:00.000", "2017-04-18 18:00:00.000" });
        shiftData.add(new String[] { "2017-04-17 00:00:00.000", "2017-04-23 23:59:59.999", "2017-04-19 09:30:00.000", "2017-04-19 18:00:00.000" });
        shiftData.add(new String[] { "2017-04-17 00:00:00.000", "2017-04-23 23:59:59.999", "2017-04-21 08:00:00.000", "2017-04-21 16:30:00.000" });
        shiftData.add(new String[] { "2017-04-17 00:00:00.000", "2017-04-23 23:59:59.999", "2017-04-23 09:00:00.000", "2017-04-23 16:00:00.000" });

        shiftData.add(new String[] { "2017-04-24 00:00:00.000", "2017-04-30 23:59:59.999", "2017-04-24 09:30:00.000", "2017-04-24 18:00:00.000" });
        shiftData.add(new String[] { "2017-04-24 00:00:00.000", "2017-04-30 23:59:59.999", "2017-04-26 08:00:00.000", "2017-04-30 16:30:00.000" });
        shiftData.add(new String[] { "2017-04-24 00:00:00.000", "2017-04-30 23:59:59.999", "2017-04-30 09:00:00.000", "2017-04-30 16:00:00.000" });

        shiftData.add(new String[] { "2017-05-01 00:00:00.000", "2017-05-07 23:59:59.999", "2017-05-01 08:00:00.000", "2017-05-01 16:30:00.000" });
        shiftData.add(new String[] { "2017-05-01 00:00:00.000", "2017-05-07 23:59:59.999", "2017-05-03 09:30:00.000", "2017-05-03 18:00:00.000" });
        shiftData.add(new String[] { "2017-05-01 00:00:00.000", "2017-05-07 23:59:59.999", "2017-05-07 09:00:00.000", "2017-05-07 16:00:00.000" });

        shiftData.add(new String[] { "2017-05-08 00:00:00.000", "2017-05-14 23:59:59.999", "2017-05-09 09:30:00.000", "2017-05-09 18:00:00.000" });
        shiftData.add(new String[] { "2017-05-08 00:00:00.000", "2017-05-14 23:59:59.999", "2017-05-14 09:00:00.000", "2017-05-14 16:00:00.000" });

        shiftData.add(new String[] { "2017-05-15 00:00:00.000", "2017-05-21 23:59:59.999", "2017-05-15 08:00:00.000", "2017-05-15 16:30:00.000" });
        shiftData.add(new String[] { "2017-05-15 00:00:00.000", "2017-05-21 23:59:59.999", "2017-05-17 09:30:00.000", "2017-05-17 18:00:00.000" });

        shiftData.add(new String[] { "2017-05-22 00:00:00.000", "2017-05-22 23:59:59.999", "2017-05-23 09:00:00.000", "2017-05-23 17:30:00.000" });
        shiftData.add(new String[] { "2017-05-22 00:00:00.000", "2017-05-22 23:59:59.999", "2017-05-24 08:00:00.000", "2017-05-24 16:30:00.000" });
        shiftData.add(new String[] { "2017-05-22 00:00:00.000", "2017-05-22 23:59:59.999", "2017-05-26 08:30:00.000", "2017-05-26 17:00:00.000" });

        shiftData.add(new String[] { "2017-05-29 00:00:00.000", "2017-06-04 23:59:59.999", "2017-05-30 09:30:00.000", "2017-05-30 18:00:00.000"});
    }

//    public static void addDataToDB(Context context) {
//        for (Shift s : shifts) {
//            new DatabaseManager(context).insertShift(s);
//        }
//    }

    public static void addDataToDB(ContentResolver contentResolver) {
        if (shiftData.size() == 0) createShiftData();
        for (String[] shift : shiftData) {
            ContentValues values = new ContentValues();
            values.put(ShiftyContract.Shift.COLUMN_WEEK_START_DATETIME, shift[0]);
            values.put(ShiftyContract.Shift.COLUMN_WEEK_END_DATETIME, shift[1]);
            values.put(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME,shift[2]);
            values.put(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME, shift[3]);
            Uri uri = contentResolver.insert(ShiftyContract.Shift.CONTENT_URI, values);

            Log.d("TESTDATA", uri != null ? uri.toString() : "null uri");
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

        for (int m = 4; m < 8; m++) {
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
