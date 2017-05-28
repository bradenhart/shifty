package io.bradenhart.shifty.util;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bradenhart on 8/04/17.
 */

public class DateUtil {

    public static final String FMT_ISO_8601_DATETIME = "yyyy-MM-dd HH:mm:ss.sss";
    public static final String FMT_ISO_8601_DATE = "yyyy-MM-dd";
    public static final String FMT_ISO_8601_TIME = "HH:MM:SS.sss";
    public static final String FMT_WEEKDAY_FULL = "EEEE";
    //    public static final String FMT_DATETIME_PD = "yyyy-MM-dd HH:mm:ss a";
    public static final String FMT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    private static final String FMT_YEAR_FULL = "yyyy";
    private static final String FMT_MONTH_SHORT = "M";
    private static final String FMT_DAY_SHORT = "d";
    public static final String FMT_TIME_SHORT = "hh:mm a";
    public static final String FMT_DAY_DATE = "dd MMM yyyy";

    public static String getDatestringWithFormat(String format, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        return sdf.format(date);
    }

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

    public static String getWeekStart(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        return getWeekStart(sdf.format(date), format);
    }

//    public String getStartOfCurrentWeek(String format) {
//        Calendar c = Calendar.getInstance();
//        return getWeekStart(c.getTime(), format);
//    }

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

    public static String getWeekEnd(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        return getWeekEnd(sdf.format(date), format);
    }

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

    public static String getPrettyDateString(String dateString, String format) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(format, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            return new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH).format(date);
        }

        return "";
    }

    public static String getPrettyDateString(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH);
        c.set(year, month, day);
        return sdf.format(c.getTime());
    }

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

    public static String getPeriod(String dateString, String format) {
        return getHour(dateString, format) < 12 ? "AM" : "PM";
    }

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

        BigDecimal result = new BigDecimal((double) diff / 1000 / 60 / 60);
        result = result.setScale(2, RoundingMode.DOWN);

        // ms / 1000 = s; s / 60 = m; m / 60 = h
        return result.doubleValue();
    }

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
            long mins = ((end.getTimeInMillis() - start.getTimeInMillis()) / 1000) / 60; //mins for shift
            long diff = ((now.getTimeInMillis() - start.getTimeInMillis()) / 1000) / 60; //mins passed
            return (double) diff / mins;
        }

        return 0;
    }

    public static Date getDateFromDateTime(String datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_DATETIME, Locale.ENGLISH);
        try {
            Date date = sdf.parse(datetime);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getWorkWeekTitle(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_DAY_DATE, Locale.ENGLISH);

        String title = "";

        title += "(Mon) ";
        title += sdf.format(getDateFromDateTime(startDate));
        title += " - ";
        title += sdf.format(getDateFromDateTime(endDate));
        title += " (Sun)";

        return title;
    }

    public static String getStartDateForCurrentWeek() {
        Calendar c = Calendar.getInstance();
        return getWeekStart(c.getTime(), FMT_ISO_8601_DATETIME);
    }

}
