package market;

import DTOClass.SaleDTO;
import enums.Pricing;
import jdk.internal.org.objectweb.asm.Handle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SubOrder {
    private final int SERIAL_NUMBER;
    private final SalesUsed SALES_USED = new SalesUsed();
    private final Map<Integer, Double> ID_TO_QUANTITY;
    private final double PRODUCTS_PRICE;
    private final double DELIVERY_PRICE;
    private final Order MAIN_ORDER;
    private final Store STORE;

    public SubOrder(Map<Integer, Double> idToQuantity, int serialNumber, SalesUsed salesUsed, double productsPrice, double deliveryPrice, Order mainOrder, Store store) {
        this.STORE = store;
        this.SERIAL_NUMBER = serialNumber;
        this.DELIVERY_PRICE = deliveryPrice;
        this.MAIN_ORDER = mainOrder;
        this.ID_TO_QUANTITY = idToQuantity;
        addSales(salesUsed);
        this.PRODUCTS_PRICE = productsPrice;
    }

    private void addSales(SalesUsed salesUsed) {
        for(String saleName : salesUsed.getSaleNameToItemPick().keySet()){
            Sale sale = this.STORE.containSale(saleName);
            if(sale != null){
                this.SALES_USED.getSaleNameToItemPick().put(saleName, salesUsed.getSaleNameToItemPick().get(saleName));
            }
        }

        for(String saleName : salesUsed.getAllOrNothingSaleCounter().keySet()){
            Sale sale = this.STORE.containSale(saleName);
            if(sale != null){
                this.SALES_USED.getAllOrNothingSaleCounter().put(saleName, salesUsed.getAllOrNothingSaleCounter().get(saleName));
            }
        }
    }

    public SalesUsed getSALES_USED() {
        return SALES_USED;
    }

    public int getNumOfProducts(IMarketEngine market) {
        int totalProducts = 0;

        for (Integer id : getIdToQuantity().keySet()) {
            if (market.getProductPricing(id).equals(Pricing.PRODUCT)) {
                totalProducts += getIdToQuantity().get(id);
            } else {
                totalProducts += 1;
            }
        }

        int saleTotalProducts = SALES_USED.getNumOfProducts(this.STORE);

        return totalProducts + saleTotalProducts;
    }

    public static int getNumOfProducts(IMarketEngine market, Collection<Integer> itemID, Map<Integer, Double> idToQuantityStore) {
        int totalProducts = 0;

        for (Integer id : idToQuantityStore.keySet()) {
            if (market.getProductPricing(id).equals(Pricing.PRODUCT)) {
                totalProducts += idToQuantityStore.get(id);
            } else {
                totalProducts += 1;
            }
        }

        return totalProducts;
    }

    public Map<Integer, Double> getIdToQuantity() {
        return ID_TO_QUANTITY;
    }

    public int getSerialNumber() {
        return SERIAL_NUMBER;
    }

    public double getProductsPrice() {
        return PRODUCTS_PRICE;
    }

    public double getDeliveryPrice() {
        return DELIVERY_PRICE;
    }

    public Order getMainOrder() {
        return MAIN_ORDER;
    }

    public Store getStore() {
        return STORE;
    }

    public int getNumberOfProductsTypes(IMarketEngine market) {
        Set<Integer> productsIds = new HashSet<>();
        ID_TO_QUANTITY.keySet().forEach(key -> productsIds.add(key));
        SALES_USED.getSaleNameToItemPick().values().forEach(list ->{
            list.forEach(id -> productsIds.add(id));
        });
        SALES_USED.getAllOrNothingSaleCounter().keySet().forEach(saleName -> {
            SaleDTO sale = market.getSale(saleName);
            sale.getGET().getOffer().forEach(offer -> {
                productsIds.add(offer.getId());
            });
        });
        return productsIds.size();
    }
}
