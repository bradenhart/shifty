package io.bradenhart.shifty.domain;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents a payslip for a workweek.
 *
 * @author bradenhart
 */
public class Payslip implements Serializable {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TAX_BRACKET_ONE, TAX_BRACKET_TWO, TAX_BRACKET_THREE, TAX_BRACKET_FOUR})
    public @interface TaxBracket {}

    /* TaxRate constants */
    public static final int TAX_BRACKET_ONE = 1;
    public static final int TAX_BRACKET_TWO = 2;
    public static final int TAX_BRACKET_THREE = 3;
    public static final int TAX_BRACKET_FOUR = 4;


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({MODE_HOUR, MODE_GROSS})
    public @interface Mode {}

    /* Mode constants */
    public static final String MODE_HOUR = "MODE_HOUR";
    public static final String MODE_GROSS = "MODE_GROSS";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({STAFF_EMPLOYEE, STAFF_EMPLOYER})
    public @interface Staff {}

    /* Staff constants */
    public static final String STAFF_EMPLOYEE = "STAFF_EMPLOYEE";
    public static final String STAFF_EMPLOYER = "STAFF_EMPLOYER";

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
     * FIELDS
     */
    Double gross;
    Double hours;
    Double annual;
    @TaxBracket int taxBracket;
    Double payeTotal;
    Double kiwisavEmployee;
    Double kiwisavEmployer;
    Double studentLoan;
    Double net;

    /**
     * CONSTRUCTORS
     */
    public Payslip(Double hours) {
        this.gross = calculateGross(hours);
        this.hours = hours;
        this.annual = calculateAnnual(gross);
        this.taxBracket = getTaxBracket(annual);
        this.payeTotal = calculatePaye(gross, taxBracket, 0.0);
        this.kiwisavEmployee = calculateKiwiSaver(gross, STAFF_EMPLOYEE);
        this.kiwisavEmployer = calculateKiwiSaver(gross, STAFF_EMPLOYER);
        this.studentLoan = calculateStudentLoan(gross);
        this.net = calculateNet(gross, payeTotal, kiwisavEmployee, studentLoan);
    }

    public Payslip(Double value, @Mode String mode) {
        if (mode.equals(MODE_HOUR)) {
            this.hours = value;
            this.gross = calculateGross(value);
        } else {
            this.gross = value;
            this.hours = value / HOURLY_RATE_FULL;
        }

        this.annual = calculateAnnual(gross);
        this.taxBracket = getTaxBracket(annual);
        this.payeTotal = calculatePaye(gross, taxBracket, 0.0);
        this.kiwisavEmployee = calculateKiwiSaver(gross, STAFF_EMPLOYEE);
        this.kiwisavEmployer = calculateKiwiSaver(gross, STAFF_EMPLOYER);
        this.studentLoan = calculateStudentLoan(gross);
        this.net = calculateNet(gross, payeTotal, kiwisavEmployee, studentLoan);
    }

    /**
     * METHODS
     */
    public Double getRate(@TaxBracket int bracket) {
        switch (bracket) {
            case TAX_BRACKET_ONE: return 10.5/100;
            case TAX_BRACKET_TWO: return 17.5/100;
            case TAX_BRACKET_THREE: return 30.0/100;
            case TAX_BRACKET_FOUR: return 33.0/100;
            default: return null;
        }
    }

    public Double getBottom(@TaxBracket int bracket) {
        Double bottom = 1.0;

        switch (bracket) {
            case TAX_BRACKET_ONE: return bottom;
            case TAX_BRACKET_TWO: return bottom + 14000.0;
            case TAX_BRACKET_THREE: return bottom + 30.0;
            case TAX_BRACKET_FOUR: return bottom + 33.0;
            default: return null;
        }
    }

    @TaxBracket
    public int getTaxBracket(Double annual) {
        if (annual < 14000) {
            return TAX_BRACKET_ONE;
        } else if (annual < 48000) {
            return TAX_BRACKET_TWO;
        } else if (annual < 70000) {
            return TAX_BRACKET_THREE;
        } else {
            return TAX_BRACKET_FOUR;
        }
    }

    public Double getTop(@TaxBracket int bracket) {
        switch (bracket) {
            case TAX_BRACKET_ONE:
                return 14000.0;
            case TAX_BRACKET_TWO:
                return 48000.0;
            case TAX_BRACKET_THREE:
                return 70000.0;
            case TAX_BRACKET_FOUR:
                return Double.POSITIVE_INFINITY;
            default: return null;
        }
    }

    public Double getWeeklyMax(@TaxBracket int bracket) {
        return getTop(bracket) / 52;
    }


    private Double calculateGross(Double hours) {
        return HOURLY_RATE_FULL * hours;
    }

    private Double calculateAnnual(Double gross) {
        return gross * 52;
    }

    private Double calculatePaye(Double gross, @TaxBracket int bracket, Double total) {
        Double taxedAmount;
        Double payeRate = getRate(bracket) + ACC_EARNERS_LEVY_RATE;
        Double payeDeduction;
        Double runningTotal;

        if (bracket == TAX_BRACKET_ONE) {
            taxedAmount = gross;
            payeDeduction = payeRate * taxedAmount;
            runningTotal = total + payeDeduction;
            return runningTotal;
        } else if (bracket == TAX_BRACKET_TWO) {
            taxedAmount = gross - getWeeklyMax(TAX_BRACKET_ONE);
            payeDeduction = payeRate * taxedAmount;
            runningTotal = total + payeDeduction;
            return calculatePaye(getWeeklyMax(TAX_BRACKET_ONE), TAX_BRACKET_ONE, runningTotal);
        } else if (bracket == TAX_BRACKET_THREE) {
            taxedAmount = gross - getWeeklyMax(TAX_BRACKET_TWO);
            payeDeduction = payeRate * taxedAmount;
            runningTotal = total + payeDeduction;
            return calculatePaye(getWeeklyMax(TAX_BRACKET_TWO), TAX_BRACKET_TWO, runningTotal);
        } else {
            taxedAmount = gross - getWeeklyMax(TAX_BRACKET_THREE);
            payeDeduction = payeRate * taxedAmount;
            runningTotal = total + payeDeduction;
            return calculatePaye(getWeeklyMax(TAX_BRACKET_THREE), TAX_BRACKET_THREE, runningTotal);
        }
    }

    private Double calculateKiwiSaver(Double gross, @Staff String type) {
        if (type.equals(STAFF_EMPLOYEE)) {
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

    @Override
    public String toString() {
        return "Payslip{" +
                "gross=" + gross +
                ", hours=" + hours +
                ", annual=" + annual +
                ", taxBracket=" + taxBracket +
                ", payeTotal=" + payeTotal +
                ", kiwisavEmployee=" + kiwisavEmployee +
                ", kiwisavEmployer=" + kiwisavEmployer +
                ", studentLoan=" + studentLoan +
                ", net=" + net +
                '}';
    }
}
