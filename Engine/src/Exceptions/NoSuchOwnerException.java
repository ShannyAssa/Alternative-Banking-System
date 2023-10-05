package Exceptions;

import java.util.Set;

public class NoSuchOwnerException extends RuntimeException{
    private String owner;
    private final String EXCEPTION_MESSAGE = "Owner %s does not exist.";

    public NoSuchOwnerException(String owner) {
        this.owner = owner;
    }

    @Override
    public String getMessage(){
        return String.format(EXCEPTION_MESSAGE, owner) + '\n';
    }
}
