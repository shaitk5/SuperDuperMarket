package market;

import java.util.ArrayList;
import java.util.List;

public class Updates {
    private List<FeedbackUpdate> feedbacks;
    private List<OrderUpdate> orders;
    private List<StoreUpdate> stores;

    Updates(){
        reset();
    }

    public void reset(){
        this.feedbacks = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.stores = new ArrayList<>();
    }

    public void addFeedbackUpdate(FeedbackUpdate feedbackUpdate){
        feedbacks.add(feedbackUpdate);
    }

    public void addOrderUpdate(OrderUpdate orderUpdate){
        orders.add(orderUpdate);
    }

    public void addStoreUpdate(StoreUpdate storeUpdate){
        stores.add(storeUpdate);
    }

    public int getFeedbacksVersion() {
        return feedbacks.size();
    }

    public int getOrderVersion() {
        return orders.size();
    }

    public int getNewStoreVersion() {
        return stores.size();
    }

    public List<FeedbackUpdate> getFeedbackEntries(int fromIndex){
        if (fromIndex < 0 || fromIndex > feedbacks.size()) {
            fromIndex = 0;
        }
        return feedbacks.subList(fromIndex, feedbacks.size());
    }
    public List<OrderUpdate> getOrdersEntries(int fromIndex){
        if (fromIndex < 0 || fromIndex > orders.size()) {
            fromIndex = 0;
        }
        return orders.subList(fromIndex, orders.size());
    }

    public List<StoreUpdate> getNewStoreEntries(int fromIndex){
        if (fromIndex < 0 || fromIndex > stores.size()) {
            fromIndex = 0;
        }
        return stores.subList(fromIndex, stores.size());
    }

    public static class FeedbackUpdate{
        String customerName;
        String zone;
        int rating;
        String comment;
        String date;

        public FeedbackUpdate(String customerName, String zone, int rating, String comment, String date) {
            this.customerName = customerName;
            this.zone = zone;
            this.rating = rating;
            this.comment = comment;
            this.date = date;
        }
    }

    public static class OrderUpdate{
        int orderSerialNumber;
        int subOrderSerialNumber;
        String customerName;
        int numberOfProductsTypes;
        double productsPrice;
        double deliveryPrice;

        public OrderUpdate(int orderSerialNumber, int subOrderSerialNumber, String customerName, int numberOfProductsTypes, double productsPrice, double deliveryPrice) {
            this.orderSerialNumber = orderSerialNumber;
            this.subOrderSerialNumber = subOrderSerialNumber;
            this.customerName = customerName;
            this.numberOfProductsTypes = numberOfProductsTypes;
            this.productsPrice = productsPrice;
            this.deliveryPrice = deliveryPrice;
        }
    }

    public static class StoreUpdate{
        String ownerName;
        String storeName;
        Coordinates storeLocation;
        String zone;
        int storeNumberOfProducts;
        int zoneNumberOfProducts;

        public StoreUpdate(String ownerName, String storeName, String zone, Coordinates storeLocation, int storeNumberOfProducts, int zoneNumberOfProducts) {
            this.ownerName = ownerName;
            this.storeName = storeName;
            this.storeLocation = storeLocation;
            this.zone = zone;
            this.storeNumberOfProducts = storeNumberOfProducts;
            this.zoneNumberOfProducts = zoneNumberOfProducts;
        }
    }
}
