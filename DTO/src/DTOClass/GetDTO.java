package DTOClass;

import java.util.ArrayList;
import java.util.List;

public class GetDTO {
    private final List<OfferDTO> Offer;
    private final String operator;

    public GetDTO(List<OfferDTO> offer, String operator) {
        Offer = offer;
        this.operator = operator;
    }

    public GetDTO(GetDTO get) {
        this.operator = get.getOperator();
        this.Offer = new ArrayList<>();
        get.Offer.forEach(offer ->{
            this.Offer.add(new OfferDTO(offer));
        });
    }

    public List<OfferDTO> getOffer() {
        return Offer;
    }

    public String getOperator() {
        return operator;
    }
}
