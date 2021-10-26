package market;

import DTOClass.LocationDTO;

import java.util.HashSet;
import java.util.Set;

public class Order {
    public static int serialNumberCounter = 1;
    private final String CUSTOMER_NAME;
    private final Coordinates CUSTOMER_LOCATION;
    private final int SERIAL_NUMBER;
    private final Set<SubOrder> SUB_ORDERS = new HashSet<>();
    private final SalesUsed SALES_USED;
    private String date;

    Order(String customerName, SalesUsed salesUsed, String date, Coordinates customerLocation) {
        this.SALES_USED = salesUsed;
        this.CUSTOMER_LOCATION = customerLocation;
        this.CUSTOMER_NAME = customerName;
        this.SERIAL_NUMBER = serialNumberCounter++;
        this.date = date;
    }

    Order(String customerName, SalesUsed salesUsed, String date, LocationDTO customerLocation) {
        this.SALES_USED = salesUsed;
        this.CUSTOMER_LOCATION = new Coordinates(customerLocation);
        this.CUSTOMER_NAME = customerName;
        this.SERIAL_NUMBER = serialNumberCounter++;
        this.date = date;
    }

    public int getNumberOfProductTypes(){
        return SUB_ORDERS.stream().mapToInt(order -> order.getIdToQuantity().keySet().size()).sum();
    }

    public double getProductsPrice() {
        return SUB_ORDERS.stream().mapToDouble(SubOrder::getProductsPrice).sum();
    }

    public double getDeliveryPrice() {
        return SUB_ORDERS.stream().mapToDouble(SubOrder::getDeliveryPrice).sum();
    }

    public double getTotalPrice() {
        return this.getDeliveryPrice() + this.getProductsPrice();
    }

    public int getSerialNumber() {
        return SERIAL_NUMBER;
    }

    public Set<SubOrder> getSubOrders() {
        return SUB_ORDERS;
    }

    public void addSubOrder(SubOrder subOrder){
        this.SUB_ORDERS.add(subOrder);
    }

    public SalesUsed getSALES_USED() {
        return SALES_USED;
    }

    public String getCUSTOMER_NAME() {
        return CUSTOMER_NAME;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Coordinates getCUSTOMER_LOCATION() {
        return CUSTOMER_LOCATION;
    }
}


