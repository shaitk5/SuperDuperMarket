package market;

import enums.Action;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private double balance = 0;
    private double expenses = 0;
    private List<Transactions> transactions = new ArrayList<>();

    public Wallet(double balance, double expenses) {
        this.balance = balance;
        this.expenses = expenses;
    }

    public Wallet(){}

    public double getBalance() {
        return balance;
    }

    public double getExpenses() {
        return expenses;
    }

    public void addMoney(double money){
        DecimalFormat df = new DecimalFormat("#.##");
        this.balance += money;
        this.balance = Double.parseDouble(df.format(this.balance));
    }

    public void pay(double money){
        DecimalFormat df = new DecimalFormat("#.##");
        this.balance -= money;
        this.expenses += money;
        this.balance = Double.parseDouble(df.format(this.balance));
        this.expenses = Double.parseDouble(df.format(this.expenses));
    }

    public void addTransaction(Transactions action) {
        this.transactions.add(action);
        if(action.getAction() == Action.PAY){
            pay(action.getMoney());
        } else {
            addMoney(action.getMoney());
        }
    }

    public List<Transactions> getTransactions() {
        return this.transactions;
    }
}
