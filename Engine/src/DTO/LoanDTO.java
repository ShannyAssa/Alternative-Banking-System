package DTO;

import Account.impl.Loan;
import Account.impl.Loan.Lender;
import Account.impl.LoanAction;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LoanDTO {

    public class LenderDTO{
        public LenderDTO(Loan.Lender curr) {
            this.lenderName = curr.getLenderName();
            this.contribution = curr.getContribution();
        }

        private String lenderName;
        private double contribution;

        public String getLenderName() {
            return lenderName;
        }
        public double getContribution() {
            return contribution;
        }
    }


    private String loanId;
    private String Owner;
    private List<LoanDTO.LenderDTO> participants = new LinkedList<>();
    private String category;
    private double principal;
    private double principalWithInterest;
    private double interestRate; // percentage
    private int loanTerm; // the total time in Yaz the borrower has to pay back
    private int paymentInterval; // pays every Yaz
    private int startingDate; // loan's starting Yaz
    private int nextRepayment; // next Yaz he has to pay
    private Loan.Status status;
    private List<LoanActionDTO> history = new LinkedList<>();
    private double PaidPrincipal;
    private double PaidInterest;
    private double UnpaidPrincipal;
    private double UnpaidInterest;
    private List<LoanActionDTO> unpaidPayments = new LinkedList<>();
    private double pendingPrincipal;
    private int terminationDate;
    private double collectedPrincipal;

    private final SimpleBooleanProperty isLoanCheckedInScrambleTable = new SimpleBooleanProperty();

    public LoanDTO(Loan loan) {
        this.loanId = loan.getLoanId();
        this.Owner = loan.getOwner().getName();
        this.category = loan.getCategory();
        this.principal = loan.getPrincipal();
        this.interestRate = loan.getInterestRate();
        this.principalWithInterest = loan.getPrincipalWithInterest();
        this.loanTerm = loan.getLoanTerm();
        this.paymentInterval = loan.getPaymentInterval();
        this.startingDate = loan.getStartingDate();
        this.nextRepayment = loan.getNextRepayment();
        this.status = loan.getStatus();
        this.pendingPrincipal = loan.getPendingPrincipal();
        PaidPrincipal = loan.getPaidPrincipal();
        PaidInterest = loan.getPaidInterest();
        UnpaidPrincipal = loan.getUnpaidPrincipal();
        UnpaidInterest = loan.getUnpaidInterest();
        this.pendingPrincipal = loan.getPendingPrincipal();
        this.terminationDate = loan.getTerminationDate();
        this.collectedPrincipal = this.principal - this.pendingPrincipal;

        for (Lender curr : loan.getParticipants()) {
            participants.add(new LenderDTO(curr));
        }

        for (LoanAction curr : loan.getHistory()) {
            history.add(new LoanActionDTO(curr));
        }

        for (LoanAction curr : loan.getUnpaidPayments()) {
            unpaidPayments.add(new LoanActionDTO(curr));
        }
    }

    public String getLoanId() {
        return loanId;
    }

    public String getOwner() {
        return Owner;
    }

    public List<LenderDTO> getParticipants() {
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

    public int getNextRepayment() { return nextRepayment; }

    public Loan.Status getStatus() {
        return status;
    }

    public List<LoanActionDTO> getHistory() {
        return history;
    }

    public double getPaidPrincipal() {
        return PaidPrincipal;
    }

    public double getPaidInterest() {
        return PaidInterest;
    }

    public double getUnpaidPrincipal() {
        return UnpaidPrincipal;
    }

    public double getUnpaidInterest() {
        return UnpaidInterest;
    }

    public List<LoanActionDTO> getUnpaidPayments() {
        return unpaidPayments;
    }

    public int getTerminationDate() { return terminationDate; }

    public double getPendingPrincipal() {
        return pendingPrincipal;
    }

    public double getCollectedPrincipal() {
        return collectedPrincipal;
    }

    public List<String> participantsToString(){
        List<String> participantsList = new ArrayList<>();
        for (LenderDTO curr : this.participants) {
            participantsList.add("Name: " + curr.getLenderName() + "\n"
            + "Contribution: " + curr.getContribution());
        }
        return participantsList;
    }

    public List<String> paymentsHistoryToString(){
        List<String> paymentsHistoryList = new ArrayList<>();
        for (LoanActionDTO curr : this.history) {
            paymentsHistoryList.add(curr.toString());
        }
        return paymentsHistoryList;
    }

    public List<String> unpaidPaymentsToString(){
        List<String> unpaidPaymentsList = new ArrayList<>();
        for (LoanActionDTO curr : this.unpaidPayments) {
            unpaidPaymentsList.add(curr.toString());
        }
        return unpaidPaymentsList;
    }

    public BooleanProperty isLoanCheckedInScrambleTableProperty() { return isLoanCheckedInScrambleTable; }

    public double getSinglePrincipal(){
        return principal / (loanTerm / paymentInterval);
    }

    public double getSingleInterest(){
        return ((interestRate / 100) * principal) / (loanTerm / paymentInterval);
    }

    public double getSinglePayment(){
        return getSinglePrincipal() + getSingleInterest();
    }



}
