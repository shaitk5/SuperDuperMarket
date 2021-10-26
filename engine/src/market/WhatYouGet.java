package market;

import DTOClass.GetDTO;
import xml.schema.generated.ThenYouGet;
import java.util.ArrayList;
import java.util.List;

public class WhatYouGet {
    private List<Offer> offer;
    private String operator;

    public WhatYouGet(String operator){
        this.operator = operator;
        this.offer = new ArrayList<>();
    }

    public WhatYouGet(ThenYouGet thenYouGet) {
        this.operator = thenYouGet.getOperator();
        this.offer = new ArrayList<>();
        thenYouGet.getSDMOffer().forEach(offer -> this.offer.add(new Offer(offer)));
    }

    public WhatYouGet(GetDTO get) {
        this.operator = get.getOperator();
        this.offer = new ArrayList<>();
        get.getOffer().forEach(offer -> this.offer.add(new Offer(offer)));
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<Offer> getOffers() {
        return offer;
    }

    public void setOffers(List<Offer> sdmOffer) {
        this.offer = sdmOffer;
    }

    public void addOffer(double quantity, int id, double extraCost) {
        this.offer.add(new Offer(quantity, id, extraCost));
    }

    //add remove Offer...
}
