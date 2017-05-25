package io.bradenhart.shifty;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.bradenhart.shifty.util.DateUtil;
import io.bradenhart.shifty.util.Utils;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void weekStartFromMorningShift() {
        /*Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FMT_ISO_8601_DATETIME, Locale.ENGLISH);

        try {
            c.setTime(sdf.parse("2017-05-24 09:30:00.000"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));

        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

        assertEquals(0, hourOfDay);*/
        String result = DateUtil.getWeekStart("2017-05-24 09:30:00.000", DateUtil.FMT_ISO_8601_DATETIME);
        String expected = "2017-05-22 00:00:00.000";

        assertEquals("DateUtil.getWeekStart fails", expected, result);
    }

    @Test
    public void weekStartFromMiddayShift() {
        /*Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FMT_ISO_8601_DATETIME, Locale.ENGLISH);

        try {
            c.setTime(sdf.parse("2017-05-24 12:00:00.000"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));

        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

        assertEquals(0, hourOfDay);*/
        String result = DateUtil.getWeekStart("2017-05-24 12:00:00.000", DateUtil.FMT_ISO_8601_DATETIME);
        String expected = "2017-05-22 00:00:00.000";

        assertEquals("DateUtil.getWeekStart fails", expected, result);
    }

    @Test
    public void weekStartFromAfternoonShift() {
        /*Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FMT_ISO_8601_DATETIME, Locale.ENGLISH);

        try {
            c.setTime(sdf.parse("2017-05-24 12:30:00.000"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));

        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

        assertEquals(0, hourOfDay);*/
        String result = DateUtil.getWeekStart("2017-05-24 12:30:00.000", DateUtil.FMT_ISO_8601_DATETIME);
        String expected = "2017-05-22 00:00:00.000";

        assertEquals("DateUtil.getWeekStart fails", expected, result);
    }

    @Test
    public void weekStartFromEveningShift() {
        /*Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FMT_ISO_8601_DATETIME, Locale.ENGLISH);

        try {
            c.setTime(sdf.parse("2017-05-24 18:00:00.000"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));

        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

        assertEquals(0, hourOfDay);*/
        String result = DateUtil.getWeekStart("2017-05-24 18:00:00.000", DateUtil.FMT_ISO_8601_DATETIME);
        String expected = "2017-05-22 00:00:00.000";

        assertEquals("DateUtil.getWeekStart fails", expected, result);
    }


    /* WEEK END */
    @Test
    public void weekEndFromMorningShift() {
        String result = DateUtil.getWeekEnd("2017-05-24 11:30:00.000", DateUtil.FMT_ISO_8601_DATETIME);
        String expected = "2017-05-28 23:59:59.059";

        assertEquals("DateUtil.getWeekEnd fails", expected, result);
    }

    @Test
    public void weekEndFromMiddayShift() {
        String result = DateUtil.getWeekEnd("2017-05-24 12:00:00.000", DateUtil.FMT_ISO_8601_DATETIME);
        String expected = "2017-05-28 23:59:59.059";

        assertEquals("DateUtil.getWeekEnd fails", expected, result);
    }

    @Test
    public void weekEndFromAfternoonShift() {
        String result = DateUtil.getWeekEnd("2017-05-24 12:30:00.000", DateUtil.FMT_ISO_8601_DATETIME);
        String expected = "2017-05-28 23:59:59.059";

        assertEquals("DateUtil.getWeekEnd fails", expected, result);
    }

    @Test
    public void weekEndFromEveningShift() {
        String result = DateUtil.getWeekEnd("2017-05-24 18:00:00.000", DateUtil.FMT_ISO_8601_DATETIME);
        String expected = "2017-05-28 23:59:59.059";

        assertEquals("DateUtil.getWeekEnd fails", expected, result);
    }

    @Test
    public void getHoursBetweenShiftTimes() {
//        "2017-03-29 09:30:00.000", "2017-03-29 18:00:00.000"
        ArrayList<Object[]> shiftsList = new ArrayList<>();
        shiftsList.add(new Object[] {"2017-03-29 09:30:00.000", "2017-03-29 18:00:00.000", 8.5});

        for (Object[] shifts : shiftsList) {
            Double hours = DateUtil.getHoursBetween((String) shifts[0], (String) shifts[1], DateUtil.FMT_ISO_8601_DATETIME);
            assertEquals(shifts[2], hours);
        }
    }

}