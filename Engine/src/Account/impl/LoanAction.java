package Account.impl;

import java.io.Serializable;

public class LoanAction implements Serializable {
    private final int yazDate;
    private double principal;
    private double interest;
    private double totalPayment;

    public LoanAction(int yazDate, double principal, double interest, double totalPayment) {
        this.yazDate = yazDate;
        this.principal = principal;
        this.interest = interest;
        this.totalPayment = totalPayment;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public int getYazDate() {
        return yazDate;
    }

    public double getPrincipal() {
        return principal;
    }

    public double getInterest() {
        return interest;
    }

    public void updateLoanAction(double interest, double principal) {
        this.totalPayment -= interest + principal;
        this.interest -= interest;
        this.principal -= principal;
    }
}
