package DTO;

import Account.impl.LoanAction;

public class LoanActionDTO {
    private int yazDate;
    private double principal;
    private double interest;
    private double totalPayment;

    public LoanActionDTO(LoanAction curr) {
        this.yazDate = curr.getYazDate();
        this.principal = curr.getPrincipal();
        this.interest = curr.getInterest();
        this.totalPayment = curr.getTotalPayment();
    }

    @Override
    public String toString() {
        return  "Current Yaz: " + yazDate +
                "\nPrincipal: " + principal +
                "\nInterest: " + interest +
                "\nTotal Payment: " + totalPayment;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public int getYazDate() {
        return yazDate;
    }
}
