package Account.impl;

import Customer.impl.Customer;
import Account.LoanInterface;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Loan implements LoanInterface, Serializable {

    public enum Status{
        NEW, PENDING, ACTIVE, RISK, FINISHED
    }

    public class Lender implements Serializable{
        private Customer lender;
        private double contribution;

        public Lender(Customer lender, double contribution) {
            this.lender = lender;
            this.contribution = contribution;
        }
        public double getContribution() {
            return contribution;
        }

        public String getLenderName() {
            return lender.getName();
        }

        public Customer getLender() { return lender; }

        public void setLenderName(Customer buyer) {
            this.lender = buyer;
        }
    }

    private final String loanId;
    private final Customer owner;
    private List<Lender> participants = new LinkedList<>();
    private final String category;
    private final double principal;
    private final double principalWithInterest;
    private final double interestRate; // percentage
    private final int loanTerm; // the total time in Yaz the borrower has to pay back
    private final int paymentInterval; // pays every Yaz
    private int startingDate; // loan's starting Yaz
    private int nextRepayment; // next Yaz he has to pay
    private Status status;
    private List<LoanAction> history = new LinkedList<>();
    private double paidPrincipal;
    private double paidInterest;
    private double unpaidPrincipal;
    private double unpaidInterest;
    private List<LoanAction> unpaidPayments = new LinkedList<>();
    private double pendingPrincipal;
    private int terminationDate;


    public Loan(String loanId, Customer owner, String category, double principal,
                 double interestRate, int loanTerm, int paymentInterval, int startingDate) {
        this.loanId = loanId;
        this.owner = owner;
        this.category = category;
        this.principal = principal;
        this.interestRate = interestRate;
        this.principalWithInterest = principal + ((interestRate / 100) * principal);
        this.loanTerm = loanTerm;
        this.paymentInterval = paymentInterval;
        this.startingDate = startingDate;
        this.nextRepayment = startingDate + paymentInterval;
        this.status = Status.NEW;
        paidPrincipal = 0;
        paidInterest = 0;
        unpaidPrincipal = principal;
        unpaidInterest = principalWithInterest - principal;
        this.pendingPrincipal = principal;
    }

    public String getLoanId() {
        return loanId;
    }

    public Customer getOwner() {
        return owner;
    }

    public List<Lender> getParticipants() {
        return participants;
    }

    public String getCategory() {
        return category;
    }

    public double getPrincipal() {
        return principal;
    }

    public double getPrincipalWithInterest() {
        return principalWithInterest;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public int getPaymentInterval() {
        return paymentInterval;
    }

    public int getStartingDate() {
        return startingDate;
    }

    public int getNextRepayment() {
        return nextRepayment;
    }

    public Status getStatus() {
        return status;
    }

    public List<LoanAction> getHistory() {
        return history;
    }

    public int getTerminationDate() {return terminationDate; }

    public double getPaidPrincipal() {
        return paidPrincipal;
    }

    public double getPaidInterest() {
        return (paidInterest);
    }

    public double getUnpaidPrincipal() {
        return unpaidPrincipal;
    }

    public double getUnpaidInterest() {return (unpaidInterest); }

    public List<LoanAction> getUnpaidPayments() {
        return unpaidPayments;
    }

    public double getPendingPrincipal() {
        return pendingPrincipal;
    }

    public double getSinglePrincipal(){
        return principal / (loanTerm / paymentInterval);
    }

    public double getSingleInterest(){
        return ((interestRate / 100) * principal) / (loanTerm / paymentInterval);
    }

    public double getSinglePayment(){
        return getSinglePrincipal() + getSingleInterest();
    }

    /**
     * This method gets as a parameter:
     * @param contribution which represents a lender's total contribution to this loans, and
     * @return the amount of money the borrower has to repay him back each payment.
     * note: both principal and interest
     * */
    public double getSingleRepayment(double contribution){
        return (contribution * ((interestRate / 100) + 1)) / (loanTerm / paymentInterval);
    }

    public void setRiskStatus(int yaz)
    {
        this.status = Status.RISK;
        if(unpaidPayments.size() != (loanTerm / paymentInterval)){
            unpaidPayments.add(new LoanAction(yaz, getSinglePrincipal(), getSingleInterest(), getSinglePayment()));
        }

    }

    @Override
    public void assignLenderAndMoney(Customer lender, double amount, int yaz) {
        pendingPrincipal -= amount;

        if(this.participants.contains(lender)){ //if lender already exists
            for (Lender curr : this.participants) {
                if(curr.getLender().getName() == lender.getName()){
                    curr.contribution += amount;
                    break;
                }
            }
        }
        else{
            participants.add(new Lender(lender, amount));
        }


        if(pendingPrincipal<principal)
            this.status=Status.PENDING;
        if(pendingPrincipal==0) {
            this.status = Status.ACTIVE;
            this.startingDate = yaz;
            this.nextRepayment = yaz + this.paymentInterval;
            this.owner.depositMoneyDouble(principal, yaz);
        }

    }

    @Override
    public void completePayment(int yaz, Customer borrower){
        if(this.status == Status.RISK){
            completeRiskPayment(yaz, borrower);
        }
        else{
            completeSinglePayment(yaz, borrower);
        }

        if(unpaidPrincipal == 0 && unpaidInterest == 0){
            this.status = Status.FINISHED;
            this.terminationDate = yaz;

        }
    }

    public void completeSinglePayment(int yaz, Customer borrower){
        borrower.moneyWithdrawal(getSinglePayment(), yaz);
        history.add(new LoanAction(yaz, getSinglePrincipal(), getSingleInterest(), getSinglePayment()));
        repayParticipants(yaz, 1);

        this.paidPrincipal += getSinglePrincipal();
        this.paidInterest += getSingleInterest();

        this.unpaidPrincipal -= getSinglePrincipal();
        this.unpaidInterest -= getSingleInterest();

        if(unpaidPrincipal == 0 && unpaidInterest == 0){
            this.status = Status.FINISHED;
            this.terminationDate = yaz;
        }

        if(this.unpaidPayments.isEmpty()){
            this.status = Status.ACTIVE;
        }
    }

    public void completeRiskPayment(int yaz, Customer borrower){
        for (int i = 0; i < unpaidPayments.size(); i++) {
            completeSinglePayment(yaz, borrower);
        }
        if(unpaidPayments.size() != (loanTerm / paymentInterval)){
            completeSinglePayment(yaz, borrower);
        }
        this.status = Status.ACTIVE;
        unpaidPayments.clear();
    }

    public void repayParticipants(int yaz, double numberOfPayments){
        for (Lender curr : this.participants) {
            curr.getLender().depositMoneyDouble(getSingleRepayment(curr.getContribution() * numberOfPayments), yaz);
        }
    }

    public void payFullAmount(Customer borrower, int yaz){
        this.unpaidPayments.clear();
        payByAmount(yaz, borrower, unpaidPrincipal + unpaidInterest, (unpaidPrincipal + unpaidInterest) / getSinglePayment());
    }

    public void updateNextRepayment() {
        this.nextRepayment += this.paymentInterval;
    }


    public void payByAmount(int yaz, Customer borrower, double amount, double numberOfPayments){
        double interestFromAmount, principalFromAmount;

        if(amount == this.unpaidInterest + this.unpaidPrincipal){
           interestFromAmount = this.unpaidInterest;
           principalFromAmount = this.unpaidPrincipal;
        }
        else{
            interestFromAmount = (this.interestRate / 100) *  amount;
            principalFromAmount = amount - interestFromAmount;
        }


        borrower.moneyWithdrawal(amount, yaz);
        history.add(new LoanAction(yaz, principalFromAmount, interestFromAmount, amount));
        repayParticipants(yaz, numberOfPayments);

        this.paidPrincipal += principalFromAmount;
        this.paidInterest += interestFromAmount;

        this.unpaidPrincipal -= principalFromAmount;
        this.unpaidInterest -= interestFromAmount;

        if(unpaidPrincipal == 0 && unpaidInterest == 0){
            this.status = Status.FINISHED;
            this.terminationDate = yaz;
        }
        else if(this.unpaidPayments.isEmpty()){
            this.status = Status.ACTIVE;
        }

    }

    public double getUnpaidPaymentsAmount(){
        double sum = 0;
        for (LoanAction curr : this.unpaidPayments) {
            sum += curr.getTotalPayment();
        }
        return sum;
    }

    public double getPriceForLenderCont(String lenderName){
        double lenderCont = 0;
        double sum = 0, ownershipPercentage;
        for (LoanAction curr : this.history){
            sum += curr.getPrincipal();
        }

        for (Lender curr : this.participants){
            if(curr.getLender().getName() == lenderName){
                lenderCont = curr.getContribution();
                break;
            }
        }

        ownershipPercentage = lenderCont / this.principal;

        return (lenderCont - (ownershipPercentage * sum));
    }
}



