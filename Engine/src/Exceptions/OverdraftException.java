package Exceptions;

public class OverdraftException extends RuntimeException{
    private final String EXCEPTION_MESSAGE = "Overdraft is not allowed.";

    @Override
    public String getMessage(){
        return EXCEPTION_MESSAGE;
    }
}
