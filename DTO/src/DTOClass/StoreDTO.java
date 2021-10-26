package DTOClass;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class StoreDTO implements Serializable {
    private final LocationDTO LOCATION;
    private final String OWNER;
    private final int SERIAL_NUMBER;
    private final double PPK;
    private final double PRODUCTS_INCOME;
    private final double deliveryIncome;
    private final String NAME;
    private final Map<Integer, SellDTO> ID_TO_SELL;
    private final Set<SaleDTO> SALES;
    private final Set<OrderDTO> ORDERS;

    public StoreDTO(String owner, int serialNumber, double PPK, String name, int coordinateX, int coordinateY, Map<Integer, SellDTO> idToSell, Set<SaleDTO> sales, Set<OrderDTO> orders){
        this.SALES = sales;
        this.OWNER = owner;
        this.LOCATION = new LocationDTO(coordinateX, coordinateY);
        this.SERIAL_NUMBER = serialNumber;
        this.PPK = PPK;
        this.NAME = name;
        this.ID_TO_SELL = idToSell;
        this.ORDERS = orders;
        this.PRODUCTS_INCOME = calculateProductsIncome();
        this.deliveryIncome = calculateDeliveryIncome();
    }

    private double calculateDeliveryIncome() {
        DecimalFormat df = new DecimalFormat("#.##");
        double income = 0;
        for(OrderDTO order : ORDERS){
            income += order.getDELIVERY_PRICE();
        }
        return Double.parseDouble(df.format(income));
    }

    public double getPRODUCTS_INCOME() {
        return PRODUCTS_INCOME;
    }

    public double getDELIVERY_INCOME() {
        return deliveryIncome;
    }

    public double calculateProductsIncome(){
        DecimalFormat df = new DecimalFormat("#.##");
        double income = 0;
        for(OrderDTO order : ORDERS){
            income += order.getProductsPrice();
        }
        return Double.parseDouble(df.format(income));
    }

    public double getDeliveryIncome(){
        DecimalFormat df = new DecimalFormat("#.##");
        double income = 0;
        for(OrderDTO order : ORDERS){
            income += order.getDELIVERY_PRICE();
        }
        return Double.parseDouble(df.format(income));
    }

    public int getSerialNumber() {
        return SERIAL_NUMBER;
    }

    public String getName() {
        return NAME;
    }

    public Map<Integer, SellDTO> getIdToSell() {
        return ID_TO_SELL;
    }

    public Set<OrderDTO> getOrders() {
        return ORDERS;
    }

    public double getPPK() {
        return PPK;
    }

    public LocationDTO getLocation(){
        return this.LOCATION;
    }

    @Override
    public String toString() {
        return this.getSerialNumber() + ". " + this.getName();
    }

    public Set<SaleDTO> getSALES() {
        return SALES;
    }

    public Set<SaleDTO> getItemSales(int itemID){
        Set<SaleDTO> salesToReturn = new HashSet<>();
        this.SALES.stream().forEach(sale -> {
            if(sale.getBUY().getItemId() == itemID){
                salesToReturn.add(sale);
            }
        });
        return salesToReturn;
    }

    public SaleDTO containSale(String saleName){
        List<SaleDTO> filtered =  SALES.stream().filter(sale -> sale.getNAME().equals(saleName)).collect(Collectors.toList());
        if(filtered.size() != 0){
            return filtered.get(0);
        }
        return null;
    }

    public List<ProductDTO> getProductsList() {
        List<ProductDTO> products = new ArrayList<>();
        getIdToSell().values().forEach(sell -> products.add(sell.getProduct()));
        return products;
    }

    public String getOWNER() {
        return OWNER;
    }
}
