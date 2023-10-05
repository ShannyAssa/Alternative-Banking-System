package DTO;

import Account.impl.Loan;

public class LoanForSaleDTO {
    private LoanDTO loan;
    private String sellerName;
    private double price;

    public LoanForSaleDTO(LoanDTO loan, String sellerName, double price) {
        this.loan = loan;
        this.sellerName = sellerName;
        this.price = price;
    }

    public String getLoanId() {
        return this.loan.getLoanId();
    }

    public String getCategory() {
        return this.loan.getCategory();
    }

    public double getPrincipal() {
        return this.loan.getPrincipal();
    }

    public double getInterestRate() {
        return this.loan.getInterestRate();
    }

    public double getPrincipalWithInterest() {
        return this.loan.getPrincipalWithInterest();
    }

    public int getPaymentInterval() {
        return this.loan.getPaymentInterval();
    }

    public Loan.Status getStatus() {
        return this.loan.getStatus();
    }

    public String getSellerName() {
        return sellerName;
    }

    public double getPriceForSale() {
        return price;
    }
}
