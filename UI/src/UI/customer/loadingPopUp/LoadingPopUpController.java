package UI.customer.loadingPopUp;

import UI.app.AppController;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class LoadingPopUpController {
    @FXML private ProgressBar progressBar;

    private AppController mainController;
    private Stage loadingStage;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void showPopup(Parent root, ReadOnlyDoubleProperty progressProperty){
        progressBar.progressProperty().bind(progressProperty);
        this.loadingStage = new Stage();
        Scene scene = new Scene(root, 300, 75);

        this.loadingStage.setScene(scene);
        loadingStage.show();

    }

    public void closePopup(){
        loadingStage.close();
    }
}


