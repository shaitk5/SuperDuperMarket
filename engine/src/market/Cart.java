package market;

import DTOClass.*;
import exception.InvalidActionException;

import java.util.HashMap;
import java.util.Map;

public class Cart {

    private final Map<Integer, Double> idToQuantity;
    private LocationDTO customerLocation;
    private String customerName;
    private int storeSerial;
    private String date;
    private boolean dynamicOrder;

    public Cart(LocationDTO customerLocation, String customerName, int storeSerial, String date) {
        this.customerLocation = customerLocation;
        this.idToQuantity = new HashMap<>();
        this.customerName = customerName;
        this.storeSerial = storeSerial;
        this.date = date;
        this.dynamicOrder = storeSerial <= 0;
    }

    public Cart(String customerName, int storeSerial, String date, Map<Integer, Double> idToQuantity, LocationDTO customerLocation) {
        this.idToQuantity = idToQuantity;
        this.customerName = customerName;
        this.storeSerial = storeSerial;
        this.date = date;
        this.dynamicOrder = storeSerial <= 0;
        this.customerLocation = customerLocation;
    }

    public Map<Integer, Double> getIdToQuantity() {
        return idToQuantity;
    }

    public String getCustomer() {
        return this.customerName;
    }

    public void setCustomer(String customerSerial) {
        this.customerName = customerSerial;
    }

    public int getStore() {
        return this.storeSerial;
    }

    public void setStore(int storeSerial) {
        this.storeSerial = storeSerial;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addProducts(SellDTO selectedCells, Double value) throws InvalidActionException {
        if(selectedCells.getProduct().getPricing().equals("Product") && (value % 1 != 0)){
            throw new InvalidActionException("Not an integer");
        }
        int key = selectedCells.getProduct().getSerialNumber();
        if(idToQuantity.containsKey(selectedCells.getProduct().getSerialNumber())){
            idToQuantity.put(key, idToQuantity.get(key) +  value);
        } else {
            idToQuantity.put(key, value);
        }
    }

    public boolean isDynamicOrder(){
        return dynamicOrder;
    }

    public LocationDTO getCustomerLocation() {
        return customerLocation;
    }
}
