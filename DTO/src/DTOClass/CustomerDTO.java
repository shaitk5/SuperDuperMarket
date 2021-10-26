package DTOClass;

public class CustomerDTO {
    private final String NAME;
    private final LocationDTO LOCATION;
    private final int NUMBER_OF_ORDERS;
    private final double AVERAGE_DELIVERY_PRICE;
    private final double AVERAGE_PRODUCTS_PRICE;
    //private final Set<OrderDTO> orders;
    private final int ID;

    public CustomerDTO(String NAME, int COORDINATE_X, int COORDINATE_Y, int numberOfOrders, double averageDeliveryPrice, double averageOrderPrice, int id) {
        this.NAME = NAME;
        this.LOCATION = new LocationDTO(COORDINATE_X, COORDINATE_Y);
        this.NUMBER_OF_ORDERS = numberOfOrders;
        this.AVERAGE_DELIVERY_PRICE = averageDeliveryPrice;
        this.AVERAGE_PRODUCTS_PRICE = averageOrderPrice;
        this.ID = id;
        //this.orders = orders;
    }

    public int getID() {
        return ID;
    }

    public double getAVERAGE_PRODUCTS_PRICE() {
        return AVERAGE_PRODUCTS_PRICE;
    }

    public double getAVERAGE_DELIVERY_PRICE() {
        return AVERAGE_DELIVERY_PRICE;
    }

    public int getNUMBER_OF_ORDERS() {
        return NUMBER_OF_ORDERS;
    }

    public String getNAME() {
        return NAME;
    }

    @Override
    public String toString() {
        return ID + ". " + NAME;
    }

    public LocationDTO getLOCATION() {
        return LOCATION;
    }
}
