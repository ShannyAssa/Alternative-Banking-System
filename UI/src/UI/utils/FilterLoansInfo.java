package UI.utils;

import DTO.LoanDTO;

import java.util.Map;

public class FilterLoansInfo {

    private Map<String, LoanDTO> loans;
    private Map<String, Integer> openLoansNumber;

    public Map<String, LoanDTO> getLoans() {
        return loans;
    }
    public Map<String, Integer> getOpenLoansNumber() { return openLoansNumber; }

}
