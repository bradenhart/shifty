package io.bradenhart.shifty.domain;

import io.bradenhart.shifty.util.DateUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bradenhart on 15/12/16.
 */

public class Shift implements Serializable {

    /**
     * FIELDS
     */
    private String id;

    private Date date;

    private ShiftTime startTime;
    private ShiftTime endTime;

    private Double shiftLength;
    private Double paidHours;

    /**
     * CONSTRUCTORS
     */
    public Shift() {

    }

    public Shift(String id, Date date, ShiftTime startTime, ShiftTime endTime) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shiftLength = calculateTotalHours(startTime, endTime);
        this.paidHours = shiftLength <= 5.0 ? shiftLength : shiftLength - 0.5;
//        Log.e("SHIFT", toString());
    }

    public Shift(Date date, ShiftTime startTime, ShiftTime endTime) {
//        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shiftLength = calculateTotalHours(startTime, endTime);
        this.paidHours = shiftLength <= 5.0 ? shiftLength : shiftLength - 0.5;
        this.id = formStartDateTimeString();

//      Log.e("SHIFT", toString());
    }


    public Shift(int year, int month, int day, int hour1, int min1, int pd1, int hour2, int min2, int pd2) {
        this.startTime = new ShiftTime(ShiftTime.Hour.get(hour1), ShiftTime.Minute.get(min1), ShiftTime.Period.get(pd1));
        this.endTime = new ShiftTime(ShiftTime.Hour.get(hour2), ShiftTime.Minute.get(min2), ShiftTime.Period.get(pd2));
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour1);
        c.set(Calendar.MINUTE, min1);
        c.set(Calendar.SECOND, 0);
        this.date = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FMT_DATETIME, Locale.ENGLISH);
        this.id = sdf.format(date);
    }

    /**
     * GETTERS
     */
    public Date getDate() {
        return date;
    }

    public ShiftTime getStartTime() {
        return startTime;
    }

    public ShiftTime getEndTime() {
        return endTime;
    }

    public Double getShiftLength() {
        return shiftLength;
    }

    public Double getPaidHours() {
        return paidHours;
    }

    public String getId() {
        return id;
    }

    /**
     * SETTERS
     */
    public void setId(String id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setStartTime(ShiftTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(ShiftTime endTime) {
        this.endTime = endTime;
    }

    public void setShiftLength(Double shiftLength) {
        this.shiftLength = shiftLength;
    }

    public void setPaidHours(Double paidHours) {
        this.paidHours = paidHours;
    }

    /**
     * METHODS
     */
    private Double calculateTotalHours(ShiftTime start, ShiftTime end) {
        if (start == null || end == null || end.before(start)) return 0.0;

        if (start.getPeriod() == end.getPeriod()) {
            // 8:00am - 11.45am
            Integer total = ((end.getHour().value() - start.getHour().value()) * 60)
                    - start.getMinute().value()
                    + end.getMinute().value();
            return total / 60.0;
        } else {
            // 8:00am - 4:30pm
            Integer total = (((12 - start.getHour().value()) + end.getHour().value()) * 60)
                    - start.getMinute().value()
                    + end.getMinute().value();
            return total / 60.0;
        }
    }

//    public String displayShiftHours() {
//        return getStartHr() + ":" + getStartMin() + "am"
//                + " - "
//                + (Integer.parseInt(endHr) - 12) + ":" + getEndMin() + "pm";
//    }

    public String displayShiftHours() {
        return "";
    }

    private String formStartDateTimeString() {
        Calendar c = Calendar.getInstance();
//        c.set(Calendar.YEAR, date.getYear());
//        c.set(Calendar.MONTH, date.getMonth().value());
//        c.set(Calendar.DAY_OF_MONTH, date.getDay());
        c.setTime(date);
        c.set(Calendar.HOUR, startTime.getHour().value());
        c.set(Calendar.MINUTE, startTime.getMinute().value());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.AM_PM, startTime.getPeriod() == ShiftTime.Period.AM ? Calendar.AM : Calendar.PM);

        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FMT_DATETIME, Locale.ENGLISH);
        return sdf.format(c.getTime());
//        return String.format(Locale.ENGLISH, "%d-%02d-%02d %02d:%02d:00",
//                date.getYear(),
//                date.getMonth().value(),
//                date.getDay(),
//                startTime.getHour().value(),
//                startTime.getMinute().value());
    }

    private String formEndDateTimeString() {
        Calendar c = Calendar.getInstance();
//        c.set(Calendar.YEAR, date.getYear());
//        c.set(Calendar.MONTH, date.getMonth().value());
//        c.set(Calendar.DAY_OF_MONTH, date.getDay());

        c.setTime(date);
        c.set(Calendar.HOUR, endTime.getHour().value());
        c.set(Calendar.MINUTE, endTime.getMinute().value());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.AM_PM, endTime.getPeriod() == ShiftTime.Period.AM ? Calendar.AM : Calendar.PM);

        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.FMT_DATETIME, Locale.ENGLISH);
        return sdf.format(c.getTime());
    }

    public String getStartDateTimeString() {
        return formStartDateTimeString();
    }

    public String getEndDateTimeString() {
        return formEndDateTimeString();
    }

    @Override
    public String toString() {
        return "Shift{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", weekStart=" + DateUtil.getWeekStart(id) +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", shiftLength=" + shiftLength +
                ", paidHours=" + paidHours +
                '}';
    }
}