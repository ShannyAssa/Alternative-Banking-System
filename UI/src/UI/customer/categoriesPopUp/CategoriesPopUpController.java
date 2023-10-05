package UI.customer.categoriesPopUp;

import DTO.CategoryDTO;
import UI.app.AppController;
import UI.app.login.constants.Constants;
import UI.app.login.utils.HttpClientUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static UI.app.login.constants.Constants.GSON_INSTANCE;

public class CategoriesPopUpController {

    @FXML private TableView<CategoryDTO> categoriesSelectTable;
    @FXML private Button finishSelectionBtn;
    @FXML private TableColumn<CategoryDTO,String> categoryColumn;
    @FXML private TableColumn<CategoryDTO, Boolean> selectCategoryColumn;
    private AppController mainController;
    private Stage categoriesStage;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void showPopup(Parent root){
        this.categoriesStage = new Stage();
        Scene scene = new Scene(root, 600, 520);

        setItems();

        this.categoriesStage.setScene(scene);
        this.categoriesStage.show();
    }


    public void initialize(){
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        selectCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("checked"));
    }

    public void setItems(){
        categoriesSelectTable.getItems().clear();

        String finalUrl = HttpUrl
                .parse(Constants.CATEGORIES_PAGE)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("setItems in CategoriesPopUpController")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            System.out.println("setItems in CategoriesPopUpController response code != 200 :" + response.code())
                    );
                } else {
                    Platform.runLater(() -> {
                        try {
                            String jsonCategories = response.body().string();
                            List<String> categories = GSON_INSTANCE.fromJson(jsonCategories, List.class);
                            List<CategoryDTO> categoriesDTO = new LinkedList<>();
                            for (String curr:categories) {
                                categoriesDTO.add(new CategoryDTO(curr));
                            }
                            categoriesSelectTable.getItems().setAll(categoriesDTO);
                            selectCategoryColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
                            categoriesSelectTable.setEditable(true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
            }
        });


    }

    @FXML
    public void finishSelection(ActionEvent event) {
        List<CategoryDTO> checkedCategories = new LinkedList<>();
        for (CategoryDTO curr : categoriesSelectTable.getItems()) {
            if(curr.checkedProperty().getValue()){
                checkedCategories.add(curr);
            }
        }

        this.mainController.updateScrambleCategories(checkedCategories);
        this.categoriesStage.close();
    }
}
