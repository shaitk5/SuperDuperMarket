package json;

public class SalesChosen {
    String saleName;
    int productSerial;

    public SalesChosen(String saleName, int productSerial) {
        this.saleName = saleName;
        this.productSerial = productSerial;
    }

    public String getSaleName() {
        return saleName;
    }

    public int getProductSerial() {
        return productSerial;
    }
}