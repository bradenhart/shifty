package io.bradenhart.shifty.domain;

import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

/**
 * Created by bradenhart on 13/03/17.
 */

public class ShiftTime implements Serializable {

    /**
     * FIELDS
     */
    private Hour hour;
    private Minute minute;
    private Period period;

    /**
     * ENUMS
     */
    public enum Hour {
        ONE(1, "1"),
        TWO(2, "2"),
        THREE(3, "3"),
        FOUR(4, "4"),
        FIVE(5, "5"),
        SIX(6, "6"),
        SEVEN(7, "7"),
        EIGHT(8, "8"),
        NINE(9, "9"),
        TEN(10, "10"),
        ELEVEN(11, "11"),
        TWELVE(12, "12"),
        INVALID(-1, "INVALID");

        private Integer value;
        private String hour;

        Hour(Integer i, String hour) {
            value = i;
            this.hour = hour;
        }

        public Integer value() {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        public static Hour get(String h) {
            switch (h) {
                case "1":
                    return ONE;
                case "2":
                    return TWO;
                case "3":
                    return THREE;
                case "4":
                    return FOUR;
                case "5":
                    return FIVE;
                case "6":
                    return SIX;
                case "7":
                    return SEVEN;
                case "8":
                    return EIGHT;
                case "9":
                    return NINE;
                case "10":
                    return TEN;
                case "11":
                    return ELEVEN;
                case "12":
                    return TWELVE;
                default:
                    Log.e("HOUR", h);
                    return INVALID;
            }
        }

        public static Hour get(int h) {
            if (h > 12) h -= 12;
            switch (h) {
                case 1:
                    return ONE;
                case 2:
                    return TWO;
                case 3:
                    return THREE;
                case 4:
                    return FOUR;
                case 5:
                    return FIVE;
                case 6:
                    return SIX;
                case 7:
                    return SEVEN;
                case 8:
                    return EIGHT;
                case 9:
                    return NINE;
                case 10:
                    return TEN;
                case 11:
                    return ELEVEN;
                case 12:
                    return TWELVE;
                default:
                    Log.e("HOUR", h + "");
                    return INVALID;
            }
        }
    }

    public enum Minute {
        ZERO(0, "00"),
        FIVE(5, "05"),
        TEN(10, "10"),
        FIFTEEN(15, "15"),
        TWENTY(20, "20"),
        TWENTYFIVE(25, "25"),
        THIRTY(30, "30"),
        THIRTYFIVE(35, "35"),
        FORTY(40, "40"),
        FORTYFIVE(45, "45"),
        FIFTY(50, "50"),
        FIFTYFIVE(55, "55"),
        INVALID(-1, "INVALID");

        private Integer value;
        private String minute;

        Minute(Integer i, String m) {
            value = i;
            minute = m;
        }

        public static Minute get(String m) {
            switch (m) {
                case "00":
                    return ZERO;
                case "05":
                    return FIVE;
                case "10":
                    return TEN;
                case "15":
                    return FIFTEEN;
                case "20":
                    return TWENTY;
                case "25":
                    return TWENTYFIVE;
                case "30":
                    return THIRTY;
                case "35":
                    return THIRTYFIVE;
                case "40":
                    return FORTY;
                case "45":
                    return FORTYFIVE;
                case "50":
                    return FIFTY;
                case "55":
                    return FIFTYFIVE;
                default:
                    return INVALID;
            }
        }

        public static Minute get(int m) {
            switch (m) {
                case 0:
                    return ZERO;
                case 5:
                    return FIVE;
                case 10:
                    return TEN;
                case 15:
                    return FIFTEEN;
                case 20:
                    return TWENTY;
                case 25:
                    return TWENTYFIVE;
                case 30:
                    return THIRTY;
                case 35:
                    return THIRTYFIVE;
                case 40:
                    return FORTY;
                case 45:
                    return FORTYFIVE;
                case 50:
                    return FIFTY;
                case 55:
                    return FIFTYFIVE;
                default:
                    return INVALID;
            }
        }

        public Integer value() {
            return value;
        }

        @Override
        public String toString() {
            return minute;
        }

    }

    public enum Period {
        AM(Calendar.AM, "AM"),
        PM(Calendar.PM, "PM"),
        INVALID(-1, "INVALID");

        private Integer value;
        private String period;

        Period(Integer i, String period) {
            value = i;
            this.period = period;
        }

        public static Period get(String p) {
            switch (p.toUpperCase()) {
                case "AM":
                    return AM;
                case "PM":
                    return PM;
                default:
                    return INVALID;
            }
        }

        public static Period get(int p) {
            switch (p) {
                case 0: return AM;
                case 1: return PM;
                default: return INVALID;
            }
        }

        public Integer value() {
            return value;
        }

        @Override
        public String toString() {
            return period;
        }
    }

    /**
     * Constructor(s)
     */
    public ShiftTime(Hour hour, Minute minute, Period period) {
        this.hour = hour;
        this.minute = minute;
        this.period = period;
    }

    @Override
    public String toString() {
        return hour.toString() + ":" + minute.toString() + " " + period.toString();
    }

    public Hour getHour() {
        return hour;
    }

    public Minute getMinute() {
        return minute;
    }

    public Period getPeriod() {
        return period;
    }

    public boolean before(ShiftTime otherTime) {
        // times are equals so one is not before the other
        if (this.equals(otherTime)) return false;
        // first time is later than the other time
        if (this.getPeriod() == Period.PM && otherTime.getPeriod() == Period.AM) return false;

        // times are in the same period (AM/PM)
        if (this.getPeriod() == otherTime.getPeriod()) {
            // first time's hour is before the other time's hour
            if (this.hour.value() < otherTime.getHour().value()) return true;
                // times are in the same hour
            else if (Objects.equals(this.hour.value(), otherTime.getHour().value())) {
                // if first time's minute is before the other time's minute, return true
                return this.minute.value() < otherTime.getMinute().value();
            } else return false; // first time's hour is after the other time's hour
        } else return true; // first time's period is after the other time's period
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShiftTime time = (ShiftTime) o;

        if (hour != time.hour) return false;
        if (minute != time.minute) return false;
        return period == time.period;
    }

    @Override
    public int hashCode() {
        int result = hour.hashCode();
        result = 31 * result + minute.hashCode();
        result = 31 * result + period.hashCode();
        return result;
    }
}
