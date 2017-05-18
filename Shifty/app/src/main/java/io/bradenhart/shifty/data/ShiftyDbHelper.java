package io.bradenhart.shifty.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.bradenhart.shifty.data.ShiftyContract.*;

/**
 * Created by bradenhart on 27/03/17.
 */

class ShiftyDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "shifty_db.db";
    private static final int DB_VERSION = 1;

    private static ShiftyDbHelper dbHelper;
    private Context context;

    private final String SQL_CREATE_SHIFT = "create table " + Shift.TABLE_NAME + "("
            + Shift._ID                             + " integer primary key autoincrement, "
            + Shift.COLUMN_WEEK_START_DATETIME      + " text not null, "
            + Shift.COLUMN_WEEK_END_DATETIME        + " text not null, "
            + Shift.COLUMN_SHIFT_START_DATETIME     + " text not null, "
            + Shift.COLUMN_SHIFT_END_DATETIME       + " text not null"
                                                    + ")";

    private final String SQL_DELETE_SHIFT = "drop table if exists " + Shift.TABLE_NAME;

//    private final String SQL_DELETE_WEEK = "drop table if exists " + Week.TABLE_NAME;

    public ShiftyDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    static synchronized ShiftyDbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new ShiftyDbHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SHIFT);
//        db.execSQL(SQL_CREATE_WEEK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(SQL_DELETE_WEEK);
        db.execSQL(SQL_DELETE_SHIFT);
        onCreate(db);
    }
}
