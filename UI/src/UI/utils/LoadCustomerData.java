package UI.utils;

import DTO.LoanDTO;

import java.util.List;
import java.util.Map;

public class LoadCustomerData {
     private Map<String, LoanDTO> loansAsBorrower;
     private Map<String, LoanDTO> loansAsLender;
     private List<String> accountStatement;
     private List<String> messages;

    public LoadCustomerData(Map<String, LoanDTO> loansAsBorrower, Map<String, LoanDTO> loansAsLender, List<String> accountStatement, List<String> messages) {
        this.loansAsBorrower = loansAsBorrower;
        this.loansAsLender = loansAsLender;
        this.accountStatement = accountStatement;
        this.messages= messages;

    }


    public Map<String, LoanDTO> getLoansAsBorrower() {
        return loansAsBorrower;
    }

    public void setLoansAsBorrower(Map<String, LoanDTO> loansAsBorrower) {
        this.loansAsBorrower.clear();
        for (String currKey : loansAsBorrower.keySet()){
            this.loansAsBorrower.put(currKey, loansAsBorrower.get(currKey));
        }
//        this.loansAsBorrower = loansAsBorrower;
    }

    public Map<String, LoanDTO> getLoansAsLender() {
        return loansAsLender;
    }

    public void setLoansAsLender(Map<String, LoanDTO> loansAsLender) {
        this.loansAsLender.clear();
        for (String currKey : loansAsLender.keySet()){
            this.loansAsLender.put(currKey, loansAsLender.get(currKey));
        }
    }

    public List<String> getAccountStatement() {
        return accountStatement;
    }

    public void setAccountStatement(List<String> accountStatement) {
        this.accountStatement = accountStatement;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "LoadCustomerData{" +
                "loansAsBorrower=" + loansAsBorrower +
                ", loansAsLender=" + loansAsLender +
                ", accountStatement=" + accountStatement +
                ", messages=" + messages +
                '}';
    }
}
