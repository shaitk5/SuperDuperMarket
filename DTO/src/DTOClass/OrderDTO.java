package DTOClass;

import market.Coordinates;
import market.SalesUsed;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;

public class OrderDTO implements Serializable {
    private final int SERIAL_NUMBER;
    private final int SUB_ORDER_SERIAL_NUMBER;
    private final int NUMBER_OF_PRODUCTS;
    private final int NUMBER_OF_PRODUCTS_TYPE;
    private final double DISTANCE;
    private final double DELIVERY_PRICE;
    private final double PRODUCTS_PRICE;
    private final String CUSTOMER_NAME;
    private final LocationDTO CUSTOMER_LOCATION;
    private final SalesUsed SALES_USED;
    private final int STORE_SERIAL_NUMBER;
    private final Map<Integer, Double> ID_TO_QUANTITY;
    private final String DATE;

    public OrderDTO(int serialNumber, String date, int subOrderSerialNumber, int numOfProducts, double productsPrice, String customer_name,
                    LocationDTO customerLocation, SalesUsed sales_used, int store_serial_number, Map<Integer, Double> idToQuantity, Coordinates storeLocation, double storePPK) {
        DecimalFormat df = new DecimalFormat("#.##");
        this.SALES_USED = sales_used;
        this.SERIAL_NUMBER = serialNumber;
        this.NUMBER_OF_PRODUCTS = numOfProducts;
        this.DATE = date;
        this.PRODUCTS_PRICE = Double.parseDouble(df.format(productsPrice));
        this.SUB_ORDER_SERIAL_NUMBER = subOrderSerialNumber;
        this.CUSTOMER_NAME = customer_name;
        this.CUSTOMER_LOCATION = customerLocation;
        this.STORE_SERIAL_NUMBER = store_serial_number;
        this.ID_TO_QUANTITY = idToQuantity;
        this.NUMBER_OF_PRODUCTS_TYPE = idToQuantity.size();
        this.DISTANCE = Coordinates.getDistance(storeLocation, customerLocation);
        this.DELIVERY_PRICE = this.DISTANCE * storePPK;
    }

    public double getDistance(){
        return DISTANCE;
    }

    public int getNumOfProductTypes() {
        return ID_TO_QUANTITY.keySet().size();
    }

    public int getNumberOfProducts() {
        return this.NUMBER_OF_PRODUCTS;
    }

    public int getSerialNumber() {
        return SERIAL_NUMBER;
    }

    public String getDate() {
        return DATE;
    }

    public Map<Integer, Double> getIdToQuantity() {
        return ID_TO_QUANTITY;
    }

    public int getSubOrderSerialNumber() {
        return SUB_ORDER_SERIAL_NUMBER;
    }

    public double getProductsPrice() {
        return PRODUCTS_PRICE;
    }

    public int getStoreSerialNumber() {
        return STORE_SERIAL_NUMBER;
    }

    public LocationDTO getCUSTOMER_LOCATION() {
        return CUSTOMER_LOCATION;
    }

    public SalesUsed getSALES_USED() {
        return SALES_USED;
    }

    public String getCUSTOMER_NAME() {
        return CUSTOMER_NAME;
    }

    public int getNUMBER_OF_PRODUCTS_TYPE() {
        return NUMBER_OF_PRODUCTS_TYPE;
    }

    public double getDELIVERY_PRICE() {
        return DELIVERY_PRICE;
    }
}