package io.bradenhart.shifty.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.bradenhart.shifty.util.DateUtil;

/**
 * Created by bradenhart on 4/04/17.
 */

public class WorkWeek implements Serializable {

    private List<Shift> shifts;
    private Double hours;
    private Payslip payslip;
    private String weekDate;

    public WorkWeek(String weekDate, List<Shift> shifts) {
        this.shifts = shifts;
        this.hours = calculateHours(shifts);
        this.payslip = new Payslip(weekDate, hours);
        this.weekDate = weekDate;
    }

    private Double calculateHours(List<Shift> shifts) {
        Double hours = 0.0;
        for (Shift s : shifts) {
            hours += s.getPaidHours();
        }
        return hours;
    }

    public List<Shift> getShifts() {
        return shifts;
    }

    public Payslip getPayslip() {
        return payslip;
    }

    public String getTitle() {
        SimpleDateFormat fromDateFormat = new SimpleDateFormat(DateUtil.FMT_DATETIME, Locale.ENGLISH);
        SimpleDateFormat toDateFormat = new SimpleDateFormat("MMMM dd ''yy", Locale.ENGLISH);

        try {
            Date date = fromDateFormat.parse(weekDate);
            return "Week of " + toDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }


}
