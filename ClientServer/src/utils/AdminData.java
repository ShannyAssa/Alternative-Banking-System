package utils;

import DTO.CustomerDTO;
import DTO.LoanDTO;

import java.util.Map;

public class AdminData {

    final private Map<String, LoanDTO> loans;
    final private Map<String, CustomerDTO> customers;


    public AdminData(Map<String, LoanDTO> loans, Map<String, CustomerDTO> customers) {
        this.loans = loans;
        this.customers=customers;
    }
}
