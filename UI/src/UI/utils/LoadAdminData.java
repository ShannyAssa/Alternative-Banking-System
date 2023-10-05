package UI.utils;

import DTO.CustomerDTO;
import DTO.LoanDTO;

import java.util.Map;

public class LoadAdminData {

    private Map<String, LoanDTO> loans;
    private Map<String, CustomerDTO> customers;

    public LoadAdminData(Map<String, LoanDTO> loans, Map<String, CustomerDTO> customers) {
        this.loans = loans;
        this.customers=customers;
    }

    public Map<String, LoanDTO> getLoans() {
        return loans;
    }

    public Map<String, CustomerDTO> getCustomers() {
        return customers;
    }

    public void setLoans(Map<String, LoanDTO> loans) {
        this.loans = loans;
    }

    public void setCustomers(Map<String, CustomerDTO> customers) {
        this.customers = customers;
    }
}
