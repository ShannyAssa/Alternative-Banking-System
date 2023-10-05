package Account;

import Customer.impl.Customer;

public interface LoanInterface {

    /**
     * @param amount is the lender's contribution to the loan.
     * @param yaz the method updates the loan starting date in case the status becomes active.
     * @param lender the method assigns the lender to the loan.
     * The method as well updates the loan's pending money and owner capital.
     */
    public void assignLenderAndMoney(Customer lender, double amount, int yaz);

    /**
     * @param yaz is used to update both the borrower's account action and the loan's repayment history.
     * @param borrower is the customer who repays the loan debt.
     */
    public void completePayment(int yaz, Customer borrower);
}
