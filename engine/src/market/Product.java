package market;

import DTOClass.ProductDTO;
import enums.Pricing;

public class Product {
    private final String NAME;
    private final Pricing PRICING;
    private final int SERIAL_NUMBER;
    private int numberOfStoresSelling;
    private double quantitySold;

    public Product(String name, int serialNumber, Pricing pricing, int numberOfStoresSelling, double quantitySold){
        this.numberOfStoresSelling = numberOfStoresSelling;
        this.NAME = name;
        this.SERIAL_NUMBER = serialNumber;
        this.PRICING = pricing;
        this.quantitySold = quantitySold;
    }

    public Product(ProductDTO product){
        this.numberOfStoresSelling = product.getNumberOfStoresSelling();
        this.NAME = product.getName();
        this.SERIAL_NUMBER = product.getSerialNumber();
        this.PRICING = product.getPricing().equals("Weight") ? Pricing.WEIGHT : Pricing.PRODUCT;
    }

    public String getName() {
        return NAME;
    }

    public int getSerialNumber() {
        return SERIAL_NUMBER;
    }

    public Pricing getPricing() {
        return PRICING;
    }

    public int getNumberOfStoresSelling() {
        return numberOfStoresSelling;
    }

    public void setNumberOfStoresSelling(int numberOfStoresSelling) {
        this.numberOfStoresSelling = numberOfStoresSelling;
    }

    public double getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(double quantitySold) {
        this.quantitySold = quantitySold;
    }
}
