package io.bradenhart.shifty.domain;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.bradenhart.shifty.util.DateUtil;

/**
 * Created by bradenhart on 4/04/17.
 */

public class Payslip implements Serializable {

    /**
     * CONSTANTS
     */
    private final Double ACC_EARNERS_LEVY_RATE = 1.39 / 100;
    private final Double KIWISAVER_RATE_EMPLOYEE = 3.0 / 100;
    private final Double KIWISAVER_RATE_EMPLOYER = 3.0 / 100;
    private final Double STUDENT_LOAN_RATE = 12.0 / 100;
    private final Double HOURLY_RATE_BASE = 16.0;
    private final Double HOURLY_RATE_HOLIDAY = 8.0 / 100;
    private final Double HOURLY_RATE_FULL = HOURLY_RATE_BASE * (1 + HOURLY_RATE_HOLIDAY);

    /**
     * ENUMS
     */
    private enum TaxRate {

        BRACKET_ONE(1, 10.5, 0.0, 14000.0, "Up to $14,000"),
        BRACKET_TWO(2, 17.5, 14000.0, 48000.0, "Over $14,000 and up to $48,000"),
        BRACKET_THREE(3, 30.0, 48000.0, 70000.0, "Over $48,000 and up to $70,000"),
        BRACKET_FOUR(4, 33.0, 70000.0, Double.POSITIVE_INFINITY, "Remaining income over $70,000");

        Integer bracket;
        Double rate;
        Double bottom;
        Double top;
        String description;

        TaxRate(Integer bracket, Double rate, Double bottom, Double top, String description) {
            this.bracket = bracket;
            this.rate = rate / 100;
            this.bottom = bottom;
            this.top = top;
            this.description = description;
        }

        public Double getRate() {
            return this.rate;
        }

        public Double getBottom() {
            return this.bottom + 1.0;
        }

        public Integer getBracket() {
            return this.bracket;
        }

        public Double getTop() {
            return this.top;
        }

        public String getDescription() {
            return this.description;
        }

        public Double getWeeklyMax() {
            return this.top / 52;
        }

    }

    public static enum Mode {
        HOUR, GROSS
    }

    private enum Staff {
        EMPLOYEE, EMPLOYER
    }

    /**
     * FIELDS
     */
    Double gross;
    Double hours;
    Double annual;
    TaxRate rate;
    Double payeTotal;
    Double kiwisavEmployee;
    Double kiwisavEmployer;
    Double studentLoan;
    Double net;
    String weekDate;

    /**
     * CONSTRUCTORS
     */
    public Payslip(Double hours) {
        this.gross = calculateGross(hours);
        this.hours = hours;
        this.annual = calculateAnnual(gross);
        this.rate = getTaxRate(annual);
        this.payeTotal = calculatePaye(gross, getTaxRate(annual), 0.0, rate.getBracket());
        this.kiwisavEmployee = calculateKiwiSaver(gross, Staff.EMPLOYEE);
        this.kiwisavEmployer = calculateKiwiSaver(gross, Staff.EMPLOYER);
        this.studentLoan = calculateStudentLoan(gross);
        this.net = calculateNet(gross, payeTotal, kiwisavEmployee, studentLoan);
    }

    public Payslip(Double value, Mode mode) {
        if (mode == Mode.HOUR) {
            this.hours = value;
            this.gross = calculateGross(value);
        } else {
            // TODO implement calculating net from gross
        }

        this.annual = calculateAnnual(gross);
        this.rate = getTaxRate(annual);
        this.payeTotal = calculatePaye(gross, getTaxRate(annual), 0.0, rate.getBracket());
        this.kiwisavEmployee = calculateKiwiSaver(gross, Staff.EMPLOYEE);
        this.kiwisavEmployer = calculateKiwiSaver(gross, Staff.EMPLOYER);
        this.studentLoan = calculateStudentLoan(gross);
        this.net = calculateNet(gross, payeTotal, kiwisavEmployee, studentLoan);
    }

    /**
     * METHODS
     */
    private Double calculateGross(Double hours) {
        return HOURLY_RATE_FULL * hours;
    }

    private Double calculateAnnual(Double gross) {
        return gross * 52;
    }

    private TaxRate getTaxRate(Double annual) {
        if (annual < 14000) {
            return TaxRate.BRACKET_ONE;
        } else if (annual < 48000) {
            return TaxRate.BRACKET_TWO;
        } else if (annual < 70000) {
            return TaxRate.BRACKET_THREE;
        } else {
            return TaxRate.BRACKET_FOUR;
        }
    }

    private Double calculatePaye(Double gross, TaxRate rate, Double total, int n) {
        Double taxedAmount;
        Double payeRate = rate.getRate() + ACC_EARNERS_LEVY_RATE;
        Double payeDeduction;
        Double runningTotal;

        if (rate == TaxRate.BRACKET_ONE) {
            taxedAmount = gross;
            payeDeduction = payeRate * taxedAmount;
            runningTotal = total + payeDeduction;
            return runningTotal;
        } else if (rate == TaxRate.BRACKET_TWO) {
            taxedAmount = gross - TaxRate.BRACKET_ONE.getWeeklyMax();
            payeDeduction = payeRate * taxedAmount;
            runningTotal = total + payeDeduction;
            return calculatePaye(TaxRate.BRACKET_ONE.getWeeklyMax(), TaxRate.BRACKET_ONE, runningTotal, n - 1);
        } else if (rate == TaxRate.BRACKET_THREE) {
            taxedAmount = gross - TaxRate.BRACKET_TWO.getWeeklyMax();
            payeDeduction = payeRate * taxedAmount;
            runningTotal = total + payeDeduction;
            return calculatePaye(TaxRate.BRACKET_TWO.getWeeklyMax(), TaxRate.BRACKET_TWO, runningTotal, n - 1);
        } else {
            taxedAmount = gross - TaxRate.BRACKET_THREE.getWeeklyMax();
            payeDeduction = payeRate * taxedAmount;
            runningTotal = total + payeDeduction;
            return calculatePaye(TaxRate.BRACKET_THREE.getWeeklyMax(), TaxRate.BRACKET_THREE, runningTotal, n - 1);
        }
    }

    private Double calculateKiwiSaver(Double gross, Staff type) {
        if (type == Staff.EMPLOYEE) {
            return gross * KIWISAVER_RATE_EMPLOYEE;
        } else {
            return gross * KIWISAVER_RATE_EMPLOYER;
        }
    }

    private Double calculateStudentLoan(Double gross) {
        return (gross > 367.0) ? (gross - 367.0) * STUDENT_LOAN_RATE : 0.0;
    }

    private Double calculateNet(Double gross, Double payeTotal, Double kiwisavEmployee, Double studentLoan) {
        return gross - (payeTotal + kiwisavEmployee + studentLoan);
    }

    public String getTitle() {
        SimpleDateFormat fromDateFormat = new SimpleDateFormat(DateUtil.FMT_DATETIME, Locale.ENGLISH);
        SimpleDateFormat toDateFormat = new SimpleDateFormat("MMMM dd ''yy", Locale.ENGLISH);

        try {
            Date date = fromDateFormat.parse(DateUtil.getWeekEnd(weekDate, DateUtil.FMT_ISO_8601_DATETIME));
            return "Period Ending " + toDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * GETTERS
     */
    public Double getGross() {
        return gross;
    }

    public Double getHours() {
        return hours;
    }

    public Double getAnnual() {
        return annual;
    }

    public Double getPayeTotal() {
        return payeTotal;
    }

    public Double getKiwisavEmployee() {
        return kiwisavEmployee;
    }

    public Double getKiwisavEmployer() {
        return kiwisavEmployer;
    }

    public Double getStudentLoan() {
        return studentLoan;
    }

    public Double getNet() {
        return net;
    }


    public Double getBaseRate() {
        return HOURLY_RATE_BASE;
    }

    public Double getRateFull() {
        return HOURLY_RATE_FULL;
    }
}
