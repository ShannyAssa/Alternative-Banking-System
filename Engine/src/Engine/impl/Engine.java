package Engine.impl;

import Account.impl.Loan;
import Account.impl.LoanAction;
import Customer.Message;
import Customer.impl.Customer;
import DTO.*;
import Engine.EngineInterface;
import Exceptions.IntervalsNotMatchException;
import Exceptions.LoanAlreadyExists;
import Exceptions.NoCatagoryException;
import xml.AbsDescriptor;
import xml.AbsLoan;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;


public class Engine implements EngineInterface {
    final int STARTING_YAZ = 1;
    final int STARTING_CUSTOMER_COUNTER = 1;

    public enum mode{REGULAR, REWIND}


    private mode systemMode = mode.REGULAR;

    private static int customerCounter = 1;
    private static int yaz = 1;

    private boolean adminExistence= false;

    private static boolean isAlreadyLoaded = false;
    private Map<String, Customer> customers = new HashMap();
    private Set<String> categories = new HashSet<>();
    private Map<String, Loan> loans = new HashMap();
    private Set<String> onlineCustomers = new HashSet<>();

    private Map<String, Customer> customersTemp = new HashMap();
    private Set<String> categoriesTemp = new HashSet<>();
    private Map<String, Loan> loansTemp = new HashMap();

    private Map<String, List<Loan>> loansToSell = new HashMap<>();
    private List<EngineDTO> previousEngines = new ArrayList<>();

    private final static String JAXB_XML_GAME_PACKAGE_NAME = "xml";

    private int engineToViewYaz;

    public void setCustomerCounter(int customerCounter) {
        Engine.customerCounter = customerCounter;
    }

    public void setYaz(int yaz) {
        this.yaz = yaz;
    }

    public boolean isAdminExist() {
        return adminExistence;
    }

    public void setAdminExistence(boolean newExistence) {
        this.adminExistence= newExistence;
    }

    @Override
    public void yazPromoting() {
        this.previousEngines.add(new EngineDTO(customers, categories, loans, loansToSell));
        ++Engine.yaz;
        for (Loan curr : loans.values()) {
            if (curr.getStatus().equals(Loan.Status.ACTIVE) || curr.getStatus().equals(Loan.Status.RISK)) {
                if(curr.getPaymentInterval() == 1){
                    curr.getOwner().addMessage(Message.MessageType.PAYMENT_ALERT, curr.getLoanId(), yaz, curr.getSinglePayment(),
                            curr.getUnpaidPaymentsAmount());
                    if(!curr.getUnpaidPayments().isEmpty() || (!curr.getHistory().isEmpty() && curr.getHistory().get(curr.getHistory().size() - 1).getYazDate() != yaz - 1)){
                        curr.setRiskStatus(yaz);
                        removeFromLoansToSell(curr.getLoanId());
                        double riskAmount = curr.getUnpaidPaymentsAmount();
                        curr.getOwner().addMessage(Message.MessageType.RISK_ALERT, curr.getLoanId(), yaz, riskAmount,
                                riskAmount);
                    }
                    curr.updateNextRepayment();
                }
                else if (curr.getNextRepayment() == yaz) {
                    curr.getOwner().addMessage(Message.MessageType.PAYMENT_ALERT, curr.getLoanId(), yaz, curr.getSinglePayment(),
                            curr.getUnpaidPaymentsAmount());
                } else if (curr.getNextRepayment() + 1 == yaz) {
                    int lastPaymentYaz = 0;
                    if(!curr.getHistory().isEmpty()){
                        lastPaymentYaz = curr.getHistory().get(curr.getHistory().size() - 1).getYazDate();
                    }
                    if(!curr.getUnpaidPayments().isEmpty() || lastPaymentYaz != yaz - 1){
                        curr.setRiskStatus(yaz);
                        removeFromLoansToSell(curr.getLoanId());
                        double riskAmount = curr.getUnpaidPaymentsAmount();
                        curr.getOwner().addMessage(Message.MessageType.RISK_ALERT, curr.getLoanId(), yaz, riskAmount,
                                riskAmount);
                    }
                    curr.updateNextRepayment();
                }
            }

        }

    }

    @Override
    public boolean loadData(String pathToXml, String customerName) {
        try {
            setCustomerCounter(STARTING_CUSTOMER_COUNTER);
            InputStream inputStream = new FileInputStream(new File(pathToXml));
            AbsDescriptor descriptors = deserializeFrom(inputStream);
            extractCategories(descriptors.getAbsCategories().getAbsCategory());
            extractLoan(descriptors.getAbsLoans().getAbsLoan(), customerName);
            insertToOriginalStructures();

        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    private void insertToOriginalStructures() {
        this.loans.putAll(loansTemp);
        this.categories.addAll(categoriesTemp);
        clearDataTemp();
//        if (!this.isAlreadyLoaded) {
//            this.isAlreadyLoaded = true;
//        }

    }

    private void clearDataTemp() {
        this.customersTemp.clear();
        this.categoriesTemp.clear();
        this.loansTemp.clear();
    }

    public void extractLoan(List<AbsLoan> loansFromXml, String customerName) {
        for (AbsLoan curr : loansFromXml) {
            if (!categoriesTemp.contains(curr.getAbsCategory())) {
                clearDataTemp();
                throw new NoCatagoryException(curr.getAbsCategory(), categoriesTemp);
            }
            if (curr.getAbsTotalYazTime() % curr.getAbsPaysEveryYaz() != 0) {
                clearDataTemp();
                throw new IntervalsNotMatchException();
            }
            Loan newLoan = new Loan(curr.getId(), customers.get(customerName), curr.getAbsCategory(), curr.getAbsCapital(),
                    curr.getAbsIntristPerPayment(), curr.getAbsTotalYazTime(), curr.getAbsPaysEveryYaz(), STARTING_YAZ);
            if(loans.containsKey(curr.getId())){
                throw new LoanAlreadyExists(curr.getId());
            }
            loansTemp.put(curr.getId(), newLoan);
            customers.get(customerName).addNewLoanToBorrowerList(newLoan);
        }
    }

    public void extractCategories(List<String> categoriesFromXml) {
        categoriesTemp.addAll(categoriesFromXml);
    }


    private static AbsDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (AbsDescriptor) u.unmarshal(in);
    }

    public Map<String, LoanDTO> loansInfo() {
        Map<String, LoanDTO> loansDTO = new HashMap();
        for (Loan curr : loans.values()) {
            loansDTO.put(curr.getLoanId(), new LoanDTO(curr));
        }
        return loansDTO;
    }

    public Map<String, LoanDTO> loansAsLender(String name) {
        List<Loan> loansAsLender = customers.get(name).getLenderList();
        Map<String, LoanDTO> res = new HashMap<>();

        for (Loan currLoan : loansAsLender) {
            res.put(currLoan.getLoanId(), new LoanDTO(currLoan));
        }

        return res;
    }

    public Map<String, LoanDTO> loansAsBorrower(String name) {
        List<Loan> loansAsBorrower = customers.get(name).getBorrowerList();
        Map<String, LoanDTO> res = new HashMap<>();

        for (Loan currLoan : loansAsBorrower) {
            res.put(currLoan.getLoanId(), new LoanDTO(currLoan));
        }

        return res;
    }

    public Map<String, CustomerDTO> customersInfo() {
        Map<String, CustomerDTO> customersDTO = new HashMap();
        for (Customer curr : customers.values()) {
            customersDTO.put(curr.getName(), new CustomerDTO(curr));
        }
        return customersDTO;
    }

    @Override
    public void depositMoney(String name, int sum) {
        customers.get(name).depositMoney(sum, yaz);
    }

    @Override
    public String moneyWithdrawal(String customerName, double sum) {
        Customer curr = customers.get(customerName);
        double capital = curr.getAccount().getCapital();
        if (sum > capital) {
            String message = "Money withdrawal was unsuccessful.\nThe maximum amount of money %s can withdraw is: %s";
            throw new NumberFormatException();
        } else {
            curr.moneyWithdrawal(sum, yaz);
            return "Money withdrawal was successful.";
        }
    }

    public Set<String> getCategories() {
        return categories;
    }

    public List<String> getCustomersNames() {
        List<String> customersNameList = new ArrayList<>();
        for (Customer curr : customers.values()) {
            customersNameList.add(curr.getName());
        }
        return customersNameList;
    }

    public List<String> getCategoriesList() {
        List<String> categoriesList = new ArrayList<>();
        for (String curr : categories) {
            categoriesList.add(curr);
        }

        return categoriesList;
    }

    public List<CategoryDTO> getCategoriesDTO() {
        List<CategoryDTO> categoriesList = new ArrayList<>();
        for (String curr : categories) {
            categoriesList.add(new CategoryDTO(curr));
        }

        return categoriesList;
    }

    /**
     * This method gets the following parameters:
     *
     * @param lender           the customer who invests money.
     * @param investmentAmount the amount of money that is being divided to all:
     * @param chosenLoans      the loans which the lender chose to invest in.
     *                         the method checks in what way the system should split the money and calls
     *                         the matching methods to do so.
     */
    public void loansAssignment(CustomerNameListDTO lender, List<LoanDTO> chosenLoans, double investmentAmount, int maxOwnership) {
        double totalPendingPrincipals = 0, pendingPrincipal, totalPrincipal;
        int maxPercentage = maxOwnership / 100;
        Customer theLender = customers.get(lender.getName());

        for (LoanDTO curr : chosenLoans) {
            totalPrincipal = loans.get(curr.getLoanId()).getPrincipal();
            pendingPrincipal = loans.get(curr.getLoanId()).getPendingPrincipal();
            if (pendingPrincipal <= totalPrincipal * maxPercentage) {
                totalPendingPrincipals += pendingPrincipal;
            } else {
                totalPendingPrincipals += totalPrincipal * maxPercentage;
            }
        }

        if (investmentAmount >= totalPendingPrincipals) {
            assignLoansFullAmount(theLender, chosenLoans, maxPercentage);
        } else {
            assignLoansByCalculation(theLender, chosenLoans, investmentAmount, maxPercentage);
        }
    }

    /**
     * In case where the investment amount the lender gives is greater than the customers'
     * pending money (or equals to), the system assigns the full amount of pending money to all loans.
     *
     * @param theLender   the customer who invests money.
     * @param chosenLoans the loans which the lender chose to invest in.
     */
    private void assignLoansFullAmount(Customer theLender, List<LoanDTO> chosenLoans, int maxPercentage) {
        Loan loanToUpdate;
        double totalPrincipal;
        double pendingPrincipal;
        double amountToPay;
        for (LoanDTO curr : chosenLoans) {
            totalPrincipal = loans.get(curr.getLoanId()).getPrincipal();
            pendingPrincipal = loans.get(curr.getLoanId()).getPendingPrincipal();

            if (pendingPrincipal <= totalPrincipal * maxPercentage) {
                amountToPay = pendingPrincipal;
            } else {
                amountToPay = totalPrincipal * maxPercentage;
            }
            loanToUpdate = loans.get(curr.getLoanId());
            loanToUpdate.assignLenderAndMoney(theLender, amountToPay, yaz);
            theLender.addNewLoanToLenderList(loanToUpdate, amountToPay, yaz);
        }

    }

    /**
     * This method calculates the money divided to chosen loans
     *
     * @param theLender        the customer who invests money.
     * @param investmentAmount the amount of money that is being divided to all:
     * @param chosenLoans      the loans which the lender chose to invest in.
     *                         this method calls "finalLoansAssignment" method which actually performs the assignment.
     */
    private void assignLoansByCalculation(Customer theLender, List<LoanDTO> chosenLoans, double investmentAmount, int maxPercentage) {

        double leftToAssign = investmentAmount, pendingPrincipal, minimum, totalPrincipal, amountToPay;
        int loansAmount = chosenLoans.size();

        Map<String, Double> principalAssigning = new HashMap<>();

        for (LoanDTO curr : chosenLoans) {
            totalPrincipal = loans.get(curr.getLoanId()).getPrincipal();
            pendingPrincipal = loans.get(curr.getLoanId()).getPendingPrincipal();
            if (pendingPrincipal <= totalPrincipal * maxPercentage) {
                amountToPay = pendingPrincipal;
            } else {
                amountToPay = totalPrincipal * maxPercentage;
            }
            principalAssigning.put(curr.getLoanId(), amountToPay);
        }

        while (leftToAssign > 0) {
            minimum = getMinimumLoanAmount(principalAssigning);
            if (leftToAssign >= minimum * loansAmount) {
                leftToAssign -= minimum * loansAmount;
                for (String curr : principalAssigning.keySet()) {
                    principalAssigning.replace(curr, (principalAssigning.get(curr) - minimum)); // updating the map
                }
            } else {
                for (String curr : principalAssigning.keySet()) {
                    if (leftToAssign >= minimum) {
                        if (principalAssigning.get(curr) == minimum) {
                            principalAssigning.replace(curr, (principalAssigning.get(curr) - minimum));
                            leftToAssign -= minimum;
                            break;
                        }
                    } else {
                        if (principalAssigning.get(curr) > 0) {
                            principalAssigning.replace(curr, (principalAssigning.get(curr) - leftToAssign));
                            leftToAssign -= leftToAssign;
                            break;
                        }
                    }
                }
            }
            finalLoansAssignment(principalAssigning, theLender);
        }

    }

    private void finalLoansAssignment(Map<String, Double> principalAssigning, Customer theLender) {

        double pendingPrincipal, finalAssigning;
        Loan loanToUpdate;
        for (String curr : principalAssigning.keySet()) {
            loanToUpdate = loans.get(curr);
            pendingPrincipal = loanToUpdate.getPendingPrincipal();
            finalAssigning = pendingPrincipal - principalAssigning.get(curr);
            if (finalAssigning > 0) {
                loanToUpdate.assignLenderAndMoney(theLender, finalAssigning, yaz);
                theLender.addNewLoanToLenderList(loanToUpdate, finalAssigning, yaz);
            }

        }
    }

    /**
     * This method returns the minimum amount of money of
     *
     * @param principalAssigning map
     */
    private double getMinimumLoanAmount(Map<String, Double> principalAssigning) {
        return principalAssigning.values().stream().filter(value -> value > 0).min(Double::compare).get();
    }

    public static int getYaz() {
        return yaz;
    }

    public int getOpenLoansNumber(String ownerId) {
        return (int) this.customers.get(ownerId).getBorrowerList().stream().filter(
                loan -> !loan.getStatus().equals(Loan.Status.FINISHED)).count();
    }

    public List<String> getCustomerAccountStatement(String name) {
        Customer curr = this.customers.get(name);
        AccountDTO accountDTO = new AccountDTO(curr.getAccount());
        return accountDTO.historyToString();
    }

    public CustomerDTO getCustomerDTO(String name) {
        return (new CustomerDTO(customers.get(name)));
    }

    public boolean paySingleLoan(String ownerName, String loanId) {
        Loan loanToPay = null;
        Customer borrower = customers.get(ownerName);

        for (Loan curr : customers.get(ownerName).getBorrowerList()) {
            if (curr.getLoanId().equals(loanId)) {
                loanToPay = curr;
                break;
            }
        }


        if (loanToPay.getStatus() == Loan.Status.ACTIVE) {
            if (borrower.getAccount().getCapital() >= loanToPay.getSinglePayment()) {
                loanToPay.completeSinglePayment(yaz, borrower);
                return true;
            }
        }
        return false;

    }

    public boolean payFullLoan(String ownerName, String loanId) {
        Customer borrower = customers.get(ownerName);
        Loan loanToPay = null;

        for (Loan curr : customers.get(ownerName).getBorrowerList()) {
            if (curr.getLoanId().equals(loanId)) {
                loanToPay = curr;
                break;
            }
        }

        if (borrower.getAccount().getCapital() >= (loanToPay.getUnpaidPrincipal() + loanToPay.getUnpaidInterest())) {
            loanToPay.payFullAmount(borrower, this.yaz);
            return true;
        }
        else {
            return false;
        }

    }

    public void payRisk(String ownerName, String loanId, double amountToPay){
        Loan loanToPay = loans.get(loanId);
        Customer borrower = customers.get(ownerName);
        double loanTotalUnpaidPayments = loanToPay.getUnpaidPaymentsAmount();
        if(amountToPay >= loanTotalUnpaidPayments){
            double remainder = amountToPay - loanTotalUnpaidPayments;

            loanToPay.getUnpaidPayments().clear();

            loanToPay.payByAmount(this.yaz, borrower, loanTotalUnpaidPayments, (amountToPay / loanToPay.getSinglePayment()));

            if(remainder == loanToPay.getSinglePayment()){
                loanToPay.completeSinglePayment(this.yaz, borrower);

            }
            else if(remainder > 0){
                loanToPay.payByAmount(this.yaz, borrower, remainder, remainder / loanToPay.getSinglePayment());
                double interestPercentage = (loanToPay.getInterestRate() / 100);
                double principalToPay = remainder - (remainder * interestPercentage);
                loanToPay.getUnpaidPayments().add(new LoanAction(this.yaz, principalToPay, remainder - principalToPay, remainder));
                loanToPay.setRiskStatus(this.yaz);
                removeFromLoansToSell(loanToPay.getLoanId());
            }

        }

        else{
            loanToPay.payByAmount(this.yaz, borrower, loanTotalUnpaidPayments, (amountToPay / loanToPay.getSinglePayment()));
            double payment = amountToPay;
            for (LoanAction curr : loanToPay.getUnpaidPayments()) {
                if (payment != 0) {
                    if(curr.getTotalPayment() <= payment){
                        payment -= curr.getTotalPayment();
                        loanToPay.getUnpaidPayments().remove(curr);
                    }
                    else{
                        double interestPercentage = (loanToPay.getInterestRate() / 100);
                        double principalToPay = payment - (payment * interestPercentage);
                        curr.updateLoanAction(principalToPay, payment - principalToPay);
                    }
                }
                if(payment == 0){
                    break;
                }
            }
        }

    }

    public void sellLoan(String customerName, String loanId){
        Loan loanToSell = loans.get(loanId);

        if(!loansToSell.containsKey(customerName)){
            loansToSell.put(customerName, new LinkedList<>());
            loansToSell.get(customerName).add(loanToSell);
        }
        else{
            loansToSell.get(customerName).add(loanToSell);
        }

    }

    public boolean isPurposedForSale(String customerName, String loanId) {
        if(loansToSell.containsKey(customerName)){
            for (Loan curr : loansToSell.get(customerName)){
                if(curr.getLoanId() == loanId){
                    return true;
                }
            }
        }
        return false;
    }

    public void removeFromLoansToSell(String loanId){
        for (String customerName : loansToSell.keySet()){
            List<Loan> currLoansToSell = loansToSell.get(customerName);
            currLoansToSell.removeIf(loan -> loan.getLoanId() == loanId);
        }
    }

    public List<LoanForSaleDTO> getLoansForSaleDTOList(String lenderName){
        List<LoanForSaleDTO> loansForSale = new ArrayList<>();
        for (String curr : loansToSell.keySet()){
            List<Loan> currLoansToSell = loansToSell.get(curr);
            currLoansToSell.removeIf(loan -> loan.getStatus().equals(Loan.Status.FINISHED));
        }
            for (String curr : loansToSell.keySet()){
            if(curr != lenderName){
                for (Loan currLoan : loansToSell.get(curr)){
                    if(lenderName != currLoan.getOwner().getName()){
                        loansForSale.add(new LoanForSaleDTO(new LoanDTO(currLoan),
                                curr, currLoan.getPriceForLenderCont(curr)));
                    }
                }
            }
        }


        return loansForSale;
    }

    public void updateParticipantForLoan(String loanId, String sellerName, String buyerName, double price){
        Loan loan = loans.get(loanId);
        Customer seller = customers.get(sellerName);
        Customer buyer = customers.get(buyerName);
        for (Loan.Lender curr : loan.getParticipants()){
            if(curr.getLenderName().equals(sellerName)){
                curr.setLenderName(buyer);
                this.loansToSell.get(sellerName).remove(loan);
                break;
            }
        }

        seller.getLenderList().remove(loan);
        buyer.getLenderList().add(loan);

        seller.depositMoneyDouble(price, yaz);
        buyer.moneyWithdrawal(price, yaz);
    }

    public void createNewLoan(String customerName, int principal, int loanTerm, int loanInterval, String loanId, String category, int interest){
        Loan newLoan = new Loan(loanId, customers.get(customerName), category, principal,
                interest, loanTerm, loanInterval, this.yaz);
        loans.put(loanId, newLoan);
        customers.get(customerName).addNewLoanToBorrowerList(newLoan);
    }

    public boolean isLoanExists(String loanId) {
        return loans.containsKey(loanId);
    }

    public List<EngineDTO> getPreviousEngines() {
        return previousEngines;
    }

    public synchronized void addUser(String username) {
        customers.put(username, new Customer(username, 0));
        onlineCustomers.add(username);
    }
    public boolean isUserExists(String username) {
        return onlineCustomers.contains(username);
    }

    public String getSystemMode() {
        if (this.systemMode.equals(mode.REGULAR))
            return "regular";
        else
            return "rewind";
    }

    public void setSystemMode(String systemMode) {
        if(systemMode.equals("regular")){
            this.systemMode = mode.REGULAR;
        }
        else{
            this.systemMode = mode.REWIND;
        }

    }

    public LoanDTO getLoanDTO(String id){
        return new LoanDTO(loans.get(id));
    }

    public EngineDTO getCurrEngineToView(){
        return new EngineDTO(customers, categories, loans, loansToSell);
    }

    public int getEngineToViewYaz() {
        return engineToViewYaz;
    }

    public void setEngineToViewYaz(int engineToViewYaz) {
        this.engineToViewYaz = engineToViewYaz;
    }
}