package io.bradenhart.shifty.domain;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by bradenhart on 25/03/17.
 */

public class ShiftDate implements Serializable {

    /**
     * FIELDS
     */
    private int day;
    private Weekday weekday;
    private Month month;
    private int year;

    /**
     * ENUMS
     */
    public enum Weekday {
        MONDAY(Calendar.MONDAY, "Monday"), TUESDAY(Calendar.TUESDAY, "Tuesday"),
        WEDNESDAY(Calendar.WEDNESDAY, "Wednesday"), THURSDAY(Calendar.THURSDAY, "Thursday"),
        FRIDAY(Calendar.FRIDAY, "Friday"), SATURDAY(Calendar.SATURDAY, "Saturday"),
        SUNDAY(Calendar.SUNDAY, "Sunday"), INVALID(-1, "INVALID");

        private Integer value;
        private String name;

        Weekday(Integer w, String name) {
            value = w;
            this.name = name;
        }

        public static Weekday get(int w) {
            switch (w) {
                case Calendar.MONDAY:
                    return MONDAY;
                case Calendar.TUESDAY:
                    return TUESDAY;
                case Calendar.WEDNESDAY:
                    return WEDNESDAY;
                case Calendar.THURSDAY:
                    return THURSDAY;
                case Calendar.FRIDAY:
                    return FRIDAY;
                case Calendar.SATURDAY:
                    return SATURDAY;
                case Calendar.SUNDAY:
                    return SUNDAY;
                default:
                    return INVALID;
            }
        }

        public Integer value() {
            return value;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum Month {
        JANUARY(Calendar.JANUARY, "January"), FEBRUARY(Calendar.FEBRUARY, "February"),
        MARCH(Calendar.MARCH, "March"), APRIL(Calendar.APRIL, "April"), MAY(Calendar.MAY, "May"),
        JUNE(Calendar.JUNE, "June"), JULY(Calendar.JULY, "July"), AUGUST(Calendar.AUGUST, "August"),
        SEPTEMBER(Calendar.SEPTEMBER, "September"), OCTOBER(Calendar.OCTOBER, "October"),
        NOVEMBER(Calendar.NOVEMBER, "November"), DECEMBER(Calendar.DECEMBER, "December"),
        INVALID(-1, "INVALID");

        private Integer value;
        private String name;

        Month(Integer m, String name) {
            value = m;
            this.name = name;
        }

        public static Month get(int m) {
            switch (m) {
                case Calendar.JANUARY:
                    return JANUARY;
                case Calendar.FEBRUARY:
                    return FEBRUARY;
                case Calendar.MARCH:
                    return MARCH;
                case Calendar.APRIL:
                    return APRIL;
                case Calendar.MAY:
                    return MAY;
                case Calendar.JUNE:
                    return JUNE;
                case Calendar.JULY:
                    return JULY;
                case Calendar.AUGUST:
                    return AUGUST;
                case Calendar.SEPTEMBER:
                    return SEPTEMBER;
                case Calendar.OCTOBER:
                    return OCTOBER;
                case Calendar.NOVEMBER:
                    return NOVEMBER;
                case Calendar.DECEMBER:
                    return DECEMBER;
                default:
                    return INVALID;
            }
        }

        public Integer value() {
            return value;
        } // +1 ?

        @Override
        public String toString() {
            return name;
        }

    }


    /**
     * CONSTRUCTOR(S)
     */
    public ShiftDate(int day, Weekday weekday, Month month, int year) {
        this.day = day;
        this.weekday = weekday;
        this.month = month;
        this.year = year;
    }

    /**
     * GETTERS
     */
    public int getDay() {
        return day;
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public Month getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }


    /**
     * CONVERTERS
     */




    public String toString() {
        return weekday.toString() + ", " + day + " " + month.toString() + " " + year;
    }

//    public String getWeekEnd(int weeks) {
//        Calendar c = Calendar.getInstance();
////        c.setFirstDayOfWeek(Calendar.MONDAY);
//        c.set(this.year, this.month.value() - 1, this.day, 12, 59, 59);
//        c.add(Calendar.DAY_OF_MONTH, 7 - weekday.value());
//        c.add(Calendar.WEEK_OF_YEAR, weeks);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
//        return sdf.format(c.getTime());
//    }
}
