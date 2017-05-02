package io.bradenhart.shifty.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.ShiftTime;
import io.bradenhart.shifty.util.DateUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bradenhart on 27/03/17.
 */

public class DatabaseManager {

    private SQLiteDatabase database;
    private ShiftrDbHelper dbHelper;
//    private Context context;

    public DatabaseManager(Context context) {
//        this.context = context;
        this.dbHelper = ShiftrDbHelper.getInstance(context.getApplicationContext());
    }

    private void openForWrite() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    private void openForRead() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    private void close() {
        database.close();
    }


    public boolean insertShift(Shift shift) {
//        Log.e("DatabaseManager", "<<--" + shift.toString() + " -->>");
        ContentValues values = new ContentValues();
        values.put(ShiftrContract.Shift._ID, shift.getId());
        values.put(ShiftrContract.Shift.COLUMN_NAME_START_HOUR, shift.getStartTime().getHour().toString());
        values.put(ShiftrContract.Shift.COLUMN_NAME_START_MIN, shift.getStartTime().getMinute().toString());
        values.put(ShiftrContract.Shift.COLUMN_NAME_START_PERIOD, shift.getStartTime().getPeriod().toString());
        values.put(ShiftrContract.Shift.COLUMN_NAME_END_HOUR, shift.getEndTime().getHour().toString());
        values.put(ShiftrContract.Shift.COLUMN_NAME_END_MIN, shift.getEndTime().getMinute().toString());
        values.put(ShiftrContract.Shift.COLUMN_NAME_END_PERIOD, shift.getEndTime().getPeriod().toString());
        values.put(ShiftrContract.Shift.COLUMN_NAME_WEEK_START, DateUtil.getWeekStart(shift.getId()));
        openForWrite();
        long l = database.insert(ShiftrContract.Shift.TABLE_NAME, null, values);
        close();
        return l != -1;
    }

    public Shift retrieveShift(String _id) {
        openForRead();

        Cursor cursor = database.rawQuery("select * from " + ShiftrContract.Shift.TABLE_NAME
                        + " where " + ShiftrContract.Shift._ID + " = ?",
                new String[]{_id}
        );

        Shift shift = null;

        if (cursor.moveToFirst()) {
            shift = getShiftFromCursor(cursor);
        }

        cursor.close();
        close();

        return shift;
    }

    public int getShiftCount() {
        openForRead();

        Cursor cursor = database.rawQuery("select _id from " + ShiftrContract.Shift.TABLE_NAME, null);

        cursor.moveToFirst();

        int count = cursor.getCount();
        cursor.close();
        close();
        return count;
    }

    public List<Shift> retrieveAllShifts() {
        List<Shift> shifts = new ArrayList<>();

        openForRead();

        Cursor cursor = database.rawQuery("select * from " + ShiftrContract.Shift.TABLE_NAME + " order by " + ShiftrContract.Shift._ID + " asc", null);

        if (cursor.moveToFirst()) {
            do {
                shifts.add(getShiftFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        close();
        return shifts;
    }

    public Map<String, List<Shift>> getShiftsInDateRange(final String[] datetimes) {
        Map<String, List<Shift>> shiftMap = new LinkedHashMap<>();

        openForRead();

        Cursor cursor;
        if (datetimes.length > 1) {
            Log.e("LOG", datetimes[0] + " -> " + datetimes[datetimes.length - 1]);
            cursor = database.rawQuery("select * from " + ShiftrContract.Shift.TABLE_NAME + " where " + ShiftrContract.Shift._ID + " between datetime(?) and (?) order by " + ShiftrContract.Shift._ID + " asc",
                    new String[]{datetimes[0], datetimes[datetimes.length - 1]});
        } else {
            Log.e("LOG", datetimes[0]);
            cursor = database.rawQuery("select * from " + ShiftrContract.Shift.TABLE_NAME + " where " + ShiftrContract.Shift.COLUMN_NAME_WEEK_START + " =? order by " + ShiftrContract.Shift._ID + " asc",
                    new String[]{datetimes[0]});
        }

        if (cursor.moveToFirst()) {
            do {
                String cws = cursor.getString(cursor.getColumnIndex(ShiftrContract.Shift.COLUMN_NAME_WEEK_START));
                List<Shift> l = shiftMap.get(cws) == null ? new ArrayList<Shift>() : shiftMap.get(cws);
                Shift s = getShiftFromCursor(cursor);
                l.add(s);
                shiftMap.put(cws, l);

            }
            while (cursor.moveToNext());
        }

        cursor.close();
        close();

        return shiftMap;
    }

    private Shift getShiftFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(ShiftrContract.Shift._ID));

        String startHour = cursor.getString(cursor.getColumnIndex(ShiftrContract.Shift.COLUMN_NAME_START_HOUR));
        String startMin = cursor.getString(cursor.getColumnIndex(ShiftrContract.Shift.COLUMN_NAME_START_MIN));
        String startPeriod = cursor.getString(cursor.getColumnIndex(ShiftrContract.Shift.COLUMN_NAME_START_PERIOD));
        ShiftTime startTime = new ShiftTime(ShiftTime.Hour.get(startHour), ShiftTime.Minute.get(startMin), ShiftTime.Period.get(startPeriod));

        String endHour = cursor.getString(cursor.getColumnIndex(ShiftrContract.Shift.COLUMN_NAME_END_HOUR));
        String endMin = cursor.getString(cursor.getColumnIndex(ShiftrContract.Shift.COLUMN_NAME_END_MIN));
        String endPeriod = cursor.getString(cursor.getColumnIndex(ShiftrContract.Shift.COLUMN_NAME_END_PERIOD));
        ShiftTime endTime = new ShiftTime(ShiftTime.Hour.get(endHour), ShiftTime.Minute.get(endMin), ShiftTime.Period.get(endPeriod));

        Shift shift = new Shift(id, DateUtil.getDateFromDateTime(id), startTime, endTime);
//        Log.e("get shift", shift.getId());
        return shift;
    }

    public void deleteShift(String id) {
        openForWrite();
//        Log.e("DELETE", id);
        int res = database.delete(ShiftrContract.Shift.TABLE_NAME, ShiftrContract.Shift._ID + " = ?", new String[] { id });
        Log.e("DELETE", res == 0 ? "failed" : res + " id: " + id);
        close();
//        return res;
    }

    public void deleteAllShifts() {
        openForWrite();
        database.execSQL("delete from " + ShiftrContract.Shift.TABLE_NAME);
        close();
    }

}
