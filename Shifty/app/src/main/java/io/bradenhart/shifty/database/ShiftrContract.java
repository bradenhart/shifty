package io.bradenhart.shifty.database;

import android.provider.BaseColumns;

/**
 * Created by bradenhart on 28/03/17.
 */

public class ShiftrContract {

    private ShiftrContract() {}

    public static class Shift implements BaseColumns {
        public static final String TABLE_NAME = "Shift";
        public static final String COLUMN_NAME_START_HOUR = "Start_Hour";
        public static final String COLUMN_NAME_START_MIN = "Start_Min";
        public static final String COLUMN_NAME_START_PERIOD = "Start_Period";
        public static final String COLUMN_NAME_END_HOUR = "End_Hour";
        public static final String COLUMN_NAME_END_MIN = "End_Min";
        public static final String COLUMN_NAME_END_PERIOD = "End_Period";
        public static final String COLUMN_NAME_WEEK_START = "Week_Start";
    }

}
