package json;

import DTOClass.LocationDTO;
import java.util.Map;

public class JsonCart {
        private LocationDTO location;
        private Map<Integer, Double> idToQuantity;
        private String zone;
        private String date;
        private String customer;
        private int store;
        private boolean dynamicOrder;

        public void setIdToQuantity(Map<Integer, Double> idToQuantity) {
                this.idToQuantity = idToQuantity;
        }

        public LocationDTO getLocation() {
                return location;
        }

        public Map<Integer, Double> getIdToQuantity() {
                return idToQuantity;
        }

        public String getZone() {
                return zone;
        }

        public String getDate() {
                return date;
        }

        public String getCustomer() {
                return customer;
        }

        public int getStore() {
                return store;
        }

        public boolean isDynamicOrder() {
                return dynamicOrder;
        }
}
