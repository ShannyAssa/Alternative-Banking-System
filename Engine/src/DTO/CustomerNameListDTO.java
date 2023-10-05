package DTO;

import Customer.impl.Customer;

public class CustomerNameListDTO {
    private String name;
    private int customerNumber;
    private double capital;

    public CustomerNameListDTO(Customer curr) {
        this.name = curr.getName();
        this.capital = curr.getAccount().getCapital();
    }

    public CustomerNameListDTO(CustomerDTO currDTO) {
        this.name = currDTO.getName();
        this.customerNumber = currDTO.getCustomerNumber();
        this.capital = currDTO.getCapital();
    }

    public String getName() {
        return name;
    }

    public int getCustomerNumber() {
        return customerNumber;
    }

    public double getCapital() {
        return capital;
    }

    @Override
    public String toString() {
        return (customerNumber + " - " + name + " Capital: " + capital);
    }
}

