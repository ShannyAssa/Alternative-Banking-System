package Customer.impl;

import Account.impl.Account;
import Account.impl.Loan;
import Customer.CustomerInterface;
import Customer.Message;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class Customer implements CustomerInterface, Serializable { //we changed
    private final String name;
    private Account account;
    private List<Loan> lenderList= new LinkedList<>(); //malve
    private List<Loan> borrowerList= new LinkedList<>(); //love
    private List<Message> messages = new LinkedList<>();

    public Customer(String name, double capital) {
        this.name = name;
        this.account = new Account(capital);
    }

    public String getName() {
        return name;
    }


    public Account getAccount() {
        return account;
    }

    public List<Loan> getBorrowerList() {
        return borrowerList;
    }

    public List<Loan> getLenderList() {
        return lenderList;
    }

    @Override
    public void addNewLoanToLenderList(Loan newLoan, double contribution, int yaz){

        lenderList.add(newLoan);
        moneyWithdrawal(contribution, yaz);
    }

    @Override
    public void addNewLoanToBorrowerList(Loan newLoan){
        borrowerList.add(newLoan);
    }

    @Override
    public void depositMoney(int sum, int yaz){
        account.depositMoney(sum, yaz);
    }

    public void depositMoneyDouble(double sum, int yaz){
        account.depositMoneyDouble(sum, yaz);
    }

    @Override
    public void moneyWithdrawal(double sum, int yaz){
        account.moneyWithdrawal(sum, yaz);
    }

    public void addMessage(Message.MessageType type, String loadId, int yazDate, double totalPayment, double riskTotal){
        this.messages.add(new Message(type, loadId, yazDate, totalPayment, riskTotal));
    }

    public List<Message> getMessages() {
        return messages;
    }
}
