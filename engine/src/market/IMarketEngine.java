package market;

import DTOClass.LocationDTO;
import DTOClass.OrderDTO;
import DTOClass.SaleDTO;
import DTOClass.StoreDTO;
import exception.DuplicateException;
import enums.Pricing;
import exception.InvalidActionException;
import json.JsonStore;
import users.UserManager;

import javax.activity.InvalidActivityException;
import java.nio.file.Path;
import java.util.*;

public interface IMarketEngine {
    Map getAllData(boolean includeOrders);
    Map getAllProducts();
    Pricing getProductPricing(int productId);
    boolean isLocationAvailable(Coordinates checkLocations) throws DuplicateException;
    void addOrder(Cart cart, SalesUsed salesUsed, UserManager userManager, Customer buyer);
    void addDynamicOrder(SalesUsed salesUsed, List<OrderDTO> orders, UserManager userManager, Customer buyer) throws InvalidActionException;
    List buildDynamicOrder(Map<Integer, Double> idToQuantity, String date, LocationDTO customerLocation, String customerName);
    void addProductToStore(int storeSerialNumber, int productPick, double price);
    void updateProductPrice(int storeSerialNumber, int productSerialNumber, double newPrice);
    void deleteProduct(int storeSerialNumber, int productSerialNumber) throws InvalidActivityException;
    void saveOrderHistory(Path path);
    void loadOrderHistoryFromFile(Path path) throws InvalidActionException;
    boolean isProductExist(int key);
    void addStore(StoreDTO store);
    void addNewProduct(Product productToAdd);
    void addSaleToStore(Sale sale, int storeId);
    boolean isSerialNumberAvailable(int serial);
    int getNumberOfOrders();
    int getNUmberOfProducts();
    int getNumberOfStores();
    double getAverageOrderPrice();
    List getUserOrders(String customerName);
    void setOwnerOnNewMarketMade(String owner);
    int getFreeStoreSerial();
    void addStore(JsonStore store);
    String getStoreOwner(int storeSerial);
    SaleDTO getSale(String saleName);
}
