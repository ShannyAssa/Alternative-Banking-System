package DTO;

import Account.impl.Action;

public class ActionDTO {
    final char INCOME = '+';
    final char OUTCOME = '-';

    private int yazDate;
    private double sum;
    private char type;
    private double beforeAction;
    private double afterAction;

    public ActionDTO(Action curr) {
        this.yazDate = curr.getYazDate();
        this.sum = curr.getSum();
        this.type = curr.getType();
        this.beforeAction = curr.getBeforeAction();
        this.afterAction = curr.getAfterAction();
    }

    @Override
    public String toString() {
        return type + " " + sum + ", Yaz date: " + yazDate + ", capital before action: " + beforeAction + ", current capital: " + afterAction;
    }
}
