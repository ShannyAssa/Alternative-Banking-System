package UI.customer.buyLoanPopUp;

import Account.impl.Loan;
import DTO.LoanForSaleDTO;
import DTO.LoanForSaleDTOList;
import UI.app.AppController;
import UI.app.login.constants.Constants;
import UI.app.login.utils.HttpClientUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static UI.app.login.constants.Constants.GSON_INSTANCE;

public class BuyLoanPopUpController {

    @FXML private TableView<LoanForSaleDTO> sellLoansTable;

    @FXML private TableColumn<LoanForSaleDTO, String> sellerName;

    @FXML private TableColumn<LoanForSaleDTO, String> loanId;

    @FXML private TableColumn<LoanForSaleDTO, String> category;

    @FXML private TableColumn<LoanForSaleDTO, Loan.Status> status;

    @FXML private TableColumn<LoanForSaleDTO, Integer> principal;

    @FXML private TableColumn<LoanForSaleDTO, Integer> paymentInterval;

    @FXML private TableColumn<LoanForSaleDTO, Integer> interestRate;

    @FXML private TableColumn<LoanForSaleDTO, Integer> totalAmount;

    @FXML private TableColumn<LoanForSaleDTO, Double> price;

    private AppController mainController;
    private Stage buyLoanStage;
    private String buyerName;

    public void setMainController(AppController mainController, String buyerName){
        this.mainController = mainController;
        this.buyerName = buyerName;
    }

    @FXML
    private Button buyLoanBtn;


    public void initialize() {
        sellerName.setCellValueFactory(new PropertyValueFactory<LoanForSaleDTO, String>("sellerName"));
        loanId.setCellValueFactory(new PropertyValueFactory<LoanForSaleDTO, String>("loanId"));
        category.setCellValueFactory(new PropertyValueFactory<LoanForSaleDTO, String>("category"));
        status.setCellValueFactory(new PropertyValueFactory<LoanForSaleDTO, Loan.Status>("status"));
        principal.setCellValueFactory(new PropertyValueFactory<LoanForSaleDTO, Integer>("principal"));
        paymentInterval.setCellValueFactory(new PropertyValueFactory<LoanForSaleDTO, Integer>("paymentInterval"));
        interestRate.setCellValueFactory(new PropertyValueFactory<LoanForSaleDTO, Integer>("interestRate"));
        totalAmount.setCellValueFactory(new PropertyValueFactory<LoanForSaleDTO, Integer>("principalWithInterest"));
        price.setCellValueFactory(new PropertyValueFactory<LoanForSaleDTO, Double>("priceForSale"));
    }

    public void setItems(){
        sellLoansTable.getItems().clear();

        String finalUrl = HttpUrl
                .parse(Constants.BUY_OFFERED_LOAN)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("setItems in BuyLoanPopUpController")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            System.out.println("setItems in BuyLoanPopUpController response code != 200 :" + response.code())
                    );
                } else {
                    Platform.runLater(() -> {
                        try {
                            String jsonCategories = response.body().string();
                            LoanForSaleDTOList loans =
                                    GSON_INSTANCE.fromJson(jsonCategories, LoanForSaleDTOList.class);
                            sellLoansTable.getItems().setAll(loans.getLoansForSaleDTO());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
            }
        });

    }

    @FXML
    void buyLoan(ActionEvent event) {
        LoanForSaleDTO currLoan = sellLoansTable.getSelectionModel().getSelectedItem();
        if(currLoan==null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR!");
            alert.setHeaderText("Please select a loan to buy!");
            alert.show();
            return;
        }
        else {

            String finalUrl = HttpUrl
                    .parse(Constants.CUSTOMER_CAPITAL)
                    .newBuilder()
                    .build()
                    .toString();
            HttpClientUtil.runAsync(finalUrl, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->
                            System.out.println("ActionPop get customer capital error.")
                    );
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() != 200) {
                        Platform.runLater(() -> {
                            System.out.println("ActionPop get customer capital error code != 200, server side");
                        });
                    } else {
                        Platform.runLater(() -> {
                            String jsonString= null;
                            try {
                                jsonString = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            double capitalFromServer = GSON_INSTANCE.fromJson(jsonString, double.class);
                            if(capitalFromServer >= currLoan.getPriceForSale()){
                                buyLoanHelper(currLoan);
                            }
                            else{
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("ERROR!");
                                alert.setHeaderText("You don't have enough money to buy this loan.");
                                alert.show();
                                return;
                            }

                        });
                    }
                }
            });

        }

    }

    public void buyLoanHelper(LoanForSaleDTO currLoan){
        String finalUrl = HttpUrl
                .parse(Constants.BUY_LOAN)
                .newBuilder()
                .addQueryParameter("loanId", currLoan.getLoanId())
                .addQueryParameter("sellerName", currLoan.getSellerName())
                .addQueryParameter("priceForSale", String.valueOf(currLoan.getPriceForSale()))
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    System.out.println("buyLoanHelper error in BuyLoanPopUpController");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    buyLoanStage.close();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("info!");
                    alert.setHeaderText("Congratulations! You have joined as a lender.");
                    alert.show();

                    mainController.updateCustomerTables();
                    setItems();

                });
            }
        });
    }

    public void showPopup(Parent root) {
        this.buyLoanStage = new Stage();
        Scene scene = new Scene(root, 1000, 600);

        setItems();

        this.buyLoanStage.setScene(scene);
        this.buyLoanStage.show();
    }

}
