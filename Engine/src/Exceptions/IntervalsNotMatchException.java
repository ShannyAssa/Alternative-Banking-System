package Exceptions;

public class IntervalsNotMatchException extends RuntimeException{
    private final String EXCEPTION_MESSAGE = "The interval payments do not match the loan term.";

    @Override
    public String getMessage(){
        return EXCEPTION_MESSAGE;
    }
}
