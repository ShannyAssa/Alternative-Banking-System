package Exceptions;

public class LoanAlreadyExists extends RuntimeException{
    private String loan;
    private final String EXCEPTION_MESSAGE = "Loan %s already exists.";

    public LoanAlreadyExists(String loan) {
        this.loan = loan;
    }

    @Override
    public String getMessage(){
        return String.format(EXCEPTION_MESSAGE, loan);
    }
}



