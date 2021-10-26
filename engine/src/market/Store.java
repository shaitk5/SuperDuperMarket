package market;

import xml.schema.generated.SDMDiscount;
import xml.schema.generated.SDMStore;
import java.util.*;
import java.util.stream.Collectors;

public class Store {
    private String owner;
    private final int SERIAL_NUMBER;
    private final String NAME;
    private final Map<Integer, Sell> ID_TO_SELL = new HashMap<>();
    private final Set<SubOrder> ORDERS = new HashSet<>();
    private final Set<Sale> SALES;
    private double deliveryPPK;
    private Coordinates coordinates;

    public Store(String name, int serialNumber, double ppk, int x, int y, Set<Sale> sales, String owner) {
        this.NAME = name;
        this.owner = owner;
        this.SERIAL_NUMBER = serialNumber;
        this.deliveryPPK = ppk;
        this.coordinates = new Coordinates(x, y);
        if(sales != null){
            this.SALES = sales;
        } else {
             this.SALES = new HashSet<>();
        }
    }

    public Store() {
        this.owner = " ";
        this.SERIAL_NUMBER = 0;
        this.NAME = " ";
        this.SALES = new HashSet<>();
    }

    public Store(SDMStore store) {
        this.NAME = store.getName();
        this.SERIAL_NUMBER = store.getId();
        this.deliveryPPK = store.getDeliveryPpk();
        this.coordinates = new Coordinates(store.getLocation());
        this.SALES = new HashSet<>();
        if(store.getSDMDiscounts() != null) {
            for (SDMDiscount discount : store.getSDMDiscounts().getSDMDiscount()) {
                SALES.add(new Sale(discount));
            }
        }
    }

    public String getName() {
        return NAME;
    }

    public Map<Integer, Sell> getIdToSell() {
        return this.ID_TO_SELL;
    }

    public int getSerialNumber() {
        return SERIAL_NUMBER;
    }

    public Set<SubOrder> getOrders() {
        return ORDERS;
    }

    public double getDeliveryPPK() {
        return deliveryPPK;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void addProduct(int id, Sell sell) {
        ID_TO_SELL.put(id, sell);
    }

    public void addOrder(SubOrder orderToAdd) {
        ORDERS.add(orderToAdd);
    }

    public Set<Sale> getSALES() {
        return SALES;
    }

    public Sale containSale(String saleName) {
        List<Sale> filtered = SALES.stream().filter(sale -> sale.getNAME().equals(saleName)).collect(Collectors.toList());
        if (filtered.size() != 0) {
            return filtered.get(0);
        }
        return null;
    }

    public void addSale(Sale sale) {
        this.SALES.add(sale);
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}