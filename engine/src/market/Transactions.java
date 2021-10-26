package market;

import enums.Action;

import java.text.DecimalFormat;
import java.util.Date;

public class Transactions {
    private Action action;
    private double money;
    private double moneyBeforeAction;
    private String date;

    public Transactions(Action action, double money, double moneyBeforeAction, String date) {
        DecimalFormat df = new DecimalFormat("#.##");
        this.action = action;
        this.money = Double.parseDouble(df.format(money));
        this.moneyBeforeAction = Double.parseDouble(df.format(moneyBeforeAction));
        this.date = date;
    }

    public Action getAction() {
        return action;
    }

    public double getMoney() {
        return money;
    }

    public String getDate() {
        return date.toString();
    }

    public double getCreditBeforeAction(){
        return this.moneyBeforeAction;
    }

    public double getCreditAfterAction(){
        return this.moneyBeforeAction + this.money;
    }
}
