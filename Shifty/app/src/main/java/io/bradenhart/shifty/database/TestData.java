package io.bradenhart.shifty.database;

import android.content.Context;

import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.ShiftDate.*;

import java.util.Calendar;

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

    public static void deleteAllTestData(Context context) {
        new DatabaseManager(context).deleteAllShifts();
    }
}
