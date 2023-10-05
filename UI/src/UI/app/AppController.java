package UI.app;

import DTO.CategoryDTO;
import DTO.CustomerDTO;
import DTO.EngineDTO;
import DTO.LoanDTO;
import UI.app.login.LoginController;
import UI.app.login.constants.Constants;
import UI.app.login.utils.HttpClientUtil;
import UI.customer.CustomerController;
import UI.header.HeaderController;
import UI.utils.DeltaLoadData;
import UI.utils.LoadCustomerData;
import UI.utils.LoadDataIndicatorAndException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static UI.app.login.constants.Constants.GSON_INSTANCE;

public class AppController {

    public void setCurrentInfo(DeltaLoadData data) {
        LoadCustomerData LoadData= data.getNewData();
        customerController.setCurrentInfo(LoadData);
        if(data.isUpdateBorrower() && data.isUpdateLender()){
            customerController.setCustomersTableItems(LoadData.getLoansAsBorrower(),
            LoadData.getLoansAsLender(), LoadData.getAccountStatement(), LoadData.getMessages());
            System.out.println("Update both");
        }
        else {
            if(data.isUpdateBorrower()) {
                customerController.setBorrowerOnly(LoadData.getLoansAsBorrower(), LoadData.getAccountStatement(), LoadData.getMessages());
                System.out.println("Update borrower tables");
            }
            else if(data.isUpdateLender()){
                customerController.setLenderOnly(LoadData.getLoansAsLender(), LoadData.getAccountStatement(), LoadData.getMessages());
                System.out.println("Update lender tables");
            }

        }
        customerController.setActionsList(data.getNewData().getAccountStatement());
        customerController.setNotificationsCenter(data.getNewData().getMessages());
    }

    enum popupMessageType {Success, Warning}

//    @FXML private HBox headerComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private BorderPane centerBorderPane;
    private CustomerController customerController;
    private Parent customerView;
    private LoginController loginController;
    private Parent startingAnimationView;
    private BorderPane loginBorderPane;

    private final StringProperty currentUserName;

    public AppController() {
        currentUserName = new SimpleStringProperty("user");
    }

    @FXML
    public void initialize() {
//        if (headerComponentController != null) {
//            headerComponentController.setMainController(this);
//        }
        loadLoginPage();
    }

    public void updateUserName(String userName) {
        currentUserName.set(userName);
        this.headerComponentController.setName(userName);
    }

    public LoadCustomerData getCurrentInfo() {
        return customerController.getCurrentInfo();
    }


    public void updateHeaderInfo(){
        this.headerComponentController.setMode();
        if(headerComponentController.getSystemModeField().equals("Regular")) {
            customerController.setCustomerRefresherMode("regular");
            this.headerComponentController.setCurrentYazField();
        }
        else{
            customerController.setCustomerRefresherMode("rewind");
            this.headerComponentController.setRewindSystemModeField();
            this.customerRewindFetchServer();
        }
    }

    public void customerRewindFetchServer(){
        String finalUrl = HttpUrl
                .parse(Constants.CUSTOMER_REWIND_FETCH)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("customerRewindFetchServer Error in appController");
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    if(response.code() != 200){
                        System.out.println("Error in rewind appController");
                    }
                    try {
                        String jsonEngineToView = response.body().string();
                        EngineDTO engineToView = GSON_INSTANCE.fromJson(jsonEngineToView, EngineDTO.class);
                        headerComponentController.setCurrentYazField(engineToView.getEngineYazToView());
                        setRewindViewTables(engineToView);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public void setRewindViewTables(EngineDTO engineDTO){
        if(engineDTO.getCustomers().containsKey(currentUserName.getValue())){
            CustomerDTO customerDTO = engineDTO.getCustomerDTO(currentUserName.getValue());
            Map<String, LoanDTO> loansAsBorrower = new HashMap<>();
            Map<String, LoanDTO> loansAsLender = new HashMap<>();

            for (LoanDTO curr : customerDTO.getBorrowerList()){
                loansAsBorrower.put(curr.getLoanId(), curr);
            }
            for (LoanDTO curr : customerDTO.getLenderList()){
                loansAsLender.put(curr.getLoanId(), curr);
            }

            customerController.setCustomersTableItems(loansAsBorrower, loansAsLender,
                    customerDTO.getAccount().historyToString(), customerDTO.getMessages());
        }
        else{
            Map<String, LoanDTO> emptyLoans = new HashMap<>();
            List<String> emptyListString = new ArrayList<>();
            customerController.setCustomersTableItems(emptyLoans, emptyLoans, emptyListString, emptyListString);
        }
    }

    public void loadLoginPage(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("/UI/app/login/login.fxml");
            fxmlLoader.setLocation(url);
            this.startingAnimationView = fxmlLoader.load(url.openStream());
            this.loginController = fxmlLoader.getController();
            centerBorderPane.setCenter(startingAnimationView);
            centerBorderPane.setStyle("-fx-background-color: #071D4C");
            this.loginController.startAnimation();
            this.loginController.setMainController(this);
            this.headerComponentController.setCurrentYazField();
            this.headerComponentController.setMode();

        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }


    public void setCustomerView(String name, Map<String, LoanDTO> loansAsBorrower, Map<String, LoanDTO> loansAsLender,
                                List<String> accountStatement, List<String> messages) throws IOException {
        if(this.customerView != null){
            centerBorderPane.setCenter(customerView);
            this.customerController.setCustomerName(name);
            this.customerController.setCurrentInfo(new LoadCustomerData(loansAsBorrower, loansAsLender, accountStatement, messages));
            this.customerController.setCustomersTableItems(loansAsBorrower, loansAsLender, accountStatement, messages);
            this.customerController.clearScrambleTable();
            return;
        }
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("/UI/customer/customer_view.fxml");
            fxmlLoader.setLocation(url);
            this.customerView = fxmlLoader.load(url.openStream());
            this.customerController = fxmlLoader.getController();
            this.customerController.setMainController(this, name);
            centerBorderPane.setCenter(customerView);
            this.customerController.setCurrentInfo(new LoadCustomerData(loansAsBorrower, loansAsLender, accountStatement, messages));
            this.customerController.setCustomersTableItems(loansAsBorrower, loansAsLender, accountStatement, messages);

            this.customerController.startCustomerRefresher();


        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void updateCustomerTables(){
        this.customerController.getCustomerInfo();
    }



    public void setXmlFile(String path, String customerName){
        try{
            String finalUrl = HttpUrl
                    .parse(Constants.LOAD_DATA)
                    .newBuilder()
                    .addQueryParameter("username", customerName)
                    .addQueryParameter("path", path)
                    .build()
                    .toString();
            HttpClientUtil.runAsync(finalUrl, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> System.out.println("Set xml file error"));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                    if (response.code() != 200) {
                        String responseBody = response.body().string();
                        Platform.runLater(() ->
                                System.out.println("setXml in AppController error code !=200 :" + responseBody)
                        );
                    }
                    else{
                        Platform.runLater(() -> {
                            try {
                                String jsonWasSuccessful = response.body().string();
                                LoadDataIndicatorAndException dataIndicatorAndException;
                                dataIndicatorAndException = GSON_INSTANCE.fromJson(jsonWasSuccessful, LoadDataIndicatorAndException.class);
                                if(dataIndicatorAndException.getWasSuccessful()){
                                    popUpMessage(popupMessageType.Success, "The XML file was loaded successfully.", "");
                                    customerController.getCustomerInfo(); //on success get the customer info
                                }
                                else {
                                    popUpMessage(popupMessageType.Warning, "The XML file was not loaded successfully. \n The system will continue operating on the last valid XML file (if exists). ",
                                            dataIndicatorAndException.getExceptionMessage());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void popUpMessage(popupMessageType type, String headerText, String contentText) {
        Alert alert = new Alert(null);
        if(type.equals(popupMessageType.Success)) {
            alert.setAlertType(Alert.AlertType.INFORMATION);
        }
        else{
            alert.setAlertType(Alert.AlertType.WARNING);
        }
        alert.setTitle(type.toString());
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public void updateCustomerActionList(List<String> actionList){
        this.customerController.setActionsList(actionList);
    }

    public void updateScrambleCategories(List<CategoryDTO> checkedCategories){
        customerController.setCheckedCategories(checkedCategories);
    }


    public void disableButtons() {
        this.customerController.disableButtons();
    }

    public void enableButtons() {
        this.customerController.enableButtons();
    }

    public void updateScrambleAndNotifications(){
        this.customerController.clearScrambleTable();
//        this.customerController.setNotificationsCenter();
    }

    public void setPreviousCustomersTableItems(EngineDTO engineToView) {
        this.customerController.setPreviousCustomersTableItems(engineToView);
    }
}
