package json;

import market.Coordinates;
import java.util.Map;

public class JsonStore {
    final private String name;
    final private String owner;
    final private double ppk;
    final private Coordinates location;
    private Map<Integer, Double> idToPrice;

    JsonStore(String name, double ppk, Coordinates location, Map<Integer, Double> idToPrice, String owner) {
        this.name = name;
        this.owner = owner;
        this.ppk = ppk;
        this.location = location;
        this.idToPrice = idToPrice;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public double getPpk() {
        return ppk;
    }

    public Coordinates getLocation() {
        return location;
    }

    public Map<Integer, Double> getIdToPrice() {
        return idToPrice;
    }

    public void setIdToPrice(Map<Integer, Double> idToPrice) {
        this.idToPrice = idToPrice;
    }
}
