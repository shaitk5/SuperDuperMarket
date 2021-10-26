package market;

import DTOClass.SellDTO;
import enums.Pricing;
import java.text.DecimalFormat;

public class Sell implements Comparable<Sell>{
    private double price;
    private int timesSold = 0;
    private double quantitySold = 0;
    private Product product;


    public Sell(double price, Product product){
        this.price = price;
        this.product = product;
    }

    public Sell(SellDTO sell){
        this.quantitySold = sell.getQuantitySold();
        this.timesSold = sell.getTimesSold();
        this.price = sell.getPRICE();
        this.product = new Product(sell.getProduct());
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void addPrice(double plusOrMinus){
        this.price += plusOrMinus;
    }

    public void addItemSold(double sold){
        this.timesSold += sold;
    }

    public void addQuantitySold(double sold){
        this.quantitySold += sold;
        product.setQuantitySold(product.getQuantitySold() + sold);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getTimesSold() {
        return timesSold;
    }

    public void setTimesSold(int timesSold) {
        this.timesSold = timesSold;
    }

    public double getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(double quantitySold) {
        this.quantitySold = quantitySold;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");
        String type = this.getProduct().getPricing().equals(Pricing.WEIGHT) ? "Average price per kilogram : " : "Average price per product : ";
        String newLine = System.lineSeparator();
        return this.getProduct().getSerialNumber() + ". " + this.getProduct().getName() + "   -   " + type
                + df.format((this.getPrice() / (double) this.getProduct().getNumberOfStoresSelling())) + newLine
                + "Stores selling the product : " + this.getProduct().getNumberOfStoresSelling() + newLine
                + "Total products sold : " + this.getQuantitySold() + newLine;
    }

    @Override
    public int compareTo(Sell o) {
        return this.getProduct().getSerialNumber() - o.getProduct().getSerialNumber();
    }
}
