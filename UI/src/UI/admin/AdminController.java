package UI.admin;

import DTO.CustomerDTO;
import DTO.EngineDTO;
import UI.app.login.constants.Constants;
import UI.app.login.utils.HttpClientUtil;
import UI.header.HeaderController;
import UI.loans_table.LoansTableController;
import UI.utils.LoadAdminData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;

import static UI.app.login.constants.Constants.GSON_INSTANCE;
import static UI.app.login.constants.Constants.REFRESH_RATE;

public class AdminController {

    @FXML private Button btnIncreaseYaz;
    @FXML private Button rewindBtn;
    @FXML private Button changeViewBtn;
    @FXML private ScrollPane loansTableComponent;
    @FXML private LoansTableController loansTableComponentController;
    @FXML private Slider rewindSlider;
//    private Engine engine;
    //Customers table
    @FXML private TableView<CustomerDTO> customersTableComponent;
    @FXML private TableColumn<CustomerDTO, String> Name;
    @FXML private TableColumn<CustomerDTO, String> Capital;
    @FXML private TableColumn<CustomerDTO, Integer> LoansAsLenderSize;
    @FXML private TableColumn<CustomerDTO, Integer> LoansAsBorrowerSize;
    @FXML private HBox adminHeader;
    @FXML private HeaderController adminHeaderController;
    AdminRefresher adminRefresher;
    private Timer timer;


    private Stage primaryStage;

    @FXML public void initialize() {
        String finalUrl = HttpUrl
                .parse(Constants.ADMIN_EXISTENCE)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("initialize AdminController");
            }

            @Override public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("ERROR!");
                        alert.setHeaderText(responseBody);
                        alert.show();
                        return; });
                        }
                else {
                    Platform.runLater(() -> {
                        customersTableInit();
                        setAdminTables();
                        startAdminRefresher();
                        adminHeaderController.setName("Admin");
                        adminHeaderController.setMode();
                        adminHeaderController.setCurrentYazField();
                    });
                }
            }
        });
    }

    public void startAdminRefresher() {
        adminRefresher = new AdminRefresher();
        adminRefresher.setMainController(this);
        timer = new Timer();
        timer.schedule(adminRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void customersTableInit(){
        Name.setCellValueFactory(new PropertyValueFactory<CustomerDTO, String>("name"));
        Capital.setCellValueFactory(new PropertyValueFactory<CustomerDTO, String>("capital"));
        LoansAsLenderSize.setCellValueFactory(new PropertyValueFactory<CustomerDTO, Integer>("lenderListSize"));
        LoansAsBorrowerSize.setCellValueFactory(new PropertyValueFactory<CustomerDTO, Integer>("borrowerListSize"));
    }

    public void setCustomersTableItems(Map<String, CustomerDTO> customers){
        customersTableComponent.getItems().clear();
        customersTableComponent.getItems().setAll(customers.values());
    }

    public void setAdminTables() {

        String finalUrl = HttpUrl
                .parse(Constants.ADMIN_VIEW)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("setAdminTables AdminController");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfUsersNames = response.body().string();
                LoadAdminData adminData = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, LoadAdminData.class);
                setCustomersTableItems(adminData.getCustomers());
                loansTableComponentController.setItems(adminData.getLoans());

            }
        });
    }
    
    public void startRewind(ActionEvent actionEvent) {
        if(this.rewindSlider.isDisable()){
            this.rewindSlider.setDisable(false);
            this.rewindBtn.setText("Stop");
            this.disableButtons();
            this.adminHeaderController.setRewindSystemModeField();
            this.startRewindServer("rewind");
        }
        else{
            this.rewindSlider.setDisable(true);
            this.rewindBtn.setText("Rewind");
            this.enableButtons();
            this.adminHeaderController.setRegularSystemModeField();
            this.startRewindServer("regular");
        }

    }


    public void startRewindServer(String mode){
        int yazToView = (int) this.rewindSlider.getValue();
        String finalUrl = HttpUrl
                .parse(Constants.CHANGE_MODE)
                .newBuilder()
                .addQueryParameter("mode", mode)
                .addQueryParameter("yazToView", String.valueOf(yazToView))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("startRewind yaz Error in adminController");
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Platform.runLater(() -> {
                        if(response.code() != 200){
                            System.out.println("Error in rewind adminController");
                        }
                        try {
                            if(mode.equals("rewind")){
                                adminRefresher.setMode("rewind");
                                String jsonEngineToView = response.body().string();
                                EngineDTO engineToView = GSON_INSTANCE.fromJson(jsonEngineToView, EngineDTO.class);
                                loansTableComponentController.setItems(engineToView.getLoans());
                                setCustomersTableItems(engineToView.getCustomers());
                                adminHeaderController.setCurrentYazField((int) rewindSlider.getValue());
                            }
                            else{
                                adminRefresher.setMode("regular");
                                setAdminTables();
                                adminHeaderController.setCurrentYazField();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            }
        });
    }

    private void enableButtons() {
        this.btnIncreaseYaz.setDisable(false);
        this.changeViewBtn.setDisable(true);
//        this.mainController.enableButtons();
        this.setAdminTables();
        // this.mainController.updateCustomerTables();
       // this.mainController.updateScrambleAndNotifications();
    }

    public void disableButtons(){
        this.btnIncreaseYaz.setDisable(true);
        this.changeViewBtn.setDisable(false);
        //        this.mainController.disableButtons();
    }

    @FXML
    public void changeEngineView(DragEvent dragEvent) {
//        int yazToView = (int)this.rewindSlider.getValue();
//
//        if(yazToView == this.engine.getYaz()){
//            this.setAdminTables();
//            this.mainController.updateCustomerTables();
//            this.mainController.updateScrambleAndNotifications();
//        }
//
//
//        else
//        {
//            EngineDTO engineToView = engine.getPreviousEngines().get(yazToView - 1);
//            this.setCustomersTableItems(engineToView.getCustomers());
//            this.loansTableComponentController.setItems(engineToView.getLoans());
//
//            this.mainController.setPreviousCustomersTableItems(engineToView);
//        }

    }

    @FXML
    void increaseYazPressed(ActionEvent event) {
        String finalUrl = HttpUrl
                .parse(Constants.PROMOTE_YAZ)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("Promoting yaz Error");
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    System.out.println("Error");
                } else {
                    Platform.runLater(() -> {
//                        int previousYaz = headerComponentController.getCurrentYazField();
//                        headerComponentController.setCurrentYazField(++previousYaz);
                        rewindSlider.setMax(rewindSlider.getMax() + 1);
                        rewindSlider.setValue(rewindSlider.getMax());
                        setAdminTables();

                    });
                }
            }
        });
//        loansTableComponentController.setItems(mainController.getEngineLoansInfo());
    }

    public void updateHeaderInfo() {
        adminHeaderController.setCurrentYazField();
    }

    public void changeView(ActionEvent actionEvent) {
        this.startRewindServer("rewind");
    }

//    public Map<String, LoanDTO> getEngineLoansInfo(){
//        return engine.loansInfo();
//    }

//    public Map<String, CustomerDTO> getEngineCustomerInfo(){
//        return engine.customersInfo();
//    }

    public String getMode(){
        return this.adminHeaderController.getSystemModeField();
    }
}