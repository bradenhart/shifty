package io.bradenhart.shifty.data;

import android.net.Uri;
import android.provider.BaseColumns;

import io.bradenhart.shifty.util.DateUtils;

/**
 * Contract class to define database information.
 * Contains constants for the Content Provider.
 * Contains Shift and Workweek classes for the respective
 * tables in the database.
 *
 * @author bradenhart
 */
public class ShiftyContract {

    /* constants for the Content Provider */
    public static final String CONTENT_AUTHORITY = "io.bradenhart.shifty";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SHIFT = "shift";
    public static final String PATH_WORKWEEK = "workweek";

    private ShiftyContract() {
    }

    /**
     * Class defining Shift table column names, table name, content uri.
     */
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

    /**
     * Class defining Workweek table column names, table name, content uri.
     * Also contains helper methods related to the workweek table.
     */
    public static class Workweek implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WORKWEEK)
                .build();

        public static final String TABLE_NAME = "Workweek";
        public static final String COLUMN_WEEK_START_DATETIME = "week_start_datetime";
        public static final String COLUMN_WEEK_END_DATETIME = "week_end_datetime";
        public static final String COLUMN_TOTAL_PAID_HOURS = "total_paid_hours";

        /**
         * Creates an sql statement to select workweeks from the current week onwards.
         *
         * @return the sql select statement
         */
        public static String getSQLSelectForThisWeekOnwards() {
            return COLUMN_WEEK_START_DATETIME + " >= '" + DateUtils.getStartDateForCurrentWeek() + "'";
        }

        /**
         * Creates an sql statement to select workweeks before the current week.
         *
         * @return the sql select statement
         */
        public static String getSQLSelectForBeforeThisWeek() {
            return COLUMN_WEEK_START_DATETIME + " < '" + DateUtils.getStartDateForCurrentWeek() + "'";
        }

    }

}
