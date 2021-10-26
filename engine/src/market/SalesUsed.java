package market;

import json.SalesChosen;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class SalesUsed implements Serializable {

    private final Map<String, List<Integer>> saleNameToItemPick = new HashMap<>();
    private final Map<String, Integer> allOrNothingSaleCounter = new HashMap<>();
    private final Map<Integer, Double> idToProductSold = new HashMap<>();

    public SalesUsed(){};

    public SalesUsed(List<SalesChosen> salesChosen) {
        salesChosen.forEach(sale -> {
            //if the sale is all or nothing.
            if (sale.getProductSerial() == 0){
                int quantity = allOrNothingSaleCounter.getOrDefault(sale.getSaleName(), 0);
                allOrNothingSaleCounter.put(sale.getSaleName(), quantity + 1);
            } else {
                List<Integer> itemPickList = saleNameToItemPick.getOrDefault(sale.getSaleName(), null);
                if(itemPickList == null) {
                    itemPickList = new ArrayList<>();
                }
                itemPickList.add(sale.getProductSerial());
                saleNameToItemPick.put(sale.getSaleName(), itemPickList);
            }
        });
    }

    public void addDataToStoreSell(Store store, Set<Integer> productsAddedFromCart) {
        for(String saleName : getSaleNameToItemPick().keySet()){
            Sale sale = store.containSale(saleName);
            if(sale != null){
                sale.getWhatYouGet().getOffers().forEach(offer -> {
                    if(!productsAddedFromCart.contains(offer.getId()) && saleNameToItemPick.get(saleName).contains(offer.getId())) {
                        store.getIdToSell().get(offer.getId()).setTimesSold(store.getIdToSell().get(offer.getId()).getTimesSold() + 1);
                    }
                    if(saleNameToItemPick.get(saleName).contains(offer.getId())){
                        store.getIdToSell().get(offer.getId()).addQuantitySold(saleNameToItemPick.get(saleName).stream().filter(productChoose ->
                                productChoose == offer.getId()).count() * offer.getQuantity());
                    }
                });
            }
        }
        for(String saleName : allOrNothingSaleCounter.keySet()){
            Sale sale = store.containSale(saleName);
            if(sale != null){
                sale.getWhatYouGet().getOffers().forEach(offer -> {
                    if(!productsAddedFromCart.contains(offer.getId())) {
                        store.getIdToSell().get(offer.getId()).setTimesSold(store.getIdToSell().get(offer.getId()).getTimesSold() + 1);
                    }
                    store.getIdToSell().get(offer.getId()).addQuantitySold(allOrNothingSaleCounter.get(saleName) * offer.getQuantity());
                });
            }
        }
    }

    public Map<Integer, Double> getIdToProductSold() {
        return idToProductSold;
    }

    public Map<String, List<Integer>> getSaleNameToItemPick() {
        return saleNameToItemPick;
    }

    public Map<String, Integer> getAllOrNothingSaleCounter() {
        return allOrNothingSaleCounter;
    }

    public double getProductsPrice(Store store) {
        double price = 0;
        for (String saleName : allOrNothingSaleCounter.keySet()) {
            Sale sale = store.containSale(saleName);
            if (sale != null) {
                int counter = allOrNothingSaleCounter.get(saleName);
                price += sale.getWhatYouGet().getOffers().stream().mapToDouble(Offer::getExtraCost).sum() * counter;
            }
        }
        for (String saleName : saleNameToItemPick.keySet()) {
            Sale sale = store.containSale(saleName);
            Map<Integer, Integer> productIdToQuantity = new HashMap<>();
            saleNameToItemPick.get(saleName).forEach(pick -> productIdToQuantity.put(pick, productIdToQuantity.getOrDefault(pick, 0) + 1));
            for (Integer key : productIdToQuantity.keySet()) {
                int counter = productIdToQuantity.get(key);
                Offer offer = sale.getWhatYouGet().getOffers().stream().filter(Offer -> Offer.getId() == key).collect(Collectors.toList()).get(0);
                price += offer.getExtraCost() * counter;
            }
        }
        return price;
    }

    public int getNumOfProducts(Store store) {
        final int[] numOfProducts = {0};
        for (String saleName : allOrNothingSaleCounter.keySet()) {
            Sale sale = store.containSale(saleName);
            if (sale != null) {
                int counter = allOrNothingSaleCounter.get(saleName);
                sale.getWhatYouGet().getOffers().stream().forEach(offer -> {
                    if(store.getIdToSell().get(offer.getId()).getProduct().getPricing().toString().equals("Product")){
                        numOfProducts[0] += offer.getQuantity() * counter;
                    } else {
                        numOfProducts[0] ++;
                    }});
            }
        }
        for (String saleName : saleNameToItemPick.keySet()) {
            Sale sale = store.containSale(saleName);
            Map<Integer, Integer> productIdToQuantity = new HashMap<>();
            saleNameToItemPick.get(saleName).forEach(pick -> productIdToQuantity.put(pick, productIdToQuantity.getOrDefault(pick, 0) + 1));
            for (Integer key : productIdToQuantity.keySet()) {
                int counter = productIdToQuantity.get(key);
                Offer offer = sale.getWhatYouGet().getOffers().stream().filter(Offer -> Offer.getId() == key).collect(Collectors.toList()).get(0);
                if(store.getIdToSell().get(offer.getId()).getProduct().getPricing().toString().equals("Product")){
                    numOfProducts[0] += offer.getQuantity() * counter;
                } else {
                    numOfProducts[0] ++;
                }
            }
        }
        return numOfProducts[0];
    }
}
