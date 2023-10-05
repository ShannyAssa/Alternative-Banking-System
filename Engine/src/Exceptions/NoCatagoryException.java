package Exceptions;

import java.util.Set;

public class NoCatagoryException extends RuntimeException{

    private String category;
    private final String EXCEPTION_MESSAGE = "Category %s does not exist.";
    Set<String> validCategories;

    public NoCatagoryException(String category, Set<String> validCategories) {
        this.category = category;
        this.validCategories = validCategories;
    }

    @Override
    public String getMessage(){

        String message = String.format(EXCEPTION_MESSAGE, category) + '\n';
        message += "The valid categories are: " + '\n';
        for (String curr : validCategories) {
            message += curr + '\n';
        }

        return message;
    }
}
