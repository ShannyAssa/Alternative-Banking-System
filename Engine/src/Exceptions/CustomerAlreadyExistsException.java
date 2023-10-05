package Exceptions;

public class CustomerAlreadyExistsException extends RuntimeException{
    private String customer;
    private final String EXCEPTION_MESSAGE = "Customer %s already exists.";

    public CustomerAlreadyExistsException(String customer) {
        this.customer = customer;
    }

    @Override
    public String getMessage(){
        return String.format(EXCEPTION_MESSAGE, customer);
    }

}
