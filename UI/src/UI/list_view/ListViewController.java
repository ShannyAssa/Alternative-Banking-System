package UI.list_view;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import UI.app.AppController;

public class ListViewController {
    @FXML private ListView<String> listView;
    private AppController mainController;


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public ListView<String> getListView() {
        return listView;
    }
}
