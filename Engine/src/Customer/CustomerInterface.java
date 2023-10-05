package Customer;

import Account.impl.Loan;

public interface CustomerInterface {

    /**
     * This method inserts new loan to this customer's lender list.
     * @param newLoan add new loan to the lender list.
     * @param yaz current date for the new loan.
     * @param contribution is the amount of money this customer invested in the loan.
     */
    public void addNewLoanToLenderList(Loan newLoan, double contribution, int yaz);

    /**
     * This method inserts new loan to this customer's borrower list.
     * @param newLoan add new loan to the borrower list.
     */
    public void addNewLoanToBorrowerList(Loan newLoan);

    /**
     * This method gets amount of money to deposit to this customer account.
     * It updates the customer's capital amount
     * @param sum amount of money to deposit
     * @param yaz current date
     */
    public void depositMoney(int sum, int yaz);

    /**
     * This method gets amount of money to withdraw from the customer account.
     * It updates the customer's capital amount
     * @param sum amount of money to deposit
     * @param yaz current date
     */
    public void moneyWithdrawal(double sum, int yaz);
}
