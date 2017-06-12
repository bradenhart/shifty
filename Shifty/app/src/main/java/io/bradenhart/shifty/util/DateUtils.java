package io.bradenhart.shifty.util;

import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for dates and datetimes.
 * Contains format strings for formatting and parsing dates and strings.
 * Contains methods for getting different parts of a date/datetime or
 * dates in a particular format.
 *
 * @author bradenhart
 */
public class DateUtils {

    public static final String FMT_ISO_8601_DATETIME = "yyyy-MM-dd HH:mm:ss.sss";
    public static final String FMT_ISO_8601_DATE = "yyyy-MM-dd";
    public static final String FMT_ISO_8601_TIME = "HH:MM:SS.sss";
    public static final String FMT_WEEKDAY_FULL = "EEEE";
    public static final String FMT_DATETIME_PD = "yyyy-MM-dd HH:mm:ss a";
    public static final String FMT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FMT_YEAR_FULL = "yyyy";
    public static final String FMT_MONTH_SHORT = "M";
    public static final String FMT_DAY_SHORT = "d";
    public static final String FMT_TIME_SHORT = "hh:mm a";
    public static final String FMT_DAY_DATE = "dd MMM yyyy";
    public static final String FMT_PRETTY_DATE = "EEEE, d MMMM yyyy";

    /**
     * Gets a formatted date string in the provided format.
     * @param date the date to format
     * @param format the format for the datestring
     * @return the formatted datestring
     */
    public static String getDatestringWithFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        return sdf.format(date);
    }

    /**
     * Gets the datestring for the Monday before the given datestring (the start of the week).
     * If the datestring is for a Monday, it is already the correct date and will just
     * modify the time component of the datestring.
     * @param dateString the given datestring
     * @param format the format for the input and output datestring
     * @return the formatted datestring
     */
    public static String getWeekStart(String dateString, String format) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        try {
            c.setTime(sdf.parse(dateString));
            c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
            c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
            c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
            c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                c.add(Calendar.DAY_OF_WEEK, -6);
            } else {
                c.add(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() - c.get(Calendar.DAY_OF_WEEK) + 1);
            }
            return sdf.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "DATESTRING ERROR";
        }
    }

    /**
     * Gets the datestring for the Monday before the given date (the start of the week).
     * If the date is for a Monday, it is already the correct date and will just
     * modify the time component of the date.
     * @param date the given date
     * @param format the format for the output datestring
     * @return the formatted datestring
     */
    public static String getWeekStart(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        return getWeekStart(sdf.format(date), format);
    }

    /**
     * Gets the datestring for the Sunday after the given date (the end of the week).
     * If the date is for a Sunday, it is already the correct date and will just
     * modify the time component of the date.
     * @param dateString the given datestring
     * @param format the format for the input and output datestring
     * @return the formatted datestring
     */
    public static String getWeekEnd(String dateString, String format) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        try {
            c.setTime(sdf.parse(getWeekStart(dateString, format)));
            c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
            c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
            c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
            c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));
            c.add(Calendar.DAY_OF_YEAR, 6);
            return sdf.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "DATESTRING ERROR";
        }
    }

    /**
     * Gets the datestring for the Sunday after the given date (the end of the week).
     * If the date is for a Sunday, it is already the correct date and will just
     * modify the time component of the date.
     * @param date the given date
     * @param format the format for the output datestring
     * @return the formatted datestring
     */
    public static String getWeekEnd(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        return getWeekEnd(sdf.format(date), format);
    }

    /**
     * Gets the datestring for the start of the month for the given date.
     * @param date the given date
     * @param format the format for the output datestring
     * @return the formatted datestring
     */
    public static String getMonthStart(Date date, String format) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getMinimum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
        c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
        c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));

        return sdf.format(c.getTime());
    }

    /**
     * Gets the datestring for the end of the month for the given date.
     * @param date the given date
     * @param format the format for the output datestring
     * @return the formatted datestring
     */
    public static String getMonthEnd(Date date, String format) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
        c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
        c.set(Calendar.MILLISECOND, c.getMaximum(Calendar.MILLISECOND));

        return sdf.format(c.getTime());
    }

    /**
     * Gets a formatted datestring given another datestring.
     * The resulting datestring is user friendly e.g. Monday 1 June 2017.
     * @param dateString the datestring to format
     * @param format the format for the given datestring
     * @return the formatted datestring
     */
    public static String getPrettyDateString(String dateString, String format) {
        Date date = null;
        try {
            date = new SimpleDateFormat(format, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            return new SimpleDateFormat(FMT_PRETTY_DATE, Locale.ENGLISH).format(date);
        }

        return "";
    }

    /**
     * Gets a formatted datestring given a year, month, and day value.
     * The resulting datestring is user friendly e.g. Monday 1 June 2017.
     * @param year the year value for the datestring
     * @param month the month value for the datestring
     * @param day the day value for the datestring
     * @return the formatted datestring
     */
    public static String getPrettyDateString(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_PRETTY_DATE, Locale.ENGLISH);
        c.set(year, month, day);
        return sdf.format(c.getTime());
    }

    /**
     * Gets the day of the month for the given datestring.
     * @param dateString the datestring to get the day of the month from
     * @param format the format of the datestring
     * @return the day of the month as a string
     */
    public static String getDayOfMonth(String dateString, String format) {
        Date date;
        try {
            date = new SimpleDateFormat(format, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return "-1";
        }
        return new SimpleDateFormat("d", Locale.ENGLISH).format(date);
    }

    /**
     * Gets the weekday for the given datestring.
     * @param dateString the datestring to get the weekday from
     * @param fromformat the format of the given datestring
     * @param toFormat the format of the weekday string
     * @return the weekday as a string
     */
    public static String getWeekday(String dateString, String fromformat, String toFormat) {
        Date date;
        try {
            date = new SimpleDateFormat(fromformat, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return "ERR";
        }
        return new SimpleDateFormat(toFormat, Locale.ENGLISH).format(date);
    }

    /**
     * Gets the year for the given datestring.
     * @param dateString the datestring to get the year from
     * @param format the format of the given datestring
     * @return the year as an integer
     */
    public static Integer getYear(String dateString, String format) {
        Date date;
        try {
            date = new SimpleDateFormat(format, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return 1994;
        }
        String yearString = new SimpleDateFormat(FMT_YEAR_FULL, Locale.ENGLISH).format(date);
        Integer year;
        try {
            year = Integer.parseInt(yearString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return 1994;
        }
        return year;
    }

    /**
     * Gets the month for the given datestring.
     * @param dateString the datestring to get the month from
     * @param format the format of the given datestring
     * @return the month as an integer
     */
    public static Integer getMonth(String dateString, String format) {
        Date date;
        try {
            date = new SimpleDateFormat(format, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
        String monthString = new SimpleDateFormat(FMT_MONTH_SHORT, Locale.ENGLISH).format(date);
        Integer month;
        try {
            month = Integer.parseInt(monthString) - 1; // months start from 0 (Jan)
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return 1;
        }
        return month;
    }

    /**
     * Gets the day for the given datestring.
     * @param dateString the datestring to get the day from
     * @param format the format of the given datestring
     * @return the day as an integer
     */
    public static Integer getDay(String dateString, String format) {
        Date date;
        try {
            date = new SimpleDateFormat(format, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
        String dayString = new SimpleDateFormat(FMT_DAY_SHORT, Locale.ENGLISH).format(date);
        Integer day;
        try {
            day = Integer.parseInt(dayString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return 1;
        }
        return day;
    }

    /**
     * Gets the hour for the given datestring.
     * @param dateString the datestring to get the hour from
     * @param format the format of the given datestring
     * @return the hour as an integer
     */
    public static Integer getHour(String dateString, String format) {
        Date date;
        try {
            date = new SimpleDateFormat(format, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
        String dayString = new SimpleDateFormat("HH", Locale.ENGLISH).format(date);
        Integer hour;
        try {
            hour = Integer.parseInt(dayString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return 1;
        }
        return hour;
    }

    /**
     * Gets the minute from the given datestring.
     * @param dateString the datestring to get the minute from
     * @param format the format of the given datestring
     * @return the minute as an integer
     */
    public static Integer getMinute(String dateString, String format) {
        Date date;
        try {
            date = new SimpleDateFormat(format, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
        String dayString = new SimpleDateFormat("mm", Locale.ENGLISH).format(date);
        Integer minute;
        try {
            minute = Integer.parseInt(dayString);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return 0;
        }
        return minute;
    }

    /**
     * Gets the time period (am/pm) from the given datestring.
     * @param dateString the datestring to get the time period from
     * @param format the format of the given datestring
     * @return the period as a string
     */
    public static String getPeriod(String dateString, String format) {
        return getHour(dateString, format) < 12 ? "AM" : "PM";
    }

    /**
     * Gets the time string for the given datestring.
     * @param dateString the datestring to get the time from
     * @param fromFormat the format of the given datestring
     * @param toFormat the format of the time string
     * @return the formatted time string
     */
    public static String getTime(String dateString, String fromFormat, String toFormat) {
        Date date;
        try {
            date = new SimpleDateFormat(fromFormat, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return "ERR";
        }
        return new SimpleDateFormat(toFormat, Locale.ENGLISH).format(date);
    }

    /**
     * Get the number of hours between two datestrings.
     * @param dateString1 the first datestring
     * @param dateString2 the second datestring
     * @param format the format of the two datestrings
     * @return the number of hours between the two datestrings
     */
    public static double getHoursBetween(String dateString1, String dateString2, String format) {
        Date date1;
        Date date2;

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);

        try {
            date1 = sdf.parse(dateString1);
            date2 = sdf.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0.0;
        }

        long diff;

        int order = date2.compareTo(date1);
        if (order == 0) return 0.0;
        else if (order > 0) {
            diff = date2.getTime() - date1.getTime();
        } else {
            diff = date1.getTime() - date2.getTime();
        }

        // ms / 1000 = s; s / 60 = m; m / 60 = h
        BigDecimal result = new BigDecimal((double) diff / 1000 / 60 / 60);
        result = result.setScale(2, RoundingMode.DOWN);

        return result.doubleValue();
    }

    /**
     * Get the percentage of the shift length that that has passed between the current
     * time and the shift times.
     * If the shift hasn't started, the result is 0.0.
     * If the shift has finished, the result is 1.0.
     * @param startTime the start time for the shift
     * @param endTime the end time for the shift
     * @param format the format for the startTime and endTime
     * @return the progress through the shift
     */
    public static double getShiftProgress(String startTime, String endTime, String format) {
        // shift's datetime
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        try {
            start.setTime(sdf.parse(startTime));
            end.setTime(sdf.parse(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // current datetime
        Calendar now = Calendar.getInstance();
        if (now.before(start)) {
            return 0;
        }

        if (now.after(end)) {
            return 1;
        }

        if (now.after(start) && now.before(end)) {
            long mins = ((end.getTimeInMillis() - start.getTimeInMillis()) / 1000) / 60; // mins for shift
            long diff = ((now.getTimeInMillis() - start.getTimeInMillis()) / 1000) / 60; // mins passed
            return (double) diff / mins;
        }

        return 0;
    }

    /**
     * Gets a Date from a given datetime string.
     * @param datetime the datetime to convert to a date
     * @return the date
     */
    @Nullable
    public static Date getDateFromDateTime(String datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_DATETIME, Locale.ENGLISH);
        try {
            return sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the start date for the current week.
     * @return a formatted datestring for the start date
     */
    public static String getStartDateForCurrentWeek() {
        Calendar c = Calendar.getInstance();
        return getWeekStart(c.getTime(), FMT_ISO_8601_DATETIME);
    }

}
