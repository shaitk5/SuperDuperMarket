package DTOClass;

public class SaleDTO {
    private final String NAME;
    private final BuyDTO BUY;
    private final GetDTO GET;

    public SaleDTO(String NAME, BuyDTO buy, GetDTO whatYouGet) {
        this.NAME = NAME;
        this.BUY = buy;
        this.GET = whatYouGet;
    }

    public SaleDTO(SaleDTO sale) {
        this.NAME = sale.getNAME();
        this.BUY = new BuyDTO(sale.getBUY());
        this.GET = new GetDTO(sale.getGET());
    }

    public String getNAME() {
        return NAME;
    }

    public GetDTO getGET() {
        return GET;
    }

    public BuyDTO getBUY() {
        return BUY;
    }
}
