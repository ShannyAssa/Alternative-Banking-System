package Account.impl;

import java.io.Serializable;

public class Action implements Serializable {
    final char INCOME = '+';
    final char OUTCOME = '-';

    private final int yazDate;
    private final double sum;
    private final char type;
    private final double beforeAction;
    private final double afterAction;

    public Action(int yazDate, double sum, char type, double capital) {
        this.yazDate = yazDate;
        this.sum = sum;
        this.type = type;
        this.beforeAction = capital;
        if(type == INCOME)
            this.afterAction = capital + sum;
        else
            this.afterAction = capital - sum;
    }

    public int getYazDate() {
        return yazDate;
    }

    public double getSum() {
        return sum;
    }

    public char getType() {
        return type;
    }

    public double getBeforeAction() {
        return beforeAction;
    }

    public double getAfterAction() {
        return afterAction;
    }

}
