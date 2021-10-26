package enums;

public enum Pricing {
    WEIGHT, PRODUCT;


    @Override
    public String toString() {
        if(this.equals(WEIGHT)){
            return "Weight";
        }
        return "Product";
    }
}
