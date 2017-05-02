package io.bradenhart.shifty.util;

import android.util.Log;

import io.bradenhart.shifty.domain.Shift;
import io.bradenhart.shifty.domain.ShiftTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bradenhart on 8/04/17.
 */

public class DateUtil {

    public static final String FMT_WEEKDAY_FULL = "EEEE";
    public static final String FMT_DATETIME_PD = "yyyy-MM-dd HH:mm:ss a";
    public static final String FMT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    private static final String FMT_YEAR_FULL = "yyyy";
    private static final String FMT_MONTH_SHORT = "M";
    private static final String FMT_DAY_SHORT = "d";
    private static final String FMT_DAY_DATE = "dd MMM ''yy";

//    public DateUtil() {
//
//    }
//
//    public DateUtil(Calendar calendar) {
//
//    }
//
//    public DateUtil(Shift shift) {
//
//    }
//
//    public DateUtil(Date date) {
//
//    }

    public static String getWeekStart(String dateString) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_DATETIME, Locale.ENGLISH);
        try {
            c.setTime(sdf.parse(dateString));
            c.set(Calendar.HOUR, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.AM_PM, Calendar.AM);
//            c.setFirstDayOfWeek(Calendar.MONDAY);
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

    public static String getWeekEnd(String dateString) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_DATETIME, Locale.ENGLISH);
        try {
            c.setTime(sdf.parse(getWeekStart(dateString)));
//            c.setFirstDayOfWeek(Calendar.MONDAY);
            c.set(Calendar.HOUR, 11);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            c.set(Calendar.AM_PM, Calendar.PM);
            c.add(Calendar.DAY_OF_YEAR, 6);
//            c.set(Calendar.DAY_OF_MONTH, 6);
            return sdf.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "DATESTRING ERROR";
        }
    }

    // -ve offset means start in the past, +ve offset means start in the future,
    // 0 means start in current week
    public static String[] getDateTimesForRange(int weeks, int offset) {
        // get calendar instance for Now
        Calendar c = Calendar.getInstance();
        // get weekstart date for start of range
        c.add(Calendar.WEEK_OF_YEAR, offset);

        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            c.add(Calendar.DAY_OF_WEEK, -6);
        } else {
            c.add(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() - c.get(Calendar.DAY_OF_WEEK) + 1);
        }

        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.AM_PM, Calendar.AM);

        SimpleDateFormat sdf = new SimpleDateFormat(FMT_DATETIME, Locale.ENGLISH);

        String[] datetimes = new String[weeks+1];
        datetimes[0] = sdf.format(c.getTime());

        for (int i = 1; i <= weeks; i++) {
            Log.e("LOG", "entering for loop");
            c.add(Calendar.WEEK_OF_YEAR, 1);
            datetimes[i] = sdf.format(c.getTime());
        }

        Log.e("LOG", "datetimes " + datetimes.length);
        return datetimes;
    }


    public static String getYMDString(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        c.set(year, month, day);
        return sdf.format(c.getTime());
    }

    public static String getDateString(String ymd, ShiftTime time) {
        return String.format(Locale.ENGLISH,
                            "%s %02d:%02d:00",
                            ymd,
                            time.getHour().value(),
                            time.getMinute().value());
    }

    public static String getPrettyDateString(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH);
        c.set(year, month, day);
        return sdf.format(c.getTime());
    }

    public static String getDayOfMonth(String dateString) {
        Date date;
        try {
            date = new SimpleDateFormat(FMT_DATETIME, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return "-1";
        }
        return new SimpleDateFormat("d", Locale.ENGLISH).format(date);
    }

    public static String getWeekday(String dateString, String format) {
        Date date;
        try {
            date = new SimpleDateFormat(FMT_DATETIME, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return "ERR";
        }
        return new SimpleDateFormat(format, Locale.ENGLISH).format(date);
    }

    public static Integer getYear(String dateString) {
        Date date;
        try {
            date = new SimpleDateFormat(FMT_DATETIME, Locale.ENGLISH).parse(dateString);
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

    public static Integer getMonth(String dateString) {
        Date date;
        try {
            date = new SimpleDateFormat(FMT_DATETIME, Locale.ENGLISH).parse(dateString);
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

    public static Integer getDay(String dateString) {
        Date date;
        try {
            date = new SimpleDateFormat(FMT_DATETIME, Locale.ENGLISH).parse(dateString);
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

    public static double getShiftProgress(Shift shift) {
        // shift's datetime
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_DATETIME, Locale.ENGLISH);
        try {
            start.setTime(sdf.parse(shift.getStartDateTimeString()));
            end.setTime(sdf.parse(shift.getEndDateTimeString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // current datetime
        Calendar now = Calendar.getInstance();
//        try {
//            now.setTime(sdf.parse("2017-04-18 09:00:00"));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        if (now.before(start)) {
            Log.e("Progress", sdf.format(now.getTime()) + " Before " + shift.getStartDateTimeString());
            return 0;
        }

        if (now.after(end)) {
            Log.e("Progress", sdf.format(now.getTime()) + " After " + shift.getEndDateTimeString());
            return 1;
        }

        if (now.after(start) && now.before(end)) {
            Log.e("Progress", shift.getId() + " Equal");
//            return (int) ((end.getTimeInMillis() - now.getTimeInMillis()));
            long mins = ((end.getTimeInMillis() - start.getTimeInMillis())/1000)/60; //mins for shift
            long diff = ((now.getTimeInMillis() - start.getTimeInMillis())/1000)/60; //mins passed
            return (double) diff/mins;
        }

        Log.e("Progress", "Nothing");
        return 0;
    }

    public static Date getDateFromDateTime(String datetime) {
        Calendar c = Calendar.getInstance();
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

    /*public static String[] getDateRange(int weeks) {
        String[] range = new String[2];
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_DATETIME, Locale.ENGLISH);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.AM_PM, Calendar.AM);
        range[0] = sdf.format(c.getTime());

        c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.AM_PM, Calendar.AM);
        c.add(Calendar.WEEK_OF_YEAR, weeks);
        range[1] = sdf.format(c.getTime());

        return range;
    }*/

}
