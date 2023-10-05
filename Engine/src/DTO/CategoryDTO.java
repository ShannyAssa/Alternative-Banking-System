package DTO;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class CategoryDTO {
    private  String category;
    private final SimpleBooleanProperty checked = new SimpleBooleanProperty(true);

    public CategoryDTO(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }
}
