package io.bradenhart.shifty.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by bradenhart on 18/05/17.
 */

public class ShiftyContentProvider extends ContentProvider {

    public static final int CODE_SHIFT = 100;
    public static final int CODE_SHIFT_WITH_ID = 101;
    public static final int CODE_SHIFT_WITH_DATE = 102;

    public static final int CODE_WORKWEEK = 200;

    private ShiftyDbHelper dbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        String authority = ShiftyContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, ShiftyContract.PATH_SHIFT, CODE_SHIFT);
        uriMatcher.addURI(authority, ShiftyContract.PATH_SHIFT + "/#", CODE_SHIFT_WITH_ID);
        uriMatcher.addURI(authority, ShiftyContract.PATH_SHIFT + "/*", CODE_SHIFT_WITH_DATE);

        uriMatcher.addURI(authority, ShiftyContract.PATH_WORKWEEK, CODE_WORKWEEK);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new ShiftyDbHelper(getContext());
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
            // query the shift table and return results in the form of workweeks
//            case CODE_WORKWEEK:
//                cursor = db.query(
//                        ShiftyContract.Shift.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder
//                );
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

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;

        switch (match) {
            // insert into /shift (Shift table)
            case CODE_SHIFT:
                long id = db.insert(ShiftyContract.Shift.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(ShiftyContract.Shift.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numRowsDeleted;

        if (selection == null) selection = "1";

        switch (match) {
            // delete all shifts
            case CODE_SHIFT:
                numRowsDeleted = db.delete(ShiftyContract.Shift.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        if (numRowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int numRowsUpdated;

        switch (match) {
            case CODE_SHIFT_WITH_ID:
                String id = uri.getPathSegments().get(1);
                numRowsUpdated = db.update(
                        ShiftyContract.Shift.TABLE_NAME,
                        values,
                        "_id = ?",
                        new String[]{ id }
                );
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
