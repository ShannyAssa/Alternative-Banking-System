package Account;

public interface AccountInterface {

    /**
     * This method gets amount of money to deposit to the Customer account.
     * It updates the customer's capital amount
     * @param sum amount of money to deposit
     * @param yaz current date
     */

    public void depositMoney(int sum, int yaz);

    /**
     * This method gets amount of money to withdraw from the Customer account.
     * It updates the customer's capital amount
     * @param sum amount of money to deposit
     * @param yaz current date
     */

    public void moneyWithdrawal(double sum, int yaz);
}
