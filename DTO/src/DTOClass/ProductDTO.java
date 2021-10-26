package DTOClass;

public class ProductDTO {
    private final String NAME;
    private final String PRICING;
    private final int SERIAL_NUMBER;
    private final double quantitySold;
    private final double AVERAGE_PRICE;
    private final int NUMBER_OF_STORES_SELLING;

    public ProductDTO(int serialNumber, int numberOfStoresSelling, String name, String pricing, double quantitySold, double averagePrice){
        this.NUMBER_OF_STORES_SELLING = numberOfStoresSelling;
        this.SERIAL_NUMBER = serialNumber;
        this.NAME = name;
        this.PRICING = pricing;
        this.quantitySold = quantitySold;
        this.AVERAGE_PRICE = averagePrice;
    }

    public int getSerialNumber() {
        return SERIAL_NUMBER;
    }

    public String getName() {
        return NAME;
    }

    public String getPricing() {
        return PRICING;
    }

    public int getNumberOfStoresSelling() {
        return NUMBER_OF_STORES_SELLING;
    }

    @Override
    public String toString() {
        return this.getSerialNumber() + ". " + this.getName();
    }

    public double getQuantitySold() {
        return quantitySold;
    }

}
