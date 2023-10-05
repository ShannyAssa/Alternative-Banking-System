package UI.customer;

import Account.impl.Loan;
import DTO.CategoryDTO;
import DTO.CustomerDTO;
import DTO.EngineDTO;
import DTO.LoanDTO;
//import Engine.impl.Engine;
import UI.app.AppController;
import UI.app.login.constants.Constants;
import UI.app.login.utils.HttpClientUtil;
import UI.customer.actionPopUp.ActionPopUpController;
import UI.customer.buyLoanPopUp.BuyLoanPopUpController;
import UI.customer.categoriesPopUp.CategoriesPopUpController;
import UI.customer.loadingPopUp.LoadingPopUpController;
import UI.customer.newLoanPopUp.NewLoanPopUpController;
import UI.customer.scrambleTable.ScrambleTableController;
import UI.loans_table.LoansTableController;
import UI.utils.FilterLoansInfo;
import UI.utils.LoadCustomerData;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static UI.app.login.constants.Constants.GSON_INSTANCE;
import static UI.app.login.constants.Constants.REFRESH_RATE;

public class CustomerController {
    @FXML private ScrollPane lenderTableComponent;
    @FXML private LoansTableController lenderTableComponentController;
    @FXML private ScrollPane borrowerTableComponent;
    @FXML private LoansTableController borrowerTableComponentController;
    @FXML private Button depositBtn;
    @FXML private Button withdrawBtn;
    @FXML private ListView<String> actionsList;
    @FXML private ListView<String> notificationsCenter;
    @FXML private Button paySinglePaymentBtn;
    @FXML private Button payFullLoanBtn;
    @FXML private ScrollPane paymentBorrowerTableComponent;
    @FXML private LoansTableController paymentBorrowerTableComponentController;
    @FXML private TextField totalAmountField;
    @FXML private Button openCategoriesBtn;
    @FXML private Slider interestMinimumSlider;
    @FXML private TextField minimumLoanTermField;
    @FXML private TextField maximumOpenLoansField;
    @FXML private Slider ownershipSlider;
    @FXML private Button filterLoansBtn;
    @FXML private Button joinAsLenderBtn;
    @FXML private ScrollPane scrambleTableComponent;
    @FXML private ScrambleTableController scrambleTableComponentController;
    @FXML private Button createNewLoanBtn;
    @FXML private Button loadNewLoanBtn;
    @FXML private Button sellLoanBtn;
    @FXML private Button buyOfferedLoansBtn;
    @FXML private HBox customerHeader;

    private LoadingPopUpController loadingPopUpController;
    private NewLoanPopUpController newLoanPopUpController;
    private CustomerRefresher customerRefresher;
    private LoadCustomerData currentInfo;

    private AppController mainController;
//    private Engine engine;
    private String customerName;
    private List<String> checkedCategories;
    private ActionPopUpController actionPopUpController;

    private Timer timer;


    public void initialize(){
        this.ownershipSlider.setValue(ownershipSlider.getMax());
    }

    public void setMainController(AppController mainController, String name) {
        this.mainController = mainController;
        this.customerName = name;
//        this.checkedCategories = engine.getCategoriesList();
        this.checkedCategories = new ArrayList<>();

        lenderTableComponentController.setMainController(mainController);
        borrowerTableComponentController.setMainController(mainController);
        scrambleTableComponentController.setMainController(mainController);
    }

    public void setCustomersTableItems(Map<String, LoanDTO> loansAsBorrower, Map<String, LoanDTO> loansAsLender,
                                       List<String> accountStatement, List<String> messages){
        lenderTableComponentController.setItems(loansAsLender);
        borrowerTableComponentController.setItems(loansAsBorrower);
        loansAsBorrower.values().removeIf(loanDTO -> !loanDTO.getStatus().equals(Loan.Status.ACTIVE) &&
                    !loanDTO.getStatus().equals(Loan.Status.RISK));
        paymentBorrowerTableComponentController.setItems(loansAsBorrower);
        setActionsList(accountStatement);
        setNotificationsCenter(messages);

    }

    public void setBorrowerOnly(Map<String, LoanDTO> loansAsBorrower, List<String> accountStatement, List<String> messages) {
        borrowerTableComponentController.setItems(loansAsBorrower);
        loansAsBorrower.values().removeIf(loanDTO -> !loanDTO.getStatus().equals(Loan.Status.ACTIVE) &&
                !loanDTO.getStatus().equals(Loan.Status.RISK));
        paymentBorrowerTableComponentController.setItems(loansAsBorrower);
//        setActionsList(accountStatement);
//        setNotificationsCenter(messages);
    }

    public void setLenderOnly(Map<String, LoanDTO> loansAsLender, List<String> accountStatement, List<String> messages){
        lenderTableComponentController.setItems(loansAsLender);
//        setActionsList(accountStatement);
//        setNotificationsCenter(messages);

    }

    public void setActionsList(List<String> accountStatement){
        actionsList.getItems().clear();
        actionsList.getItems().addAll(accountStatement);
        if(actionsList.getItems().isEmpty()){
            actionsList.getItems().add("There is no data to present.");
        }
    }

    public void setNotificationsCenter(List<String> messages){
        notificationsCenter.getItems().clear();
        notificationsCenter.getItems().addAll(messages);
        if(notificationsCenter.getItems().isEmpty()){
            notificationsCenter.getItems().add("There is no data to present.");
        }
    }

    public void setCustomerRefresherMode(String mode){
        customerRefresher.setMode(mode);
    }

    public void startCustomerRefresher() {
        customerRefresher = new CustomerRefresher();
        customerRefresher.setMainController(this.mainController);
        timer = new Timer();
        timer.schedule(customerRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    @FXML
    public void depositMoney(ActionEvent actionEvent) throws IOException {
        openActionPopUp("Please enter the amount of money to deposit:", ActionPopUpController.submitType.DEPOSIT);
    }

    @FXML
    public void withdrawMoney(ActionEvent actionEvent) throws IOException {
        openActionPopUp("Please enter the amount of money to withdraw:", ActionPopUpController.submitType.WITHDRAW);
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void openActionPopUp(String labelData, ActionPopUpController.submitType type) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/UI/customer/actionPopUp/action_popup.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        this.actionPopUpController = fxmlLoader.getController();
        this.actionPopUpController.setMainController(mainController, type,labelData, customerName);
        this.actionPopUpController.showPopup(root);
    }

    public void paySinglePayment(int yaz) throws IOException {
        LoanDTO currLoan = paymentBorrowerTableComponentController.getSelectedRowFromTable();
        if(currLoan == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("info");
            alert.setHeaderText("Please select a loan to pay!");
            alert.show();
            return;
        }
            if(currLoan.getPaymentInterval() != 1 && (currLoan.getNextRepayment() != yaz && !currLoan.getStatus().equals(Loan.Status.RISK))){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR!");
                alert.setHeaderText("Cannot pay loans if the repayment date is not matching the current yaz date");
                alert.show();
            }
            else if(currLoan.getStatus().equals(Loan.Status.ACTIVE)){
                try{
                    if(!currLoan.getHistory().isEmpty() && currLoan.getHistory().get(currLoan.getHistory().size() - 1).getYazDate() == yaz){
                        throw new Exception();
                    }
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid action");
                    alert.setHeaderText("You have already paid this day's payment.");
                    alert.show();
                    return;
                }
                payBackLoanServer("single", currLoan.getLoanId());
            }
            else if(currLoan.getStatus().equals(Loan.Status.RISK)){
                openActionPopUp("Please enter the amount of money to deposit:", ActionPopUpController.submitType.PAYBACK);
                this.actionPopUpController.setLoan(currLoan);
            }


    }

    @FXML
    public void payFullLoan(ActionEvent event) throws IOException {
        LoanDTO currLoan = paymentBorrowerTableComponentController.getSelectedRowFromTable();
        if(currLoan == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("info");
            alert.setHeaderText("Please select a loan to pay!");
            alert.show();
            return;

        }
        payBackLoanServer("full", currLoan.getLoanId());

    }

    public void payBackLoanServer(String paymentType, String loanId){
        String finalUrl = HttpUrl
                .parse(Constants.PAYBACK_LOAN_PAGE)
                .newBuilder()
                .addQueryParameter("paymentType", paymentType)
                .addQueryParameter("loanId", loanId)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("payBackLoanServer error in CustomerController");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    try {
                        String jsonPayLoanInfo = response.body().string();
                        boolean wasPaymentSuccessful= GSON_INSTANCE.fromJson(jsonPayLoanInfo, boolean.class);
                        if(wasPaymentSuccessful){
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Info!");
                            if(paymentType.equals("single")){
                                alert.setHeaderText("The single payment was successful.");
                            }
                            else{
                                alert.setHeaderText("The full payment was successful.");
                            }
                            alert.show();
                            mainController.updateCustomerTables();
                        }

                        else{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("ERROR!");
                            alert.setHeaderText("There is not enough money to pay this payment.");
                            alert.show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @FXML
    public void paySinglePaymentHelperServer(ActionEvent event){
        String finalUrl = HttpUrl
                .parse(Constants.FETCH_YAZ)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("fetchYaz error in CustomerController");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    try {
                        String jsonYazInfo = response.body().string();
                        int yaz = GSON_INSTANCE.fromJson(jsonYazInfo, int.class);
                        paySinglePayment(yaz);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @FXML
    public void joinAsLender(ActionEvent actionEvent) throws IOException {
        List<LoanDTO> loansToAssign = scrambleTableComponentController.getCheckedLoans();

        if(loansToAssign.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You must select a loan before pressing Join As A Lender");
            alert.show();
            return;
        }

        List<String> loansId = new LinkedList<>();
        for(LoanDTO currLoan : loansToAssign){
            loansId.add(currLoan.getLoanId());
        }

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(loansId);

        String finalUrl = HttpUrl
                .parse(Constants.JOIN_AS_LENDER)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("joinAsLender in customerController" + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Platform.runLater(() -> {
                        try {
                            String jsonJoinAsLender = response.body().string();
                            CustomerDTO customer = GSON_INSTANCE.fromJson(jsonJoinAsLender, CustomerDTO.class);
                            double customerCapital = customer.getCapital();
                            int maxAmountToLoan = 0, ownershipMaxPercentage;
                            try{
                                maxAmountToLoan = Integer.parseInt(totalAmountField.getText());
                                ownershipMaxPercentage = (int)ownershipSlider.getValue();
                            }
                            catch (NumberFormatException e){
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Invalid input");
                                alert.setHeaderText("Please enter only integer value which is bounded by the customer's capital." +
                                        "\nCapital: " + customerCapital);
                                alert.show();
                                return;
                            }
                            loansAssignment(customer.getName(), jsonRequest, maxAmountToLoan, ownershipMaxPercentage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            }
        });
    }



    @FXML
    public void openCategories(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/UI/customer/categoriesPopUp/categoriesPopUp.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        CategoriesPopUpController categoriesPopUpController = fxmlLoader.getController();
        categoriesPopUpController.setMainController(mainController);
        categoriesPopUpController.showPopup(root);
    }

    @FXML
    public void filterLoans(ActionEvent actionEvent) {
        String finalUrl = HttpUrl
                .parse(Constants.CUSTOMER_CAPITAL)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("CUSTOMER_CAPITAL error in CustomerController");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    try {
                        String jsonYazInfo = response.body().string();
                        double capital = GSON_INSTANCE.fromJson(jsonYazInfo, double.class);
                        filterLoansHelper(capital);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void filterLoansHelper(double customerCapital) {
        int maxAmountToLoan = 0, minLoanTerm = 0, maxOpenLoans = Integer.MAX_VALUE, interestMinPercentage, ownershipMaxPercentage;
        try {
            maxAmountToLoan = Integer.parseInt(totalAmountField.getText());
            if(maxAmountToLoan > customerCapital){
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid input");
            alert.setHeaderText("Please enter only integer value which is bounded by the customer's capital." +
                    "\nCapital: " + customerCapital);
            alert.show();
            return;
        }

        try {
            if(!minimumLoanTermField.getText().isEmpty()){
                minLoanTerm = Integer.parseInt(minimumLoanTermField.getText());
            }
            if(!maximumOpenLoansField.getText().isEmpty()){
                maxOpenLoans = Integer.parseInt(maximumOpenLoansField.getText());
            }
        }
        catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid input");
            alert.setHeaderText("Please enter only integer value.");
            alert.show();
            return;
        }

        interestMinPercentage = (int)interestMinimumSlider.getValue();
        ownershipMaxPercentage = (int)ownershipSlider.getValue();

        presentFilteredLoans(maxAmountToLoan, minLoanTerm, maxOpenLoans, interestMinPercentage, ownershipMaxPercentage);
    }

    public void presentFilteredLoans(int maxAmountToLoan, int minLoanTerm, int maxOpenLoans,int interestMinPercentage,int ownershipMaxPercentage){

        String finalUrl = HttpUrl
                .parse(Constants.LOANS_INFO)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("LOANS_INFO error in CustomerController");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    try {
                        String jsonYazInfo = response.body().string();
                        FilterLoansInfo filterLoansInfo = GSON_INSTANCE.fromJson(jsonYazInfo, FilterLoansInfo.class);
                        Map<String, LoanDTO> mapLoans = filterLoansInfo.getLoans();
                        Map<String, Integer> openLoansNumber=  filterLoansInfo.getOpenLoansNumber();

                        ArrayList<LoanDTO> loansDTO = mapLoans.values().parallelStream().filter(loan -> loan.getStatus().equals(Loan.Status.NEW) ||
                                        loan.getStatus().equals(Loan.Status.PENDING))
                                .filter(loan -> checkedCategories.contains(loan.getCategory()))
                                .filter(loan -> loan.getInterestRate() >= interestMinPercentage)
                                .filter(loan -> loan.getLoanTerm() >= minLoanTerm)
                                .filter(loan -> !loan.getOwner().equals(customerName))
                               // .filter(loan -> loan.getOwner() != customerName)
                                .filter(loan -> openLoansNumber.get(loan.getOwner()) <= maxOpenLoans)
                                .collect(Collectors.toCollection(ArrayList::new));

                        setScrambleTable(loansDTO);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });



    }


    public void setScrambleTable(ArrayList<LoanDTO> loansDTO){
        scrambleTableComponentController.setItems(loansDTO);
    }

    public void clearScrambleTable(){
        this.scrambleTableComponentController.clearScrambleTable();
        this.totalAmountField.clear();
        this.interestMinimumSlider.setValue(0);
        this.ownershipSlider.setValue(ownershipSlider.getMax());
        this.maximumOpenLoansField.clear();
        this.minimumLoanTermField.clear();
    }

    public void setCheckedCategories(List<CategoryDTO> updatedCategories){
        this.checkedCategories.clear();
        for (CategoryDTO curr : updatedCategories){
            this.checkedCategories.add(curr.getCategory());
        }
    }

    public void loansAssignment(String lender, String loansIdJson, double maxAmountToLoan, int ownershipMaxPercentage) throws IOException {
        String finalUrl = HttpUrl
                .parse(Constants.LOAN_ASSIGNMENT)
                .newBuilder()
                .addQueryParameter("lender", lender)
                .addQueryParameter("loansToAssign", loansIdJson)
                .addQueryParameter("maxAmountToLoan", String.valueOf(maxAmountToLoan))
                .addQueryParameter("ownershipMaxPercentage", String.valueOf(ownershipMaxPercentage))
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("loansAssignment in customerController");
                        }
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Assignment succeeded!");
                        alert.setHeaderText("You have joined as a lender to one or more of the loans selected.");
                        alert.show();
                        scrambleTableComponentController.clearScrambleTable();
                        getCustomerInfo();
                    //mainController.updateAdminTables();

                });
            }
        });




    }


    public void openLoadingPopUp(ReadOnlyDoubleProperty progressProperty) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/UI/customer/loadingPopUp/loadingPopUp.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        this.loadingPopUpController = fxmlLoader.getController();
        this.loadingPopUpController.setMainController(mainController);
        this.loadingPopUpController.showPopup(root, progressProperty);
    }

    public void openNewLoanPopUp() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/UI/customer/newLoanPopUp/newLoanPopUp.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        this.newLoanPopUpController = fxmlLoader.getController();
        this.newLoanPopUpController.setMainController(mainController, this.customerName);
        this.newLoanPopUpController.showPopup(root);
    }

    @FXML
    public void sellLoan(ActionEvent actionEvent){
        try{
            LoanDTO currLoan = lenderTableComponentController.getSelectedRowFromTable();
            if(!currLoan.getStatus().equals(Loan.Status.ACTIVE)){
                throw new Exception();
            }
            sellLoanServer(currLoan.getLoanId());

        }
        catch (NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR!");
            alert.setHeaderText("Please select a loan to sell!");
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("info!");
            alert.setHeaderText("You may sell only active loan.");
            alert.show();
            return;
        }


    }

    public void sellLoanServer(String loanId){
        String finalUrl = HttpUrl
                .parse(Constants.SELL_LOAN)
                .newBuilder()
                .addQueryParameter( "loanId", String.valueOf(loanId))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("sell new loan error in CustomerController");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    try {
                        String jsonSellLoanInfo = response.body().string();
                        Boolean wasPurposedSale = GSON_INSTANCE.fromJson(jsonSellLoanInfo, Boolean.class);
                        if(!wasPurposedSale){
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("ERROR!");
                            alert.setHeaderText("This loan is already offered for sale.");
                            alert.show();
                            return;
                        }
                        else{
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Info!");
                            alert.setHeaderText("This loan has been offered for sale.");
                            alert.show();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }



    @FXML
    public void buyOfferedLoans(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/UI/customer/buyLoanPopUp/buyLoanPopUp.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        BuyLoanPopUpController buyLoanPopUpController = fxmlLoader.getController();
        buyLoanPopUpController.setMainController(mainController, customerName);
        buyLoanPopUpController.showPopup(root);
    }

    @FXML
    public void createNewLoan(ActionEvent actionEvent) throws IOException {
        this.openNewLoanPopUp();
    }

    @FXML
    public void loadNewLoan(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile == null) {
            return;
        }
        String path = selectedFile.toString();
        mainController.setXmlFile(path, this.customerName);
    }

    public void getCustomerInfo(){
        String finalUrl = HttpUrl
                .parse(Constants.CUSTOMER_PAGE)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("Error")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    //String responseBody = response.body().string();
                    Platform.runLater(() ->
                            System.out.println("getCustomerInfo in customerController response code != 200 :" + response.code())
                    );
                } else {
                    Platform.runLater(() -> {
                        try {
                            String jsonArrayOfUsersNames = response.body().string();
                            LoadCustomerData customerData = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, LoadCustomerData.class);
                            currentInfo = customerData;
                            setCustomersTableItems(customerData.getLoansAsBorrower(), customerData.getLoansAsLender(),
                                    customerData.getAccountStatement(), customerData.getMessages());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
            }
        });
    }

    public LoadCustomerData getCurrentInfo() {
        return currentInfo;
    }

    public void setCurrentInfo(LoadCustomerData newInfo) {
        if(this.currentInfo != null){
            this.currentInfo.setAccountStatement(newInfo.getAccountStatement());
            this.currentInfo.setLoansAsBorrower(newInfo.getLoansAsBorrower());
            this.currentInfo.setLoansAsLender(newInfo.getLoansAsLender());
            this.currentInfo.setMessages(newInfo.getMessages());
        }
        else{
            currentInfo= newInfo;
        }

    }

    public void disableButtons() {
        this.createNewLoanBtn.setDisable(true);
        this.loadNewLoanBtn.setDisable(true);
        this.depositBtn.setDisable(true);
        this.withdrawBtn.setDisable(true);
        this.sellLoanBtn.setDisable(true);
        this.totalAmountField.setDisable(true);
        this.openCategoriesBtn.setDisable(true);
        this.filterLoansBtn.setDisable(true);
        this.interestMinimumSlider.setDisable(true);
        this.minimumLoanTermField.setDisable(true);
        this.maximumOpenLoansField.setDisable(true);
        this.ownershipSlider.setDisable(true);
        this.buyOfferedLoansBtn.setDisable(true);
        this.joinAsLenderBtn.setDisable(true);
        this.paySinglePaymentBtn.setDisable(true);
        this.payFullLoanBtn.setDisable(true);
    }

    public void enableButtons() {
        this.createNewLoanBtn.setDisable(false);
        this.loadNewLoanBtn.setDisable(false);
        this.depositBtn.setDisable(false);
        this.withdrawBtn.setDisable(false);
        this.sellLoanBtn.setDisable(false);
        this.totalAmountField.setDisable(false);
        this.openCategoriesBtn.setDisable(false);
        this.filterLoansBtn.setDisable(false);
        this.interestMinimumSlider.setDisable(false);
        this.minimumLoanTermField.setDisable(false);
        this.maximumOpenLoansField.setDisable(false);
        this.ownershipSlider.setDisable(false);
        this.buyOfferedLoansBtn.setDisable(false);
        this.joinAsLenderBtn.setDisable(false);
        this.paySinglePaymentBtn.setDisable(false);
        this.payFullLoanBtn.setDisable(false);
    }

    public void setPrevActionAndNotifications(EngineDTO engineToView){
        //set actions list
        actionsList.getItems().clear();
        actionsList.getItems().addAll(engineToView.getCustomerAccountStatement(this.customerName));
        if(actionsList.getItems().isEmpty()){
            actionsList.getItems().add("There is no data to present.");
        }
        // set notifications list
        notificationsCenter.getItems().clear();
        notificationsCenter.getItems().addAll(engineToView.getCustomers().get(this.customerName).getMessages());
        if(notificationsCenter.getItems().isEmpty()){
            notificationsCenter.getItems().add("There is no data to present.");
        }
    }

    public void setPreviousCustomersTableItems(EngineDTO engineToView){
        Map<String, LoanDTO> loansAsBorrower = engineToView.loansAsBorrower(this.customerName);

        lenderTableComponentController.setItems(engineToView.loansAsLender(this.customerName));

        borrowerTableComponentController.setItems(loansAsBorrower);

        loansAsBorrower.values().removeIf(loanDTO -> !loanDTO.getStatus().equals(Loan.Status.ACTIVE) &&
                !loanDTO.getStatus().equals(Loan.Status.RISK));


        paymentBorrowerTableComponentController.setItems(loansAsBorrower);
        setPrevActionAndNotifications(engineToView);
    }

//    public void setCustomerView(Engine engine, BorderPane loginBorderPane) throws IOException {
//        try{
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            URL url = getClass().getResource("/UI/customer/customer_view.fxml");
//            fxmlLoader.setLocation(url);
//            Parent parent = fxmlLoader.load(url.openStream());
//            loginBorderPane.setCenter(parent);
//        }
//        catch (IOException e){
//            System.out.println(e.getMessage());
//        }
//    }
}
