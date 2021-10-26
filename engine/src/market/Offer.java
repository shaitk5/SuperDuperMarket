package market;

import DTOClass.OfferDTO;
import xml.schema.generated.SDMOffer;

public class Offer {
    private double quantity;
    private int id;
    private double extraCost;

    public Offer(double quantity, int id, double extraCost){
        this.quantity = quantity;
        this.id = id;
        this.extraCost = extraCost;
    }

    public Offer(SDMOffer offer) {
        this.quantity = offer.getQuantity();
        this.extraCost = offer.getForAdditional();;
        this.id = offer.getItemId();
    }

    public Offer(OfferDTO offer) {
        this.quantity = offer.getQuantity();
        this.id = offer.getId();
        this.extraCost = offer.getExtraCost();
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getExtraCost() {
        return extraCost;
    }

    public void setExtraCost(double extraCost) {
        this.extraCost = extraCost;
    }
}
