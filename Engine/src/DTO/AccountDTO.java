package DTO;

import Account.impl.Account;
import Account.impl.Action;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AccountDTO {

    private int accountID;
    private double capital;
    private List<ActionDTO> history = new LinkedList<>();

    public AccountDTO(Account curr) {
        this.capital = curr.getCapital();
        for (Action currAction: curr.getHistory()) {
            ActionDTO newActionDTO = new ActionDTO(currAction);
            history.add(newActionDTO);
        }
    }

    public List<ActionDTO> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        String print  = "Current Account.Account: " + accountID +
                "Capital" + capital + ", History: ";

        for (ActionDTO actionDTO : history) {
            print += actionDTO.toString() + " ";
        }

        return print;
    }

    public double getCapital() {
        return capital;
    }

    public List<String> historyToString(){
        List<String> historyStringList = new ArrayList<>();
        for (ActionDTO curr : this.history) {
            historyStringList.add(curr.toString());
        }
        return historyStringList;
    }
}
