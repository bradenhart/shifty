package io.bradenhart.shifty.data;

import android.net.Uri;
import android.provider.BaseColumns;

import io.bradenhart.shifty.util.DateUtil;

/**
 * Created by bradenhart on 28/03/17.
 */

public class ShiftyContract {

    public static final String CONTENT_AUTHORITY = "io.bradenhart.shifty";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SHIFT = "shift";
    public static final String PATH_WORKWEEK = "workweek";

    private ShiftyContract() {
    }

    public static class Shift implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_SHIFT)
                .build();

        public static final String TABLE_NAME = "Shift";
        public static final String COLUMN_SHIFT_START_DATETIME = "shift_start_datetime";
        public static final String COLUMN_SHIFT_END_DATETIME = "shift_end_datetime";
        public static final String COLUMN_TOTAL_SHIFT_HOURS = "total_shift_hours";
        public static final String COLUMN_PAID_HOURS = "paid_hours";
        public static final String COLUMN_WORKWEEK_ID = "workweek_id";
    }

    public static class Workweek implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WORKWEEK)
                .build();

        public static final String TABLE_NAME = "Workweek";
        public static final String COLUMN_WEEK_START_DATETIME = "week_start_datetime";
        public static final String COLUMN_WEEK_END_DATETIME = "week_end_datetime";
        public static final String COLUMN_TOTAL_PAID_HOURS = "total_paid_hours";

        public static String getSQLSelectForThisWeekOnwards() {
            return COLUMN_WEEK_START_DATETIME + " >= '" + DateUtil.getStartDateForCurrentWeek() + "'";
        }

    }

}
