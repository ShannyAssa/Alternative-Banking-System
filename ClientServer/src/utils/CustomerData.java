package utils;

import DTO.LoanDTO;

import java.util.List;
import java.util.Map;

public class CustomerData {
    final private Map<String, LoanDTO> loansAsBorrower;
    final private Map<String, LoanDTO> loansAsLender;
    final private List<String> accountStatement;

    public Map<String, LoanDTO> getLoansAsBorrower() {
        return loansAsBorrower;
    }

    public Map<String, LoanDTO> getLoansAsLender() {
        return loansAsLender;
    }

    public List<String> getAccountStatement() {
        return accountStatement;
    }

    public List<String> getMessages() {
        return messages;
    }

    final private List<String> messages;


    public CustomerData(Map<String, LoanDTO> loansAsBorrower, Map<String, LoanDTO> loansAsLender, List<String> accountStatement,
                        List<String> messages) {
        this.loansAsBorrower = loansAsBorrower;
        this.loansAsLender = loansAsLender;
        this.accountStatement = accountStatement;
        this.messages = messages;
    }
}
