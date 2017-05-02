package io.bradenhart.shifty.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bradenhart on 4/04/17.
 */

public class WorkWeek implements Serializable {

    private List<Shift> shifts;
    private Double hours;
    private Payslip payslip;

    public WorkWeek(List<Shift> shifts) {
        this.shifts = shifts;
        this.hours = calculateHours(shifts);
        this.payslip = new Payslip(hours);
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

//    public Double getNetPay() {
//        return payslip.getNet();
//    }

    public Payslip getPayslip() {
        return payslip;
    }

}
