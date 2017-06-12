package io.bradenhart.shifty.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.bradenhart.shifty.data.ShiftyContract.*;

/**
 * Helper class for the Shifty SQLite Database.
 *
 * @author bradenhart
 */
class ShiftyDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "shifty_db.db";
    private static final int DB_VERSION = 1;

    private static ShiftyDbHelper dbHelper;

    private final String SQL_CREATE_SHIFT = "create table " + Shift.TABLE_NAME + "("
            + Shift._ID + " integer primary key autoincrement, "
            + Shift.COLUMN_WORKWEEK_ID + " text not null, "
            + Shift.COLUMN_SHIFT_START_DATETIME + " text not null, "
            + Shift.COLUMN_SHIFT_END_DATETIME + " text not null, "
            + Shift.COLUMN_TOTAL_SHIFT_HOURS + " real, "
            + Shift.COLUMN_PAID_HOURS + " real, "
            + "constraint shift_unique unique("
                + Shift.COLUMN_SHIFT_START_DATETIME + ", "
                + Shift.COLUMN_SHIFT_END_DATETIME + "), "
            + "foreign key(" + Shift.COLUMN_WORKWEEK_ID + ")"
            + " references " + Workweek.TABLE_NAME + "(" + Workweek._ID + ")"
            + ");";

    private final String SQL_CREATE_WORKWEEK = "create table " + Workweek.TABLE_NAME + "("
            + Workweek._ID + " text primary key, "
            + Workweek.COLUMN_WEEK_START_DATETIME + " text not null, "
            + Workweek.COLUMN_WEEK_END_DATETIME + " text not null, "
            + Workweek.COLUMN_TOTAL_PAID_HOURS + " real default 0.0, "
            + "constraint week_unique unique("
            + Workweek.COLUMN_WEEK_START_DATETIME + ", "
            + Workweek.COLUMN_WEEK_END_DATETIME + ") "
            + ");";

    private final String SQL_DELETE_SHIFT = "drop table if exists " + Shift.TABLE_NAME;
    private final String SQL_DELETE_WORKWEEK = "drop table if exists " + Shift.TABLE_NAME;

    private ShiftyDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    static synchronized ShiftyDbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new ShiftyDbHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_WORKWEEK);
        db.execSQL(SQL_CREATE_SHIFT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_WORKWEEK);
            db.execSQL(SQL_DELETE_SHIFT);
            onCreate(db);
    }
}
