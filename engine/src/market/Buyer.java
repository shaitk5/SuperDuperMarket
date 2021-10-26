package market;

import java.util.HashSet;
import java.util.Set;

public class Buyer extends Customer{
    private final String TYPE = "Buyer";
    private final Set<Order> ORDERS = new HashSet<>();

    public Buyer(String name) {
        super(name);
    }

    public Set<Order> getOrders() {
        return this.ORDERS;
    }

    public void addOrderDetails(Order newOrder) {
        this.ORDERS.add(newOrder);
    }

    public void addMoney(double money){
        this.WALLET.addMoney(money);
    }
}
