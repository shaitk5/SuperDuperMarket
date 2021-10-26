package order;

import DTOClass.OrderDTO;
import DTOClass.SaleDTO;
import DTOClass.StoreDTO;
import json.JsonCart;
import java.util.*;

public class OrderManager {

    public Map<SaleDTO, Integer> getRegularOrderSales(Map<Integer, StoreDTO> idToStore, JsonCart cart) {
        Map<SaleDTO, Integer> salesToQuantity = new HashMap<>();

        for (Integer key : cart.getIdToQuantity().keySet()) {
            Set<SaleDTO> sales = idToStore.get(cart.getStore()).getItemSales(key);
            for (SaleDTO sale : sales) {
                double quantity = cart.getIdToQuantity().get(key);
                salesToQuantity.put(sale, (int)(quantity / sale.getBUY().getQuantity()));
            }
        }

        return salesToQuantity;
    }

    public Map<SaleDTO, Integer> getDynamicOrderSales(List<OrderDTO> orders, Map<Integer, StoreDTO> idToStore) {
        Map<SaleDTO, Integer> salesToQuantity = new HashMap<>();

        for (OrderDTO order : orders) {
            Set<SaleDTO> sales;
            StoreDTO store = idToStore.get(order.getStoreSerialNumber());
            for (Integer key : order.getIdToQuantity().keySet()) {
                sales = store.getItemSales(key);
                for (SaleDTO sale : sales) {
                    double quantity = order.getIdToQuantity().get(key);
                    salesToQuantity.put(sale, (int)(quantity / sale.getBUY().getQuantity()));
                }
            }
        }

        return salesToQuantity;
    }

}
