package market;

import DTOClass.SaleDTO;
import xml.schema.generated.SDMDiscount;

public class Sale {
    private final String NAME;
    private WhatYouBuy whatYouBuy;
    private WhatYouGet whatYouGet;

    public Sale(String name, WhatYouBuy buy, WhatYouGet get) {
        this.NAME = name;
        this.whatYouBuy = buy;
        this.whatYouGet = get;
    }

    public Sale(SDMDiscount discount) {
        this.NAME = discount.getName();
        whatYouBuy = new WhatYouBuy(discount.getIfYouBuy());
        whatYouGet = new WhatYouGet(discount.getThenYouGet());
    }

    public Sale(SaleDTO sale) {
        this.NAME = sale.getNAME();
        this.whatYouBuy = new WhatYouBuy(sale.getBUY());
        this.whatYouGet = new WhatYouGet(sale.getGET());
    }

    public String getNAME() {
        return NAME;
    }

    public WhatYouBuy getWhatYouBuy() {
        return whatYouBuy;
    }

    public void setWhatYouBuy(WhatYouBuy whatYouBuy) {
        this.whatYouBuy = whatYouBuy;
    }

    public WhatYouGet getWhatYouGet() {
        return whatYouGet;
    }

    public void setWhatYouGet(WhatYouGet whatYouGet) {
        this.whatYouGet = whatYouGet;
    }
}
