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
    public @interface TaxBracket {
    }

    /* TaxRate constants */
    public static final int TAX_BRACKET_ONE = 1;
    public static final int TAX_BRACKET_TWO = 2;
    public static final int TAX_BRACKET_THREE = 3;
    public static final int TAX_BRACKET_FOUR = 4;


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({MODE_HOUR, MODE_GROSS})
    public @interface Mode {
    }

    /* Mode constants */
    public static final String MODE_HOUR = "MODE_HOUR";
    public static final String MODE_GROSS = "MODE_GROSS";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({STAFF_EMPLOYEE, STAFF_EMPLOYER})
    public @interface Staff {
    }

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
    private Double gross;
    private Double hours;
    private Double annual;
    private
    @TaxBracket
    int taxBracket;
    private Double payeTotal;
    private Double kiwisavEmployee;
    private Double kiwisavEmployer;
    private Double studentLoan;
    private Double net;

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

    /**
     * Gets the tax rate for the given bracket.
     *
     * @param bracket the tax bracket
     * @return the tax rate
     */
    private Double getRate(@TaxBracket int bracket) {
        switch (bracket) {
            case TAX_BRACKET_ONE:
                return 10.5 / 100;
            case TAX_BRACKET_TWO:
                return 17.5 / 100;
            case TAX_BRACKET_THREE:
                return 30.0 / 100;
            case TAX_BRACKET_FOUR:
            default:
                return 33.0 / 100;
        }
    }

//    public Double getBottomBound(@TaxBracket int bracket) {
//        Double bottom = 1.0;
//
//        switch (bracket) {
//            case TAX_BRACKET_ONE: return bottom;
//            case TAX_BRACKET_TWO: return bottom + 14000.0;
//            case TAX_BRACKET_THREE: return bottom + 30.0;
//            case TAX_BRACKET_FOUR: return bottom + 33.0;
//            default: return null;
//        }
//    }

    /**
     * Gets the tax bracket for the given annual earnings amount.
     *
     * @param annual the annual amount of earnings
     * @return the tax bracket
     */
    @TaxBracket
    private int getTaxBracket(Double annual) {
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

    /**
     * Gets the top bound of the tax bracket (the highest amount you can earn before
     * entering the next bracket up).
     *
     * @param bracket the tax bracket
     * @return the top bound of the bracket
     */
    private Double getTopBound(@TaxBracket int bracket) {
        switch (bracket) {
            case TAX_BRACKET_ONE:
                return 14000.0;
            case TAX_BRACKET_TWO:
                return 48000.0;
            case TAX_BRACKET_THREE:
                return 70000.0;
            case TAX_BRACKET_FOUR:
            default:
                return Double.POSITIVE_INFINITY;
        }
    }

    /**
     * Gets the maximum weekly gross amount that you can earn for the given
     * tax bracket.
     *
     * @param bracket the tax bracket
     * @return the maximum weekly amount
     */
    private Double getWeeklyMax(@TaxBracket int bracket) {
        return getTopBound(bracket) / 52;
    }

    /**
     * Calculates the gross amount for the given number of worked hours.
     *
     * @param hours the number of worked hours
     * @return the gross amount
     */
    private Double calculateGross(Double hours) {
        return HOURLY_RATE_FULL * hours;
    }

    /**
     * Calculates the projected annual amount for the given gross amount.
     *
     * @param gross the gross earnings
     * @return the project annual amount
     */
    private Double calculateAnnual(Double gross) {
        return gross * 52;
    }

    /**
     * Calculates the P.A.Y.E deduction for the given gross amount, tax bracket,
     * and running total for the deduction.
     *
     * @param gross   the gross amount
     * @param bracket the tax bracket
     * @param total   the running total for the calculation
     * @return the total P.A.Y.E deduction
     */
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

    /**
     * Calculates the kiwisaver contribution for the given gross amount and staff type.
     *
     * @param gross the gross amount
     * @param type  the staff type
     * @return the kiwisaver contribution
     */
    private Double calculateKiwiSaver(Double gross, @Staff String type) {
        if (type.equals(STAFF_EMPLOYEE)) {
            return gross * KIWISAVER_RATE_EMPLOYEE;
        } else {
            return gross * KIWISAVER_RATE_EMPLOYER;
        }
    }

    /**
     * Calculates the student load repayment for the given gross amount.
     *
     * @param gross the gross amount
     * @return the student loan repayment
     */
    private Double calculateStudentLoan(Double gross) {
        return (gross > 367.0) ? (gross - 367.0) * STUDENT_LOAN_RATE : 0.0;
    }

    /**
     * Calculates the net pay for the given gross amount, P.A.Y.E deduction,
     * kiwisaver contribution and student load repayment.
     *
     * @param gross           the gross amount
     * @param payeTotal       the P.A.Y.E deduction
     * @param kiwisavEmployee the kiwisaver contribution for an employee
     * @param studentLoan     the student loan repayment
     * @return the net pay
     */
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
