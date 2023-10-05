package DTO;

import Account.impl.Loan;
import Customer.impl.Customer;
import Customer.Message;
import java.util.LinkedList;
import java.util.List;

public class CustomerDTO {
    private String name;
    private int customerNumber;
    private AccountDTO account;
    private List<LoanDTO> lenderList= new LinkedList<>(); //malve
    private List<LoanDTO> borrowerList= new LinkedList<>(); //love
    private List<String> messages = new LinkedList<>();

    public CustomerDTO(Customer curr) {
        this.name = curr.getName();
        this.account = new AccountDTO(curr.getAccount());

        for (Loan currLoan : curr.getLenderList()) {
            lenderList.add(new LoanDTO(currLoan));
        }
        for (Loan currLoan : curr.getBorrowerList()) {
            borrowerList.add(new LoanDTO(currLoan));
        }
        for (Message currMessage : curr.getMessages()) {
            messages.add(currMessage.toString());
        }

    }

    public String getName() {
        return name;
    }

    public int getCustomerNumber() {
        return customerNumber;
    }

    public AccountDTO getAccount() {
        return account;
    }

    public List<LoanDTO> getLenderList() {
        return lenderList;
    }

    public List<LoanDTO> getBorrowerList() {
        return borrowerList;
    }

    @Override
    public String toString() {
        String print = "name='" + name + '\'' +
                ", customerNumber=" + customerNumber +
                ", account=" + account.toString() ;
        print += "Lender list: ";
        for (LoanDTO loanDTO : lenderList) {
            print += loanDTO.toString();
        }

        print += "Borrower list: ";
        for (LoanDTO loanDTO: borrowerList) {
            print += loanDTO.toString();
        }

        return print;
    }

    public double getCapital() {
        return account.getCapital();
    }

    public int getLenderListSize() {
        return lenderList.size();
    }

    public int getBorrowerListSize() {
        return borrowerList.size();
    }

    public List<String> getMessages() {
        return messages;
    }
}
