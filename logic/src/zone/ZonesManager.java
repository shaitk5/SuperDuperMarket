package zone;

import DTOClass.StoreDTO;
import exception.DuplicateException;
import json.JsonStore;
import market.Zone;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ZonesManager {
    private final Map<String, Zone> zones;

    public ZonesManager() {
        this.zones = new HashMap<>();
    }

    public synchronized void addZone(Zone newZone) throws DuplicateException {
        if(!zones.containsKey(newZone.getName())){
            zones.put(newZone.getName(), newZone);
        } else {
            throw new DuplicateException("There are duplicate zones");
        }
    }

    public synchronized Zone getZone(String zone) {
        return zones.getOrDefault(zone, null);
    }

    public int getVersion() {
        return zones.size();
    }

    public synchronized List<Zone> getZoneEntries() {
        return zones.values().stream().collect(Collectors.toList());
    }

    public int getFreeSerialNumber(String zone) {
        Zone zoneToCheck = getZone(zone);
        return zoneToCheck.getMarket().getFreeStoreSerial();
    }

    public synchronized void addStore(JsonStore store, String zoneName) {
        Zone zone = getZone(zoneName);
        zone.getMarket().addStore(store);
    }
}
