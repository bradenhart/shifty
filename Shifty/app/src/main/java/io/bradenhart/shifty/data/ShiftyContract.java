package io.bradenhart.shifty.data;

import android.provider.BaseColumns;

/**
 * Created by bradenhart on 28/03/17.
 */

public class ShiftyContract {

    private ShiftyContract() {}

    public static class Shift implements BaseColumns {
        public static final String TABLE_NAME = "Shift";
        public static final String COLUMN_WEEK_START_DATETIME = "week_start_datetime";
        public static final String COLUMN_WEEK_END_DATETIME = "week_end_datetime";
        public static final String COLUMN_SHIFT_START_DATETIME = "shift_start_datetime";
        public static final String COLUMN_SHIFT_END_DATETIME = "shift_end_datetime";
    }

}
