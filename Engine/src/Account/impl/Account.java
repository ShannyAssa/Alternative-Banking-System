package Account.impl;

import Account.AccountInterface;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Account implements AccountInterface, Serializable {

    final char INCOME = '+';
    final char OUTCOME = '-';

    private double capital;
    private List<Action> history = new LinkedList<>();

    public Account(double capital) {
        this.capital = capital;
    }

    public double getCapital() {
        return capital;
    }

    public List<Action> getHistory() {
        return history;
    }

    @Override
    public void depositMoney(int sum, int yaz){
        history.add(new Action(yaz, sum, INCOME, capital));
        capital += sum;
    }

    /**
     * same description as the previous method
     * */
    public void depositMoneyDouble(double sum, int yaz){
        history.add(new Action(yaz, sum, INCOME, capital));
        capital += sum;
    }

    @Override
    public void moneyWithdrawal(double sum, int yaz){
        history.add(new Action(yaz, sum, OUTCOME, capital));
        capital -= sum;
    }


}
