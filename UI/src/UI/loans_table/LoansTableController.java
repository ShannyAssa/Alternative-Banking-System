package UI.loans_table;

import Account.impl.Loan;
import DTO.LoanDTO;
import Engine.impl.Engine;
import UI.app.AppController;
import UI.list_view.ListViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class LoansTableController {
    enum ButtonCellType {PARTICIPANTS, PAYMENTS_HISTORY, UNPAID_PAYMENTS}

    @FXML private TableView<LoanDTO> loansTable;

    @FXML private TableColumn<LoanDTO, String> ID;

    @FXML private TableColumn<LoanDTO, String> Owner;

    @FXML private TableColumn<LoanDTO, String> Category;

    @FXML private TableColumn<LoanDTO, Integer> Principal;

    @FXML private TableColumn<LoanDTO, Integer> LoanTerm;

    @FXML private TableColumn<LoanDTO, Integer> InterestRate;

    @FXML private TableColumn<LoanDTO, Integer> PaymentInterval;

    @FXML private TableColumn<LoanDTO, Integer> StartingDate;

    @FXML private TableColumn<LoanDTO, Integer> TerminationDate;

    @FXML private TableColumn<LoanDTO, Integer> NextPayment;

    @FXML private TableColumn<LoanDTO, Loan.Status> Status;

    @FXML private TableColumn<LoanDTO, String> Participants;

    @FXML private TableColumn<LoanDTO, Integer> PendingPrincipal;

    @FXML private TableColumn<LoanDTO, Integer> CollectedPrincipal;

    @FXML private TableColumn<LoanDTO, String> PaymentsHistory;

    @FXML private TableColumn<LoanDTO, Integer> PaidPrincipal;

    @FXML private TableColumn<LoanDTO, Integer> UnpaidPrincipal;

    @FXML private TableColumn<LoanDTO, Double> PaidInterest;

    @FXML private TableColumn<LoanDTO, Double> UnpaidInterest;

    @FXML private TableColumn<LoanDTO, String> UnpaidPayments;


    private AppController mainController;


    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void initialize(){
        ID.setCellValueFactory(new PropertyValueFactory<LoanDTO, String>("loanId"));
        Owner.setCellValueFactory(new PropertyValueFactory<LoanDTO, String>("owner"));
        Category.setCellValueFactory(new PropertyValueFactory<LoanDTO, String>("category"));
        Principal.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("principal"));
        LoanTerm.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("loanTerm"));
        InterestRate.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("interestRate"));
        PaymentInterval.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("paymentInterval"));
        StartingDate.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("startingDate"));
        TerminationDate.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("terminationDate"));
        NextPayment.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("nextRepayment"));
        Status.setCellValueFactory(new PropertyValueFactory<LoanDTO, Loan.Status>("status"));

        Participants.setCellValueFactory(new PropertyValueFactory<LoanDTO, String>("Open"));
        PaymentsHistory.setCellValueFactory(new PropertyValueFactory<LoanDTO, String>("Open"));
        UnpaidPayments.setCellValueFactory(new PropertyValueFactory<LoanDTO, String>("Open"));

        Callback<TableColumn<LoanDTO, String>, TableCell<LoanDTO, String>> participantsButtonCell = setButtonCell(ButtonCellType.PARTICIPANTS);
        Callback<TableColumn<LoanDTO, String>, TableCell<LoanDTO, String>> historyButtonCell = setButtonCell(ButtonCellType.PAYMENTS_HISTORY);
        Callback<TableColumn<LoanDTO, String>, TableCell<LoanDTO, String>> unpaidButtonCell = setButtonCell(ButtonCellType.UNPAID_PAYMENTS);

        Participants.setCellFactory(participantsButtonCell);
        PaymentsHistory.setCellFactory(historyButtonCell);
        UnpaidPayments.setCellFactory(unpaidButtonCell);

        PendingPrincipal.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("pendingPrincipal"));
        CollectedPrincipal.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("collectedPrincipal"));
        PaidPrincipal.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("paidPrincipal"));
        UnpaidPrincipal.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("unpaidPrincipal"));
        PaidInterest.setCellValueFactory(new PropertyValueFactory<LoanDTO, Double>("paidInterest"));
        UnpaidInterest.setCellValueFactory(new PropertyValueFactory<LoanDTO, Double>("unpaidInterest"));
    }


    public void setItems(Map<String, LoanDTO> loans){
        loansTable.getItems().clear();
        loansTable.getItems().setAll(loans.values());
    }

    public Callback<TableColumn<LoanDTO, String>, TableCell<LoanDTO, String>> setButtonCell(ButtonCellType type){
        Callback<TableColumn<LoanDTO, String>, TableCell<LoanDTO, String>> cellFactory
                = //
                new Callback<TableColumn<LoanDTO, String>, TableCell<LoanDTO, String>>() {
                    @Override
                    public TableCell call(final TableColumn<LoanDTO, String> param) {
                        final TableCell<LoanDTO, String> cell = new TableCell<LoanDTO, String>() {

                            final Button btn = new Button("Open");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        LoanDTO loan = getTableView().getItems().get(getIndex());
                                        FXMLLoader fxmlLoader = new FXMLLoader();
                                        URL url = getClass().getResource("/UI/list_view/list_view.fxml");
                                        fxmlLoader.setLocation(url);
                                        try {
                                            Parent listView = fxmlLoader.load(url.openStream());
                                            ListViewController listViewController = fxmlLoader.getController();
                                            listViewController.setMainController(mainController);

                                            Scene scene = new Scene(listView, 450, 400);
                                            Stage listStage = new Stage();
                                            listStage.setScene(scene);

                                            if(type.equals(ButtonCellType.PARTICIPANTS)){
                                                listViewController.getListView().getItems().addAll(loan.participantsToString());
                                                listStage.setTitle("Participants");
                                            }
                                            else if(type.equals(ButtonCellType.PAYMENTS_HISTORY)){
                                                listViewController.getListView().getItems().addAll(loan.paymentsHistoryToString());
                                                listStage.setTitle("Payments History");
                                            }
                                            else if(type.equals(ButtonCellType.UNPAID_PAYMENTS)){
                                                listViewController.getListView().getItems().addAll(loan.unpaidPaymentsToString());
                                                listStage.setTitle("Unpaid Payments");
                                            }
                                            if(listViewController.getListView().getItems().isEmpty())
                                                listViewController.getListView().getItems().add("There is no data to present.");


                                            listStage.show();
                                        } catch (IOException e) {
                                            e.getMessage();
                                        }

                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        return cellFactory;
    }

    public LoanDTO getSelectedRowFromTable(){
        return loansTable.getSelectionModel().getSelectedItem();
    }
}
