package DTO;
// delete this DTO ..?
import Account.impl.Loan;
import Customer.impl.Customer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SystemDTO {
    private Map<String, Customer> customers= new HashMap();
    private Set<String> categories = new HashSet<>();
    private Map<String, Loan> loans= new HashMap();

    public SystemDTO(Map<String, Customer> customers, Set<String> categories, Map<String, Loan> loans) {
        this.customers = customers;
        this.categories = categories;
        this.loans = loans;
    }

}
