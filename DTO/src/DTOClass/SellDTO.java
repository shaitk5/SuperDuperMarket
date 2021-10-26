package DTOClass;

import java.text.DecimalFormat;

public class SellDTO {
    private final double PRICE;
    private final int TIMES_SOLD;
    private final double QUANTITY_SOLD;
    private final ProductDTO PRODUCT;

    public SellDTO(double price, int itemSold, double quantitySold, ProductDTO product) {
        this.PRICE = price;
        this.TIMES_SOLD = itemSold;
        this.QUANTITY_SOLD = quantitySold;
        this.PRODUCT = product;
    }

    public double getPRICE() {
        return PRICE;
    }

    public String getNAME(){
        return PRODUCT.getName();
    }

    public int getTimesSold() {
        return TIMES_SOLD;
    }

    public ProductDTO getProduct() {
        return PRODUCT;
    }

    public double getQuantitySold() {
        return QUANTITY_SOLD;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");
        String newLine = System.lineSeparator();
        return this.getProduct().toString() + " : " + df.format(this.getPRICE()) + newLine
                + "Total product sold : " + df.format(this.getQuantitySold()) + newLine + newLine;
    }
}
