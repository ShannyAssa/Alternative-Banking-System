package UI.customer.newLoanPopUp;

import UI.app.AppController;
import UI.app.login.constants.Constants;
import UI.app.login.utils.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static UI.app.login.constants.Constants.GSON_INSTANCE;

public class NewLoanPopUpController {
    @FXML private TextField newLoanIdField;
    @FXML private TextField newLoanCapitalField;
    @FXML private TextField newLoanLoanTermField;
    @FXML private TextField newLoanLoanIntervalField;
    @FXML private ComboBox<String> newLoanCategoryCombo;
    @FXML private Slider newLoanInterestSlider;
    @FXML private Button createNewLoanBtn;

    private AppController mainController;
    private Stage newLoanPopUpStage;
    private String customerName;
    private SimpleBooleanProperty wasCreated = new SimpleBooleanProperty();


    public void setMainController(AppController mainController, String customerName)
    {
        this.mainController = mainController;
        this.customerName = customerName;
    }

    public void showPopup(Parent root){
        this.fetchCategoriesServer();

        this.newLoanPopUpStage = new Stage();
        Scene scene = new Scene(root, 600, 520);
        this.newLoanPopUpStage.setScene(scene);
        this.newLoanPopUpStage.show();
    }

    public void setComboBoxCategories(List<String> categories){
        newLoanCategoryCombo.getItems().clear();
        newLoanCategoryCombo.getItems().addAll(categories);
    }

    public void fetchCategoriesServer(){
        String finalUrl = HttpUrl
                .parse(Constants.CATEGORIES_PAGE)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("fetchCategoriesServer in newLoanPopUpController")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            System.out.println("fetchCategoriesServer in newLoanPopUpController response code != 200 :" + response.code())
                    );
                } else {
                    Platform.runLater(() -> {
                        try {
                            String jsonCategories = response.body().string();
                            List<String> categories = GSON_INSTANCE.fromJson(jsonCategories, List.class);
                            setComboBoxCategories(categories);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
            }
        });
    }

    @FXML
    public void createNewLoan(ActionEvent event) {
        String errorMessage = "";
        this.wasCreated.set(false);
        int principal, loanTerm, loanInterval, interest;
        String loanId, category;
        try{
            principal = Integer.parseInt(newLoanCapitalField.getText());
            loanTerm = Integer.parseInt(newLoanLoanTermField.getText());
            loanInterval = Integer.parseInt(newLoanLoanIntervalField.getText());
            loanId = newLoanIdField.getText();
            if(loanTerm <= 0){
                errorMessage = "Loan term must be greater than 0.";
                throw new Exception();
            }
            else if(principal <= 0){
                errorMessage = "Principal must be greater than 0.";
                throw new Exception();
            }
            else if(loanInterval <= 0){
                errorMessage = "Loan interval must be greater than 0.";
                throw new Exception();
            }

            else if(loanId.isEmpty()){
                errorMessage = "Loan id field must not be empty.";
                throw new Exception();
            }
            else if(newLoanCategoryCombo.getValue().isEmpty()){
                errorMessage = "You must choose a category from the combo box.";
                throw new Exception();
            }
            else if(newLoanInterestSlider.getValue() == 0){
                errorMessage = "Loan interest must be greater than 0.";
                throw new Exception();
            }
            else if(loanTerm % loanInterval != 0){
                errorMessage = "The interval payments do not match the loan term.";
                throw new Exception();
            }
            else{
                category = newLoanCategoryCombo.getValue();
                interest = (int) newLoanInterestSlider.getValue();
                createNewLoanServer(customerName, principal, loanTerm, loanInterval, loanId, category, interest);

            }
        }
        catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR!");
            alert.setHeaderText("Principal, loan term and loan interval values must be integers.");
            alert.show();
            return;
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR!");
            alert.setHeaderText(errorMessage);
            alert.show();
            return;
        }


    }

    public void createNewLoanServer(String name, int principal, int loanTerm, int loanInterval, String loanId,
                                       String category, int interest){
        String finalUrl = HttpUrl
                .parse(Constants.CREATE_NEW_LOAN_PAGE)
                .newBuilder()
                .addQueryParameter("principal", String.valueOf(principal))
                .addQueryParameter( "loanTerm", String.valueOf(loanTerm))
                .addQueryParameter( "loanInterval", String.valueOf(loanInterval))
                .addQueryParameter( "loanId", String.valueOf(loanId))
                .addQueryParameter( "category", String.valueOf(category))
                .addQueryParameter( "interest", String.valueOf(interest))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("create new loan error in NewLoanPopUpController");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    try {
                        String jsonCreateNewLoanInfo = response.body().string();
                        Boolean wasCreatedFromServer = GSON_INSTANCE.fromJson(jsonCreateNewLoanInfo, Boolean.class);
                        if(!wasCreatedFromServer){
                            String errorMessage = "Loan ID already exists. Please choose a different ID.";
                            throw new Exception();
                        }
                        else{
                            mainController.updateCustomerTables();
                            //        this.mainController.updateAdminTables();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Info!");
                            alert.setHeaderText("You have created a new loan successfully!");
                            alert.show();
                            newLoanPopUpStage.close();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("ERROR!");
                        alert.setHeaderText(e.getMessage());
                        alert.show();
                        return;
                    }
                });
            }
        });
    }
}

