package market;

import DTOClass.BuyDTO;
import xml.schema.generated.IfYouBuy;

public class WhatYouBuy {
    private double quantity;
    private int itemId;

    public WhatYouBuy(double quantity, int itemId) {
        this.quantity = quantity;
        this.itemId = itemId;
    }

    public WhatYouBuy(IfYouBuy ifYouBuy) {
        this.quantity = ifYouBuy.getQuantity();
        this.itemId = ifYouBuy.getItemId();
    }

    public WhatYouBuy(BuyDTO buy) {
        this.quantity = buy.getQuantity();
        this.itemId = buy.getItemId();
    }

    public double getQuantity() {
        return quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
