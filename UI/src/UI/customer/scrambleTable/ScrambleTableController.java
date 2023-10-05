package UI.customer.scrambleTable;

import Account.impl.Loan;
import DTO.LoanDTO;
import UI.app.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ScrambleTableController {
    @FXML private TableView<LoanDTO> scrambleTable;

    @FXML private TableColumn<LoanDTO, String> loanIdCol;

    @FXML private TableColumn<LoanDTO, String> categoryCol;

    @FXML private TableColumn<LoanDTO, Loan.Status> statusCol;

    @FXML private TableColumn<LoanDTO, Integer> principalColumn;

    @FXML private TableColumn<LoanDTO, Integer> paymentIntervalCol;

    @FXML private TableColumn<LoanDTO, Integer> interestRateCol;

    @FXML private TableColumn<LoanDTO, Integer> totalAmountCol;

    @FXML private TableColumn<LoanDTO, Boolean> selectLoanCol;

    private AppController mainController;

    public void setMainController(AppController mainController){
        this.mainController = mainController;
    }

    public void initialize() {
        loanIdCol.setCellValueFactory(new PropertyValueFactory<LoanDTO, String>("loanId"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<LoanDTO, String>("category"));
        statusCol.setCellValueFactory(new PropertyValueFactory<LoanDTO, Loan.Status>("status"));
        principalColumn.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("principal"));
        paymentIntervalCol.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("paymentInterval"));
        interestRateCol.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("interestRate"));
        totalAmountCol.setCellValueFactory(new PropertyValueFactory<LoanDTO, Integer>("principalWithInterest"));
        selectLoanCol.setCellValueFactory(new PropertyValueFactory<>("isLoanCheckedInScrambleTable"));
    }

    public void setItems(ArrayList<LoanDTO> loans){
        scrambleTable.getItems().clear();
        scrambleTable.getItems().setAll(loans);
        selectLoanCol.setCellFactory(tc -> new CheckBoxTableCell<>());
        selectLoanCol.setEditable(true);
    }

    public void clearScrambleTable(){
        scrambleTable.getItems().clear();
    }


    public List<LoanDTO> getCheckedLoans(){
        List<LoanDTO> checkedLoans = new LinkedList<>();
        for (LoanDTO curr : scrambleTable.getItems()) {
            if(curr.isLoanCheckedInScrambleTableProperty().getValue()){
                checkedLoans.add(curr);
            }
        }
        return checkedLoans;
    }
}


