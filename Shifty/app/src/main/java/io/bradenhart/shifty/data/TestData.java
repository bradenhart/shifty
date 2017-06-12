package io.bradenhart.shifty.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.Log;

import java.util.ArrayList;

/**
 * Creates and inserts test data into the database.
 *
 * @author bradenhart
 */
public class TestData {

    public static ArrayList<String[]> shiftData = new ArrayList<String[]>() {};

    private static void createShiftData() {
        // [0] shift start datetime YYYY-MM-DD HH:MM:SS.sss
        // [1] shift end datetime   YYYY-MM-DD HH:MM:SS.sss
        shiftData.add(new String[] { "2017-03-29 09:30:00.000", "2017-03-29 18:00:00.000" });
        shiftData.add(new String[] { "2017-03-31 18:00:00.000", "2017-03-31 20:00:00.000" });
        shiftData.add(new String[] { "2017-04-01 18:00:00.000", "2017-04-01 20:00:00.000" });
        shiftData.add(new String[] { "2017-04-02 09:00:00.000", "2017-04-02 16:00:00.000" });

        shiftData.add(new String[] { "2017-04-05 09:00:00.000", "2017-04-05 17:30:00.000" });
        shiftData.add(new String[] { "2017-04-09 09:00:00.000", "2017-04-09 16:00:00.000" });

        shiftData.add(new String[] { "2017-04-10 09:30:00.000", "2017-04-10 18:00:00.000" });
        shiftData.add(new String[] { "2017-04-12 09:30:00.000", "2017-04-12 18:00:00.000" });
        shiftData.add(new String[] { "2017-04-13 09:30:00.000", "2017-04-13 18:00:00.000" });
        shiftData.add(new String[] { "2017-04-15 09:00:00.000", "2017-04-15 17:30:00.000" });

        shiftData.add(new String[] { "2017-04-17 09:00:00.000", "2017-04-17 16:00:00.000" });
        shiftData.add(new String[] { "2017-04-18 09:30:00.000", "2017-04-18 18:00:00.000" });
        shiftData.add(new String[] { "2017-04-19 09:30:00.000", "2017-04-19 18:00:00.000" });
        shiftData.add(new String[] { "2017-04-21 08:00:00.000", "2017-04-21 16:30:00.000" });
        shiftData.add(new String[] { "2017-04-23 09:00:00.000", "2017-04-23 16:00:00.000" });

        shiftData.add(new String[] { "2017-04-24 09:30:00.000", "2017-04-24 18:00:00.000" });
        shiftData.add(new String[] { "2017-04-26 08:00:00.000", "2017-04-26 16:30:00.000" });
        shiftData.add(new String[] { "2017-04-30 09:00:00.000", "2017-04-30 16:00:00.000" });

        shiftData.add(new String[] { "2017-05-01 08:00:00.000", "2017-05-01 16:30:00.000" });
        shiftData.add(new String[] { "2017-05-03 09:30:00.000", "2017-05-03 18:00:00.000" });
        shiftData.add(new String[] { "2017-05-07 09:00:00.000", "2017-05-07 16:00:00.000" });

        shiftData.add(new String[] { "2017-05-09 09:30:00.000", "2017-05-09 18:00:00.000" });
        shiftData.add(new String[] { "2017-05-14 09:00:00.000", "2017-05-14 16:00:00.000" });

        shiftData.add(new String[] { "2017-05-15 08:00:00.000", "2017-05-15 16:30:00.000" });
        shiftData.add(new String[] { "2017-05-17 09:30:00.000", "2017-05-17 18:00:00.000" });

        shiftData.add(new String[] { "2017-05-23 09:00:00.000", "2017-05-23 17:30:00.000" });
        shiftData.add(new String[] { "2017-05-24 08:00:00.000", "2017-05-24 16:30:00.000" });
        shiftData.add(new String[] { "2017-05-26 08:30:00.000", "2017-05-26 17:00:00.000" });

        shiftData.add(new String[] { "2017-05-30 09:30:00.000", "2017-05-30 18:00:00.000"});
    }

    public static void addDataToDB(ContentResolver contentResolver) {
        if (shiftData.size() == 0) createShiftData();
        ContentValues[] contentValues = new ContentValues[shiftData.size()];

        int i = 0;
        for (String[] shift : shiftData) {
            ContentValues values = new ContentValues();
            values.put(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME,shift[0]);
            values.put(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME, shift[1]);

            contentValues[i++] = values;
        }

        int numInserted = contentResolver.bulkInsert(ShiftyContract.Shift.CONTENT_URI, contentValues);
        Log.i("TestData.class", "inserted " + numInserted + " rows of test data");
    }


}
