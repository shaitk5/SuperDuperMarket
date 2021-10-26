package market;

import java.util.List;

public class Customer {
    protected final Wallet WALLET;
    protected static int serialCounter = 1;
    protected final String NAME;
    protected int ID;

    public Customer(String name) {
        this.NAME = name;
        this.ID = serialCounter++;
        this.WALLET = new Wallet();
    }

    public String getName() {
        return this.NAME;
    }

    public int getId() {
        return this.ID;
    }

    public synchronized Wallet getWallet() {
        return this.WALLET;
    }

    public Updates getUpdates() {
        return null;
    }

    public List<Transactions> getTransactions() {
        return this.WALLET.getTransactions();
    }

    public void addFeedback(Feedback feedback){
    }

    public List<Feedback> getFeedbacksEntries(int feedbackVersion) {
        return null;
    }

    public int getFeedbacksVersion() {
        return 0;
    }
}
