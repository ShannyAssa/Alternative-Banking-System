package Exceptions;

public class FileDoesNotExistException extends RuntimeException{

    private String fileName;
    private final String EXCEPTION_MESSAGE = "File %s does not exists.";

    public FileDoesNotExistException(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getMessage() { return String.format(EXCEPTION_MESSAGE, fileName); }
}
