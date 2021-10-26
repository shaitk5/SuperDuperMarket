package DTOClass;

public class BuyDTO {
    private final double QUANTITY;
    private final int ITEM_ID;

    public BuyDTO(double quantity, int item_id) {
        QUANTITY = quantity;
        ITEM_ID = item_id;
    }

    public BuyDTO(BuyDTO buy) {
        this.QUANTITY = buy.QUANTITY;
        this.ITEM_ID = buy.ITEM_ID;
    }

    public double getQuantity() {
        return QUANTITY;
    }

    public int getItemId() {
        return ITEM_ID;
    }
}
