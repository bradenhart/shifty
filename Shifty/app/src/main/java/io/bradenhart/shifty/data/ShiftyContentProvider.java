package io.bradenhart.shifty.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import io.bradenhart.shifty.util.DateUtils;

/**
 * ContentProvider for accessing data stored in the SQLite Database.
 *
 * @author bradenhart
 */
public class ShiftyContentProvider extends ContentProvider {

    /* codes for the uri matcher to match uris */
    // /shift
    public static final int CODE_SHIFT = 100;
    // /shift/<id>
    public static final int CODE_SHIFT_WITH_ID = 101;
    // /shift/<date>
    public static final int CODE_SHIFT_WITH_DATE = 102;
    // /workweek
    public static final int CODE_WORKWEEK = 200;
    // /workweek/<id>
    public static final int CODE_WORKWEEK_WITH_ID = 201;

    // helper class for the database
    private ShiftyDbHelper dbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Builds the UriMatcher for this ContentProvider.
     * @return the built uri matcher
     */
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        String authority = ShiftyContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, ShiftyContract.PATH_SHIFT, CODE_SHIFT);
        uriMatcher.addURI(authority, ShiftyContract.PATH_SHIFT + "/#", CODE_SHIFT_WITH_ID);
        uriMatcher.addURI(authority, ShiftyContract.PATH_SHIFT + "/*", CODE_SHIFT_WITH_DATE);

        uriMatcher.addURI(authority, ShiftyContract.PATH_WORKWEEK, CODE_WORKWEEK);
        uriMatcher.addURI(authority, ShiftyContract.PATH_WORKWEEK + "/*", CODE_WORKWEEK_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = ShiftyDbHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;

        switch (match) {
            // query the shift table
            case CODE_SHIFT:
                cursor = db.query(
                        ShiftyContract.Shift.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_SHIFT_WITH_ID:
                String id = uri.getPathSegments().get(1);
                cursor = db.query(
                        ShiftyContract.Shift.TABLE_NAME,
                        projection,
                        ShiftyContract.Shift._ID + " = ?",
                        new String[] {id},
                        null,
                        null,
                        sortOrder,
                        "1"
                );
                break;
            // query the workweek table
            case CODE_WORKWEEK:
                cursor = db.query(
                        ShiftyContract.Workweek.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (match) {
            case CODE_SHIFT:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        String shiftStartDatetime = value.getAsString(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME);
                        String shiftEndDatetime = value.getAsString(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME);
                        String weekStartDatetime;
                        String weekEndDatetime;
                        Double totalShiftHours; // Shift column
                        Double paidHours; // Shift column

                        // if the content value contains null values for the shift start or end times, go to next content value
                        if (shiftStartDatetime == null || shiftEndDatetime == null) continue;

                        // calculate shift hours
                        totalShiftHours = DateUtils.getHoursBetween(shiftStartDatetime, shiftEndDatetime, DateUtils.FMT_ISO_8601_DATETIME);
                        paidHours = totalShiftHours <= 5.0 ? totalShiftHours : totalShiftHours - 0.5;

                        // get the week start datetime for the shift
                        weekStartDatetime = DateUtils.getWeekStart(shiftStartDatetime, DateUtils.FMT_ISO_8601_DATETIME);

                        // get the week end datetime for the shift
                        weekEndDatetime = DateUtils.getWeekEnd(shiftStartDatetime, DateUtils.FMT_ISO_8601_DATETIME);

                        // insert workweek row
                        ContentValues workweekValues = new ContentValues();
                        workweekValues.put(ShiftyContract.Workweek._ID, weekStartDatetime);
                        workweekValues.put(ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME, weekStartDatetime);
                        workweekValues.put(ShiftyContract.Workweek.COLUMN_WEEK_END_DATETIME, weekEndDatetime);
                        // the workweek row needs to exist before the shift is inserted because the
                        // Shift has a foreign key column referencing the _id column in the workweek
                        // table.
                        // if the workweek row already exists, an SQLiteConstraintException will be thrown
                        // the transaction should continue so the exception is caught and logged.
                        try {
                            db.insertOrThrow(ShiftyContract.Workweek.TABLE_NAME,
                                    ShiftyContract.Workweek._ID + "," + ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + "," + ShiftyContract.Workweek.COLUMN_WEEK_END_DATETIME,
                                    workweekValues);
                        } catch (SQLiteConstraintException ex) {
                            Log.i("ShiftContentProvider", "workweek already exists, no insert performed");
                        }

                        // insert shift row
                        value.put(ShiftyContract.Shift.COLUMN_WORKWEEK_ID, weekStartDatetime);
                        value.put(ShiftyContract.Shift.COLUMN_TOTAL_SHIFT_HOURS, totalShiftHours);
                        value.put(ShiftyContract.Shift.COLUMN_PAID_HOURS, paidHours);

                        try {
                            long id = db.insertOrThrow(ShiftyContract.Shift.TABLE_NAME, null, value);

                            // update the workweek row that the shift references
                            String updateSQL = "update " + ShiftyContract.Workweek.TABLE_NAME
                                    + " set " + ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS + " = "
                                    + ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS + " + ?"
                                    + " where " + ShiftyContract.Workweek._ID + " = ?";
                            String[] updateArgs = new String[]{paidHours + "", weekStartDatetime};
                            db.execSQL(updateSQL, updateArgs);

                            if (id != -1) rowsInserted++;
                        } catch (SQLiteConstraintException ex) {
                            Log.i("ShiftyContentProvider", "shift not inserted, duplicate row already exists");
                        }

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;

        long id = 0;

        switch (match) {
            // insert into /shift (Shift table)
            case CODE_SHIFT:
                // finish if the content values is null
                if (values != null) {

                    db.beginTransaction();
                    try {
                        String shiftStartDatetime = values.getAsString(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME);
                        String shiftEndDatetime = values.getAsString(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME);
                        String weekStartDatetime;
                        String weekEndDatetime;
                        Double totalShiftHours; // Shift column
                        Double paidHours; // Shift column

                        // if the content values contains a null values for the shift start or end times, break
                        if (shiftStartDatetime == null || shiftEndDatetime == null)
                            throw new SQLException("Failed to insert row into " + uri + ", null start or end date provided");

                        // calculate shift hours
                        totalShiftHours = DateUtils.getHoursBetween(shiftStartDatetime, shiftEndDatetime, DateUtils.FMT_ISO_8601_DATETIME);
                        paidHours = totalShiftHours <= 5.0 ? totalShiftHours : totalShiftHours - 0.5;

                        // get the week start datetime for the shift
                        weekStartDatetime = DateUtils.getWeekStart(shiftStartDatetime, DateUtils.FMT_ISO_8601_DATETIME);

                        // get the week end datetime for the shift
                        weekEndDatetime = DateUtils.getWeekEnd(shiftStartDatetime, DateUtils.FMT_ISO_8601_DATETIME);

                        // insert workweek row
                        ContentValues workweekValues = new ContentValues();
                        workweekValues.put(ShiftyContract.Workweek._ID, weekStartDatetime);
                        workweekValues.put(ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME, weekStartDatetime);
                        workweekValues.put(ShiftyContract.Workweek.COLUMN_WEEK_END_DATETIME, weekEndDatetime);
                        // the workweek row needs to exist before the shift is inserted because the
                        // Shift has a foreign key column referencing the _id column in the workweek
                        // table.
                        // if the workweek row already exists, an SQLiteConstraintException will be thrown
                        // the transaction should continue so the exception is caught and logged.
                        try {
                            db.insertOrThrow(ShiftyContract.Workweek.TABLE_NAME,
                                    ShiftyContract.Workweek._ID + "," + ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + "," + ShiftyContract.Workweek.COLUMN_WEEK_END_DATETIME,
                                    workweekValues);
                        } catch (SQLiteConstraintException ex) {
                            Log.i("ShiftContentProvider", "workweek already exists, no insert performed");
                        }

                        // insert shift row
                        values.put(ShiftyContract.Shift.COLUMN_WORKWEEK_ID, weekStartDatetime);
                        values.put(ShiftyContract.Shift.COLUMN_TOTAL_SHIFT_HOURS, totalShiftHours);
                        values.put(ShiftyContract.Shift.COLUMN_PAID_HOURS, paidHours);
                        id = db.insert(ShiftyContract.Shift.TABLE_NAME, null, values);

                        // update the workweek row that the shift references
                        String updateSQL = "update " + ShiftyContract.Workweek.TABLE_NAME
                                + " set " + ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS + " = "
                                + ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS + " + ?"
                                + " where " + ShiftyContract.Workweek._ID + " = ?";
                        String[] updateArgs = new String[]{paidHours + "", weekStartDatetime};
                        db.execSQL(updateSQL, updateArgs);

                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                }

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(ShiftyContract.Shift.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
//            case CODE_WORKWEEK:
//                try {
//                    id = db.insertOrThrow(ShiftyContract.Workweek.TABLE_NAME, null, values);
//                } catch (SQLiteConstraintException ex) {
//                    Log.i("ShiftContentProvider", "workweek already exists");
//                    break;
//                }
//                if (id > 0) {
//                    returnUri = ContentUris.withAppendedId(ShiftyContract.Workweek.CONTENT_URI, id);
//                } else {
//                    throw new SQLException("Failed to insert row into " + uri);
//                }
//                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (returnUri != null) getContext().getContentResolver().notifyChange(
                ShiftyContract.Workweek.CONTENT_URI, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numRowsDeleted;

        String workweekID = null;

        if (selection == null) selection = "1";

        switch (match) {
            // delete all shifts
            case CODE_SHIFT:
                numRowsDeleted = db.delete(ShiftyContract.Shift.TABLE_NAME, null, null);
                break;
            case CODE_SHIFT_WITH_ID:
                // get the shift id from the uri
                String shiftID = uri.getPathSegments().get(1);

                db.beginTransaction();
                try {
                    // retrieve the paid hours for the shift being deleted
                    Cursor shiftCursor = db.query(ShiftyContract.Shift.TABLE_NAME,
                            new String[]{ShiftyContract.Shift.COLUMN_PAID_HOURS, ShiftyContract.Shift.COLUMN_WORKWEEK_ID},
                            ShiftyContract.Shift._ID + " = ?",
                            new String[]{shiftID},
                            null,
                            null,
                            null);

                    Double paidHours = 0.0;
                    workweekID = null;
                    if (shiftCursor.moveToFirst()) {
                        int paidHoursCol = shiftCursor.getColumnIndex(ShiftyContract.Shift.COLUMN_PAID_HOURS);
                        int workweekIDCol = shiftCursor.getColumnIndex(ShiftyContract.Shift.COLUMN_WORKWEEK_ID);
                        paidHours = shiftCursor.getDouble(paidHoursCol);
                        workweekID = shiftCursor.getString(workweekIDCol);
                    }
                    shiftCursor.close();

                    // delete the shift
                    int tempNumRowsDeleted = db.delete(ShiftyContract.Shift.TABLE_NAME,
                            ShiftyContract.Shift._ID + " = ?",
                            new String[]{shiftID});

                    // update the workweek row that the shift references
                    String updateSQL = "update " + ShiftyContract.Workweek.TABLE_NAME
                            + " set " + ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS + " = "
                            + ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS + " - ?"
                            + " where " + ShiftyContract.Workweek._ID + " = ?";
                    String[] updateArgs = new String[]{paidHours + "", workweekID};
                    db.execSQL(updateSQL, updateArgs);

                    numRowsDeleted = tempNumRowsDeleted;
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case CODE_WORKWEEK_WITH_ID:
                // get the workweek id from the uri
                workweekID = uri.getPathSegments().get(1);

                db.beginTransaction();
                try {
                    // delete the shifts that reference the workweek
                    db.delete(ShiftyContract.Shift.TABLE_NAME,
                            ShiftyContract.Shift.COLUMN_WORKWEEK_ID + " = ?",
                            new String[]{workweekID});

                    // delete the workweek
                    numRowsDeleted = db.delete(ShiftyContract.Workweek.TABLE_NAME,
                            ShiftyContract.Workweek._ID + " = ?",
                            new String[]{workweekID});

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            if (match == CODE_SHIFT_WITH_ID && workweekID != null) {
                getContext().getContentResolver().notifyChange(
                        ShiftyContract.Workweek.CONTENT_URI,
                        null
                );
                Log.i("ShiftyContentProvider", "updated workweek uri for " + workweekID);
            }
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int numRowsUpdated;

        switch (match) {
            case CODE_SHIFT_WITH_ID:
                if (values == null) {
                    throw new SQLException("Failed to update row at " + uri + ", null content values provided");
                }

                // continue
                db.beginTransaction();
                //
                try {
                    // get the shift id from the uri
                    String id = uri.getPathSegments().get(1);

                    String shiftStartDatetime = values.getAsString(ShiftyContract.Shift.COLUMN_SHIFT_START_DATETIME);
                    String shiftEndDatetime = values.getAsString(ShiftyContract.Shift.COLUMN_SHIFT_END_DATETIME);
                    String weekStartDatetime;
                    String weekEndDatetime;
                    Double totalShiftHours; // Shift column
                    Double paidHours; // Shift column
                    Double totalPaidHours = 0.0; // Workweek column
                    boolean workweekHasChanged = false;

                    // if the content values contains a null values for the shift start or end times, break
                    if (shiftStartDatetime == null || shiftEndDatetime == null) {
                        Log.e("ShiftyContentProvider", shiftStartDatetime + ", " + shiftEndDatetime);
                        throw new SQLException("Failed to update row at " + uri + ", null start or end time provided");
                    }

                    totalShiftHours = DateUtils.getHoursBetween(shiftStartDatetime, shiftEndDatetime, DateUtils.FMT_ISO_8601_DATETIME);
                    paidHours = totalShiftHours <= 5.0 ? totalShiftHours : totalShiftHours - 0.5;

                    // get the week start datetime for the shift
                    weekStartDatetime = DateUtils.getWeekStart(shiftStartDatetime, DateUtils.FMT_ISO_8601_DATETIME);

                    // get the week end datetime for the shift
                    weekEndDatetime = DateUtils.getWeekEnd(shiftStartDatetime, DateUtils.FMT_ISO_8601_DATETIME);

                    // the workweek id may have changed. try and create the workweek row to ensure that
                    // it exists before the shift is updated and references that row
                    if (values.containsKey(ShiftyContract.Shift.COLUMN_WORKWEEK_ID)) {
                        // insert workweek row
                        ContentValues workweekValues = new ContentValues();
                        workweekValues.put(ShiftyContract.Workweek._ID, weekStartDatetime);
                        workweekValues.put(ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME, weekStartDatetime);
                        workweekValues.put(ShiftyContract.Workweek.COLUMN_WEEK_END_DATETIME, weekEndDatetime);
                        // the workweek row needs to exist before the shift is inserted because the
                        // Shift has a foreign key column referencing the _id column in the workweek
                        // table.
                        // if the workweek row already exists, an SQLiteConstraintException will be thrown
                        // the transaction should continue so the exception is caught and logged.
                        try {
                            db.insertOrThrow(ShiftyContract.Workweek.TABLE_NAME,
                                    ShiftyContract.Workweek._ID + "," + ShiftyContract.Workweek.COLUMN_WEEK_START_DATETIME + "," + ShiftyContract.Workweek.COLUMN_WEEK_END_DATETIME,
                                    workweekValues);
                            Log.i("ShiftyContentProvider", "inserted workweek during shift update");
                            workweekHasChanged = true;
                        } catch (SQLiteConstraintException ex) {
                            Log.i("ShiftyContentProvider", "workweek already exists");
                        }
                    }

                    // get the old value of shift's paid hours
                    Double oldPaidHours = 0.0; // holds the old paid hours value, set to 0.0 in case there is no value;
                    String oldWorkweekID = null;
                    Cursor oldShiftCursor = db.query(ShiftyContract.Shift.TABLE_NAME,
                            new String[]{ShiftyContract.Shift.COLUMN_PAID_HOURS, ShiftyContract.Shift.COLUMN_WORKWEEK_ID},
                            "_id = ?",
                            new String[]{id},
                            null, null, null, "1");

                    if (oldShiftCursor.moveToFirst()) {
                        int paidHoursCol = oldShiftCursor.getColumnIndex(ShiftyContract.Shift.COLUMN_PAID_HOURS);
                        int workweekIDCol = oldShiftCursor.getColumnIndex(ShiftyContract.Shift.COLUMN_WORKWEEK_ID);
                        oldPaidHours = oldShiftCursor.getDouble(paidHoursCol);
                        oldWorkweekID = oldShiftCursor.getString(workweekIDCol);
                        oldShiftCursor.close();
                    }

                    // update the previously associated workweek to decrease its total paid hours column
                    if (workweekHasChanged) {
                        // update the previously associated workweek
                        db.execSQL("update " + ShiftyContract.Workweek.TABLE_NAME
                                        + " set " + ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS
                                        + " = " + ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS + " - ?"
                                        + " where " + ShiftyContract.Workweek._ID + " = ?",
                                new String[]{oldPaidHours + "", oldWorkweekID});
                    }

                    // update the shift
                    values.put(ShiftyContract.Shift.COLUMN_TOTAL_SHIFT_HOURS, totalShiftHours);
                    values.put(ShiftyContract.Shift.COLUMN_PAID_HOURS, paidHours);
                    int tempNumRowsUpdated = db.update(
                            ShiftyContract.Shift.TABLE_NAME,
                            values,
                            "_id = ?",
                            new String[]{id}
                    );

                    // update the associated workweek
                    // get the old value of workweek's total paid hours
                    Double oldTotalPaidHours = 0.0; // holds the old paid hours value, set to 0.0 incase there is no value;
                    Cursor oldWorkweekCursor = db.query(ShiftyContract.Workweek.TABLE_NAME,
                            new String[]{ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS},
                            "_id = ?",
                            new String[]{weekStartDatetime},
                            null, null, null, "1"
                    );

                    if (oldWorkweekCursor.moveToFirst()) {
                        int totalPaidHoursCol = oldWorkweekCursor.getColumnIndex(ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS);
                        oldTotalPaidHours = oldWorkweekCursor.getDouble(totalPaidHoursCol);
                        oldWorkweekCursor.close();
                    }

                    ContentValues updateWorkweekValues = new ContentValues();
                    updateWorkweekValues.put(ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS, oldTotalPaidHours == 0.0 ? paidHours : oldTotalPaidHours - oldPaidHours + paidHours);

                    // if oldWorkweekID is set, there is an that have no shifts associated with them
                    if (oldWorkweekID != null) {
                        db.delete(ShiftyContract.Workweek.TABLE_NAME,
                                ShiftyContract.Workweek._ID + " = ? and " + ShiftyContract.Workweek.COLUMN_TOTAL_PAID_HOURS + " <= 0.0",
                                new String[]{oldWorkweekID});
                    }

                    if (tempNumRowsUpdated > 0) {
                        db.update(ShiftyContract.Workweek.TABLE_NAME,
                                updateWorkweekValues,
                                "_id = ?",
                                new String[]{weekStartDatetime}
                        );
                    }

                    numRowsUpdated = tempNumRowsUpdated;
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsUpdated;
    }
}
