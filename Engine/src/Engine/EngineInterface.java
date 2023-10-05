package Engine;

import java.util.List;

public interface EngineInterface {

    /**
     * This method gets a string which represents an xml file path and withdraws the data within the xml file
     * to the engine's objects.
     * @param pathToXml string which represents an xml file path.
     * @return returns true if the load succeeded, otherwise returns false.
     */
    public boolean loadData(String pathToXml, String customerName);

    /**
     * This method promotes the date and repay all the loans which their repayment date matches
     * the new updated date.
     */
    public void yazPromoting();

    /**
     * This method returns all the customers names mapped by their index number.
     * @return a map of customers indexes and their names.
     */
    public List<String> getCustomersNames();

    /**
     * This method gets amount of money to deposit to the customer account.
     * It updates the customer's capital amount
     * @param name customer name
     * @param sum amount of money to deposit
     */
    public void depositMoney(String name, int sum);

    /**
     * This method gets amount of money to withdraw to the customer account.
     * It updates the customer's capital amount
     * @param customerName customer name
     * @param sum amount of money to withdraw
     */
    public String moneyWithdrawal(String customerName, double sum);
}
