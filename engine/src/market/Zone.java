package market;

public class Zone {
    private String name;
    private String owner = null;
    private IMarketEngine market;

    public Zone(String name, IMarketEngine market) {
        this.name = name;
        this.market = market;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
        this.market.setOwnerOnNewMarketMade(owner);
    }

    public String getOwner() {
        return owner;
    }

    public IMarketEngine getMarket() {
        return market;
    }
}
