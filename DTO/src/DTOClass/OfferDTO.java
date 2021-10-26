package DTOClass;

public class OfferDTO {
    private final double QUANTITY;
    private final int ID;
    private final double EXTRA_COST;

    public OfferDTO(double quantity, int id, double extraCost) {
        this.QUANTITY = quantity;
        this.ID = id;
        this.EXTRA_COST = extraCost;
    }

    public OfferDTO(OfferDTO offer) {
        this.QUANTITY = offer.getQuantity();
        this.ID = offer.getId();
        this.EXTRA_COST = offer.getExtraCost();
    }

    public double getQuantity() {
        return QUANTITY;
    }

    public int getId() {
        return ID;
    }

    public double getExtraCost() {
        return EXTRA_COST;
    }
}
