package UI.customer.actionPopUp;

import DTO.LoanActionDTO;
import DTO.LoanDTO;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static UI.app.login.constants.Constants.GSON_INSTANCE;

public class ActionPopUpController {
    public enum submitType{DEPOSIT, WITHDRAW, PAYBACK}

    @FXML private TextField capitalTextField;
    @FXML private TextField amountTextField;
    @FXML private Label actionLabel;
    @FXML private Button actionSubmitBtn;
    private AppController mainController;
    private submitType type;
    private String customerName;
    private Stage actionStage;
    private LoanDTO loanToPay;
    private double capital;


    public void setMainController(AppController mainController, submitType type, String labelData, String customerName){
        this.mainController = mainController;
       // this.engine = engine;
        this.type = type;
        this.actionLabel.setText(labelData);
        this.customerName = customerName;
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
                        capitalTextField.setText(String.valueOf(capitalFromServer));
                        amountTextField.clear();
                        capital = capitalFromServer;
                    });
                }
            }
        });


    }

    public void setLoan(LoanDTO loanToPay){
        this.loanToPay = loanToPay;
    }

    public void showPopup(Parent root){
        this.actionStage = new Stage();
        Scene scene = new Scene(root, 600, 400);

        if(type.equals(ActionPopUpController.submitType.WITHDRAW)){
            this.actionStage.setTitle("Withdrawal");
        }
        else if (type.equals(submitType.DEPOSIT)){
            this.actionStage.setTitle("Deposit");
        }
        else if (type.equals(submitType.PAYBACK)){
            this.actionStage.setTitle("Investment");
        }

        this.actionStage.setScene(scene);
        this.actionStage.show();
    }

    @FXML
    public void submit(ActionEvent event) {
        try {
            if(this.type == submitType.DEPOSIT){
                  this.submitPaymentServer(submitType.DEPOSIT, Integer.parseInt(amountTextField.getText()));
            }
            else if(this.type == submitType.WITHDRAW){
                int amountToPay = Integer.parseInt(amountTextField.getText());
                if(amountToPay > this.capital){
                    throw new NumberFormatException();
                }
                else{
                    this.submitPaymentServer(submitType.WITHDRAW, Integer.parseInt(amountTextField.getText()));
                }

            }
            else{
                int amountToPay = Integer.parseInt(amountTextField.getText());
                getTotalUnpaidPayment(loanToPay, amountToPay);
            }

            this.actionStage.close();
        }
        catch (NumberFormatException e){
            e.getMessage();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid input");
            if(this.type == submitType.WITHDRAW){
                alert.setContentText("The value must not exceed the capital.");
            }
            else if(this.type == submitType.PAYBACK){
                alert.setContentText("The value must not exceed the capital and by the debt amount.");
            }
            alert.setHeaderText("Please enter only integer value.");
            alert.show();
        }

    }

    public void payBackHelperServer(){

    }

    public void getTotalUnpaidPayment(LoanDTO loanToPay, int amountToPay){
        String finalUrl = HttpUrl
                .parse(Constants.FETCH_YAZ)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("getTotalUnpaidPayment get customer capital error.")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        System.out.println("getTotalUnpaidPayment get customer capital error code != 200, server side");
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            double total = 0;
                            for (LoanActionDTO curr : loanToPay.getUnpaidPayments()){
                                total += curr.getTotalPayment();
                            }
                            String jsonYaz = response.body().string();
                            int yaz = GSON_INSTANCE.fromJson(jsonYaz, int.class);
                            if(loanToPay.getNextRepayment() == yaz){
                                total += loanToPay.getSinglePayment();
                            }
                            if(amountToPay > capital || amountToPay > total){
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Invalid input");
                                alert.setContentText("The value must not exceed the capital and by the debt amount.");
                                alert.setHeaderText("Please enter only integer value.");
                                alert.show();
                                return;
                            }
                            else{
                                submitPaymentServer(submitType.PAYBACK, Integer.parseInt(amountTextField.getText()));
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
            }
        });

    }


    public void submitPaymentServer(submitType type, int amount) {

        try{
            String finalUrl = generateFinalUrl(type, amount);
            HttpClientUtil.runAsync(finalUrl, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> {
                        System.out.println("deposit/withdraw/payment error");
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Platform.runLater(() -> {
                        try {
                            String jsonTransactionInfo = response.body().string();
                            List<String> actionList = GSON_INSTANCE.fromJson(jsonTransactionInfo, List.class);
                            mainController.updateCustomerActionList(actionList);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String generateFinalUrl(submitType type, int amount){
        HttpUrl finalUrlHttp;
        if(type.equals( submitType.PAYBACK)){
            finalUrlHttp = HttpUrl
                    .parse(Constants.TRANSACTION_PAGE)
                    .newBuilder()
                    .addQueryParameter("username", customerName)
                    .addQueryParameter("type", String.valueOf(type))
                    .addQueryParameter( "amount", String.valueOf(amount))
                    .addQueryParameter( "loanId", String.valueOf(loanToPay.getLoanId()))
                    .build();
        }
        else{
            finalUrlHttp = HttpUrl
                    .parse(Constants.TRANSACTION_PAGE)
                    .newBuilder()
                    .addQueryParameter("username", customerName)
                    .addQueryParameter("type", String.valueOf(type))
                    .addQueryParameter( "amount", String.valueOf(amount))
                    .build();
        }

        return finalUrlHttp.toString();
    }

}
