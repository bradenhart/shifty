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
//            new Shift(new ShiftDate(29, WEDNESDAY, MARCH, y), new ShiftTime(NINE, THIRTY, AM), new ShiftTime(SIX, ZERO, PM)),
//            new Shift(new ShiftDate(31, FRIDAY, MARCH, y), new ShiftTime(SIX, ZERO, PM), new ShiftTime(EIGHT, ZERO, PM)),
//            new Shift(new ShiftDate(1, SATURDAY, APRIL, y), new ShiftTime(SIX, ZERO, PM), new ShiftTime(EIGHT, ZERO, PM)),
//            new Shift(new ShiftDate(2, SUNDAY, APRIL, y), new ShiftTime(NINE, ZERO, AM), new ShiftTime(FOUR, ZERO, PM)),
//
//            new Shift(new ShiftDate(5, WEDNESDAY, APRIL, y), new ShiftTime(NINE, ZERO, AM), new ShiftTime(ShiftTime.Hour.FIVE, THIRTY, PM)),
//            new Shift(new ShiftDate(9, SUNDAY, APRIL, y), new ShiftTime(NINE, ZERO, AM), new ShiftTime(FOUR, ZERO, PM)),
//
//            new Shift(new ShiftDate(10, MONDAY, APRIL, y), new ShiftTime(NINE, THIRTY, AM), new ShiftTime(SIX, ZERO, PM)),
//            new Shift(new ShiftDate(12, WEDNESDAY, APRIL, y), new ShiftTime(NINE, THIRTY, AM), new ShiftTime(SIX, ZERO, PM)),
//            new Shift(new ShiftDate(13, THURSDAY, APRIL, y), new ShiftTime(NINE, THIRTY, AM), new ShiftTime(SIX, ZERO, PM)),
//            new Shift(new ShiftDate(15, SATURDAY, APRIL, y), new ShiftTime(NINE, ZERO, AM), new ShiftTime(ShiftTime.Hour.FIVE, THIRTY, PM)),
//
//            new Shift(new ShiftDate(17, MONDAY, APRIL, y), new ShiftTime(NINE, ZERO, AM), new ShiftTime(FOUR, ZERO, PM)),
//            new Shift(new ShiftDate(18, TUESDAY, APRIL, y), new ShiftTime(NINE, THIRTY, AM), new ShiftTime(SIX, ZERO, PM)),
//            new Shift(new ShiftDate(19, WEDNESDAY, APRIL, y), new ShiftTime(NINE, THIRTY, AM), new ShiftTime(SIX, ZERO, PM)),
//            new Shift(new ShiftDate(21, FRIDAY, APRIL, y), new ShiftTime(EIGHT, ZERO, AM), new ShiftTime(FOUR, THIRTY, PM)),
//            new Shift(new ShiftDate(23, SUNDAY, APRIL, y), new ShiftTime(NINE, ZERO, AM), new ShiftTime(FOUR, ZERO, PM))
//            new Shift("2017-03-29 09:30:00 AM", "2017-03-31 06:00:00 PM")
            new Shift(y, Calendar.MARCH, 29, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.MARCH, 31, 6, 0, 1, 8, 0, 1),
            new Shift(y, Calendar.APRIL, 1, 6, 0, 1, 8, 0, 1),
            new Shift(y, Calendar.APRIL, 2, 9, 0, 0, 4, 0, 1),

            new Shift(y, Calendar.APRIL, 5, 9, 0, 0, 5, 30, 1),
            new Shift(y, Calendar.APRIL, 9, 9, 0, 0, 4, 0, 1),

            new Shift(y, Calendar.APRIL, 10, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.APRIL, 12, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.APRIL, 13, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.APRIL, 15, 9, 0, 0, 5, 30, 1),

            new Shift(y, Calendar.APRIL, 17, 9, 0, 0, 4, 0, 1),
            new Shift(y, Calendar.APRIL, 18, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.APRIL, 19, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.APRIL, 21, 8, 0, 0, 4, 30, 1),
            new Shift(y, Calendar.APRIL, 23, 9, 0, 0, 4, 0, 1),

            new Shift(y, Calendar.APRIL, 24, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.APRIL, 26, 8, 0, 0, 4, 30, 1),
            new Shift(y, Calendar.APRIL, 30, 9, 0, 0, 4, 0, 1),

            new Shift(y, Calendar.MAY, 1, 8, 0, 0, 4, 30, 1),
            new Shift(y, Calendar.MAY, 3, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.MAY, 7, 9, 0, 0, 4, 0, 1),

            new Shift(y, Calendar.MAY, 9, 9, 30, 0, 6, 0, 1),
            new Shift(y, Calendar.MAY, 14, 9, 0, 0, 4, 0, 1)
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
