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
            + Shift._ID + " integer primary key autoincrement, "
            + Shift.COLUMN_WORKWEEK_ID + " text not null, "
            + Shift.COLUMN_SHIFT_START_DATETIME + " text not null, "
            + Shift.COLUMN_SHIFT_END_DATETIME + " text not null, "
            + Shift.COLUMN_TOTAL_SHIFT_HOURS + " real, "
            + Shift.COLUMN_PAID_HOURS + " real, "
            + "foreign key(" + Shift.COLUMN_WORKWEEK_ID + ")"
            + " references " + Workweek.TABLE_NAME + "(" + Workweek._ID + ")"
            + ");";

    private final String SQL_CREATE_WORKWEEK = "create table " + Workweek.TABLE_NAME + "("
            + Workweek._ID + " text primary key, "
            + Workweek.COLUMN_WEEK_START_DATETIME + " text not null, "
            + Workweek.COLUMN_WEEK_END_DATETIME + " text not null, "
            + Workweek.COLUMN_TOTAL_PAID_HOURS + " real default 0.0"
            + ");";


    /* TRIGGERS */
    private final String TR_BI_SHIFT = "TR_BI_SHIFT";
    private final String SQL_CREATE_TR_BI_SHIFT = "create trigger " + TR_BI_SHIFT
        + " before insert"
        + " on " + Shift.TABLE_NAME
        + " begin"
            + " insert on conflict ignore"
            + " into " + Workweek.TABLE_NAME + "("
                                             + Workweek._ID + ", "
                                             + Workweek.COLUMN_WEEK_START_DATETIME + ","
                                             + Workweek.COLUMN_WEEK_END_DATETIME
                                             + ")"
        + " values" + "("

                    // get the start of the week for the primary key
                    + "strftime('%Y-%m-%d %H:%M:%f', new." + Shift.COLUMN_SHIFT_START_DATETIME
                    + ",’weekday 1', '-7 days', 'start of day')"

                    // get the start of the week
                    + "strftime('%Y-%m-%d %H:%M:%f', new." + Shift.COLUMN_SHIFT_START_DATETIME
                    + ",’weekday 1', '-7 days', 'start of day')"

                    // get the end of the week
                    + "strftime('%Y-%m-%d %H:%M:%f', new." + Shift.COLUMN_SHIFT_START_DATETIME
                    + ",'weekday 1', 'start of day', '-0.001 seconds')"

                    + ");"
        + "end;";

    private final String TR_AI_SHIFT = "TR_AI_SHIFT";
    private final String SQL_CREATE_TR_AI_SHIFT = "create trigger " + TR_AI_SHIFT
        + " after insert"
        + " on " + Shift.TABLE_NAME
        + " begin"
            + " update + " + Shift.TABLE_NAME
            + " set " + Shift.COLUMN_TOTAL_SHIFT_HOURS
            + " = round((julianday(new." + Shift.COLUMN_SHIFT_END_DATETIME + ") - julianday(new."
            + Shift.COLUMN_SHIFT_START_DATETIME + "))*24, 2);"
        + " end;";

    private final String TR_AU_SHIFT_TSH = "TR_AU_SHIFT_TSH";
    private final String SQL_CREATE_TR_AU_SHIFT_TSH = "create trigger " + TR_AU_SHIFT_TSH
        + " after update"
        + " on " + Shift.TABLE_NAME
        + " when new." + Shift.COLUMN_TOTAL_SHIFT_HOURS + " != old." + Shift.COLUMN_TOTAL_SHIFT_HOURS
        + " begin"
	        // update Shift.paid_hours
            + " update " + Shift.TABLE_NAME
            + " set " + Shift.COLUMN_PAID_HOURS
            + " = new." + Shift.COLUMN_TOTAL_SHIFT_HOURS
            + " where new." + Shift.COLUMN_TOTAL_SHIFT_HOURS + "<= 5.0 and new." + Shift.COLUMN_TOTAL_SHIFT_HOURS + " is not NULL;"

            + " update " + Shift.TABLE_NAME
            + " set " + Shift.COLUMN_PAID_HOURS + " = new." + Shift.COLUMN_TOTAL_SHIFT_HOURS + " - 0.5"
            + " where new." + Shift.COLUMN_TOTAL_SHIFT_HOURS + " > 5.0 and new." + Shift.COLUMN_TOTAL_SHIFT_HOURS + " is not NULL;"
        + " end;";

    private final String TR_AU_SHIFT_PH_VALID = "TR_AU_SHIFT_PH_VALID";
    private final String SQL_CREATE_TR_AU_SHIFT_PH_VALID = "create trigger " + TR_AU_SHIFT_PH_VALID
        + " after update"
        + " on " + Shift.TABLE_NAME
        + " when new." + Shift.COLUMN_PAID_HOURS + " is not NULL"
        + " begin"
            + " update " + Workweek.TABLE_NAME
            + " set " + Workweek.COLUMN_TOTAL_PAID_HOURS
            + " = (select " + Workweek.COLUMN_TOTAL_PAID_HOURS + " from " + Workweek.TABLE_NAME
                + " where " + Workweek._ID + " = new." + Shift.COLUMN_WORKWEEK_ID + ")"
            + " - old." + Shift.COLUMN_PAID_HOURS + " new." + Shift.COLUMN_PAID_HOURS
            + " where old." + Shift.COLUMN_PAID_HOURS + " is not null and " + Workweek._ID
            + " = new." + Shift.COLUMN_WORKWEEK_ID + ";"

            + " update " + Workweek.TABLE_NAME
            + " set " + Workweek.COLUMN_TOTAL_PAID_HOURS
            + " = (select " + Workweek.COLUMN_TOTAL_PAID_HOURS + " from " + Workweek.TABLE_NAME
                + " where " + Workweek._ID + " = new." + Shift.COLUMN_WORKWEEK_ID + ")"
            + " + new." + Shift.COLUMN_PAID_HOURS + " where " + Workweek._ID + "= new."
            + Shift.COLUMN_WORKWEEK_ID + ";"
        + " end;";

    private final String TR_AD_SHIFT_PH_VALID = "TR_AD_SHIFT_PH_VALID";
    private final String SQL_CREATE_TR_AD_SHIFT_PH_VALID = "create trigger " + TR_AD_SHIFT_PH_VALID
        + " after delete"
        + " on " + Shift.TABLE_NAME
        + " where old." + Shift.COLUMN_PAID_HOURS + " is not NULL"
        + " begin"
            + " update " + Workweek.TABLE_NAME
            + " set " + Workweek.COLUMN_TOTAL_PAID_HOURS
            + " = (select " + Workweek.COLUMN_TOTAL_PAID_HOURS + " from " + Workweek.TABLE_NAME
                + " where " + Workweek._ID + " = new." + Shift.COLUMN_WORKWEEK_ID + ")"
            + " - old." + Shift.COLUMN_PAID_HOURS + " where " + Workweek._ID + " = old."
            + Shift.COLUMN_WORKWEEK_ID + ";"
        + " end;";

    private final String TR_AD_WORKWEEK = "TR_AD_WORKWEEK";
    private final String SQL_CREATE_TR_AD_WORKWEEK = "create trigger " + TR_AD_WORKWEEK
        + " after delete"
        + " on " + Workweek.TABLE_NAME
        + " begin"
            + " delete from + " + Shift.TABLE_NAME + " where " + Shift.COLUMN_WORKWEEK_ID
            + " = old." + Workweek._ID + ";"
        + " end;";

    private final String TR_AU_WORKWEEK_TPS_ZERO = "TR_AU_WORKWEEK_TPS_ZERO";
    private final String SQL_CREATE_TR_AU_WORKWEEK_TPS_ZERO = "create trigger " + TR_AU_WORKWEEK_TPS_ZERO
        + " after update"
        + " on Workweek.TABLE_NAME"
        + " when new." + Workweek.COLUMN_TOTAL_PAID_HOURS + " <= 0"
        + " begin"
            + " delete from " + Workweek.TABLE_NAME + " where " + Workweek._ID + " = new."
            + Workweek._ID + ";"
        + " end;";

    private final String SQL_DELETE_SHIFT = "drop table if exists " + Shift.TABLE_NAME;
    private final String SQL_DELETE_WORKWEEK = "drop table if exists " + Shift.TABLE_NAME;

    private ShiftyDbHelper(Context context) {
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
        db.execSQL(SQL_CREATE_WORKWEEK);
        db.execSQL(SQL_CREATE_SHIFT);

        db.execSQL(SQL_CREATE_TR_BI_SHIFT);
        db.execSQL(SQL_CREATE_TR_AI_SHIFT);
        db.execSQL(SQL_CREATE_TR_AU_SHIFT_TSH);
        db.execSQL(SQL_CREATE_TR_AU_SHIFT_PH_VALID);
        db.execSQL(SQL_CREATE_TR_AD_SHIFT_PH_VALID);
        db.execSQL(SQL_CREATE_TR_AD_WORKWEEK);
        db.execSQL(SQL_CREATE_TR_AU_WORKWEEK_TPS_ZERO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_WORKWEEK);
        db.execSQL(SQL_DELETE_SHIFT);
        onCreate(db);
    }
}
