package io.bradenhart.shifty.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by bradenhart on 27/03/17.
 */

public class DatabaseManager {

    private SQLiteDatabase database;
    private ShiftyDbHelper dbHelper;

    public DatabaseManager(Context context) {
        this.dbHelper = ShiftyDbHelper.getInstance(context.getApplicationContext());
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

}
