package DTO;

import Account.impl.Loan;
import Customer.impl.Customer;

import java.util.*;

public class EngineDTO {

    private Map<String, CustomerDTO> customers = new HashMap();
    private Set<String> categories = new HashSet<>();
    private Map<String, LoanDTO> loans = new HashMap();
    private Map<String, List<LoanDTO>> loansToSell = new HashMap<>();



    private int engineYazToView = 0;

    public EngineDTO(Map<String, Customer> oCustomers, Set<String> oCategories, Map<String, Loan> oLoans, Map<String, List<Loan>> oLoansToSell) {
        for (String curr : oCustomers.keySet()) {
            customers.put(curr, new CustomerDTO(oCustomers.get(curr)));
        }

        for (String curr : oCategories) {
            categories.add(curr);
        }

        for (String curr : oLoans.keySet()) {
            loans.put(curr, new LoanDTO(oLoans.get(curr)));
        }

        for (String currLoanToSell : oLoansToSell.keySet()) {

            List<LoanDTO> newLoansToSell = new ArrayList<>();

            for (Loan currLoan : oLoansToSell.get(currLoanToSell)){
                newLoansToSell.add(new LoanDTO(currLoan));
            }

            loansToSell.put(currLoanToSell, newLoansToSell);
        }
    }

    public Map<String, CustomerDTO> getCustomers() {
        return customers;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public Map<String, LoanDTO> getLoans() {
        return loans;
    }

    public Map<String, List<LoanDTO>> getLoansToSell() {
        return loansToSell;
    }

    public Map<String, LoanDTO> loansAsLender(String name) {
        List<LoanDTO> loansAsLender = customers.get(name).getLenderList();
        Map<String, LoanDTO> res = new HashMap<>();

        for (LoanDTO curr : loansAsLender) {
            res.put(curr.getLoanId(), curr);
        }

        return res;
    }

    public Map<String, LoanDTO> loansAsBorrower(String name) {
        List<LoanDTO> loansAsBorrower = customers.get(name).getBorrowerList();
        Map<String, LoanDTO> res = new HashMap<>();

        for (LoanDTO curr : loansAsBorrower) {
            res.put(curr.getLoanId(), curr);
        }

        return res;
    }

    public List<String> getCustomerAccountStatement(String name) {
        CustomerDTO curr = this.customers.get(name);
        AccountDTO accountDTO = curr.getAccount();
        return accountDTO.historyToString();
    }

    public CustomerDTO getCustomerDTO(String customerName){
        return (customers.get(customerName));
    }

    public int getEngineYazToView() {
        return engineYazToView;
    }

    public void setEngineYazToView(int engineToView) {
        this.engineYazToView = engineToView;
    }
}
