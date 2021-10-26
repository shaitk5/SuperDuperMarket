package market;

import DTOClass.*;
import enums.Action;
import exception.DuplicateException;
import enums.Pricing;
import exception.InvalidActionException;
import json.JsonStore;
import users.UserManager;

import javax.activity.InvalidActivityException;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Market implements IMarketEngine {

    boolean fileLoaded = false;
    private final String FILE_NAME = "\\orderHistory.dat";
    private Path ordersFilePath;
    private List<OrderDTO> dynamicOrder = null;
    private Map<Integer, Store> idToStore = new HashMap<>();
    private Map<Integer, Product> idToProduct = new HashMap<>();
    private Map<Integer, Order> idToOrder = new HashMap<>();

    public Market() { }
    //getters setters
    public void setIdToStore(Map<Integer, Store> idToStore) {
        this.idToStore = idToStore;
    }

    public void setIdToProduct(Map<Integer, Product> idToProduct) {
        this.idToProduct = idToProduct;
    }

    @Override
    public boolean isSerialNumberAvailable(int serial) {
        long count = idToStore.values().stream()
                .filter(store -> store.getSerialNumber() == serial)
                .count();
        if(count != 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean isLocationAvailable(Coordinates checkLocation) throws DuplicateException {
        Collection<Store> stores = idToStore.values();
        long count = stores.stream()
                .filter(store -> store.getCoordinates().getX() == checkLocation.getX() && store.getCoordinates().getY() == checkLocation.getY())
                .count();
        if(count != 0){
            throw new DuplicateException("This location is taken");
        }
        return Coordinates.isValidCoordinate(checkLocation);
    }

    @Override
    public void addStore(StoreDTO store) {
        Set<Sale> sales = new HashSet<>();
        store.getSALES().forEach(sale -> sales.add(new Sale(sale)));

        this.idToStore.put(store.getSerialNumber(),
                new Store(store.getName(), store.getSerialNumber(), store.getPPK(), store.getLocation().getX(), store.getLocation().getY(), sales, store.getOWNER()));
    }

    @Override
    public void addStore(JsonStore store){
        int storeSerial = getFreeStoreSerial();
        idToStore.put(storeSerial, new Store(store.getName(), storeSerial, store.getPpk(), store.getLocation().getX(), store.getLocation().getY(),
                null, store.getOwner()));

        Store storeAdded = idToStore.get(storeSerial);

        store.getIdToPrice().keySet().forEach(productSerial -> {
            double price = store.getIdToPrice().get(productSerial);
            Product product = idToProduct.get(productSerial);
            product.setNumberOfStoresSelling(product.getNumberOfStoresSelling() + 1);
            storeAdded.addProduct(productSerial, new Sell(price, product));
        });

    }

    @Override
    public int getFreeStoreSerial() {
        int i = 1;
        while(true){
            if(!idToStore.containsKey(i)){
                return i;
            }
            i++;
        }
    }

    @Override
    public Map getAllData(boolean includeOrders) {
        Map<Integer, StoreDTO> idToStoresDTO = new HashMap();
        Map<Integer, ProductDTO> idToProductsDTO = getAllProducts();
        Map<Double, OrderDTO> idToOrderDTO  = null;
        Set<OrderDTO> storeOrders = new HashSet<>();
        Set<SaleDTO> sales;
        if(includeOrders) {                         //make all orders
            idToOrderDTO = makeOrdersDTO();
        }

        for(Store store : idToStore.values()){
            Map<Integer, SellDTO> idToSellDTO = new HashMap<>();
            Map<Integer, ProductDTO> finalIdToProductsDTO = idToProductsDTO;
            store.getIdToSell().values().forEach(sell -> idToSellDTO.put(sell.getProduct().getSerialNumber(),
                    new SellDTO(sell.getPrice(), sell.getTimesSold(), sell.getQuantitySold(), finalIdToProductsDTO.get(sell.getProduct().getSerialNumber())))); // add all store sell items
            if(includeOrders) {
                storeOrders = idToOrderDTO.values().stream().filter(order -> order.getStoreSerialNumber() == store.getSerialNumber()).collect(Collectors.toSet());
            }
            sales = makeSalesDTO(store.getSerialNumber());
            StoreDTO newStore = new StoreDTO(store.getOwner(), store.getSerialNumber(), store.getDeliveryPPK(), store.getName(),
                    store.getCoordinates().getX(), store.getCoordinates().getY(), idToSellDTO, sales, storeOrders);  //make all stores DTO
            idToStoresDTO.put(store.getSerialNumber(), newStore);
        }

        return idToStoresDTO;
    }

    @Override
    public void setOwnerOnNewMarketMade(String owner) {
        idToStore.values().forEach(store -> store.setOwner(owner));
    }

    private Set<SaleDTO> makeSalesDTO(int storeId) {
        Set<SaleDTO> sales = new HashSet<>();
        for (Sale sale : idToStore.get(storeId).getSALES()) {
            List<OfferDTO> offers = new ArrayList<>();
            sale.getWhatYouGet().getOffers().forEach(offer -> offers.add(new OfferDTO(offer.getQuantity(), offer.getId(), offer.getExtraCost())));
            sales.add(new SaleDTO(sale.getNAME(), new BuyDTO(sale.getWhatYouBuy().getQuantity(), sale.getWhatYouBuy().getItemId()),
                    new GetDTO(offers, sale.getWhatYouGet().getOperator())));
        }
        return sales;
    }

    @Override
    public List getUserOrders(String customerName) {
        List<OrderDTO> userOrders = new ArrayList<>();
        Map<Integer, StoreDTO> allData = this.getAllData(Boolean.TRUE);
        allData.values().forEach(store -> {
            store.getOrders().forEach(order -> {
                if(order.getCUSTOMER_NAME().equals(customerName)){
                    userOrders.add(order);
                }
            });
        });
        return userOrders;
    }

    private Map<Double, OrderDTO>  makeOrdersDTO(){
        Map<Double, OrderDTO> idToOrderDTO = new HashMap();

        for (Order order : idToOrder.values()) {
            for (SubOrder subOrder : order.getSubOrders()) {
                idToOrderDTO.put(order.getSerialNumber() + (0.01 * subOrder.getSerialNumber()),
                        new OrderDTO(order.getSerialNumber(), order.getDate(),
                                subOrder.getSerialNumber(), subOrder.getNumOfProducts(this), subOrder.getProductsPrice(),
                                order.getCUSTOMER_NAME(), new LocationDTO(order.getCUSTOMER_LOCATION()),
                                subOrder.getSALES_USED(), subOrder.getStore().getSerialNumber(), subOrder.getIdToQuantity(),
                                subOrder.getStore().getCoordinates(), subOrder.getStore().getDeliveryPPK()));
            }
        }

        return idToOrderDTO;
    }

    @Override
    public Pricing getProductPricing(int productId) {
        if(idToProduct.containsKey(productId)){
            return idToProduct.get(productId).getPricing();
        }
        return null;
    }

    private double getProductAveragePrice(int serialNumber) {
        Product product = idToProduct.get(serialNumber);
        double totalPrice = idToStore.values().stream().
                map(store -> store.getIdToSell().getOrDefault(serialNumber, null)).filter(Objects::nonNull).mapToDouble(Sell::getPrice).sum();
        return (totalPrice / product.getNumberOfStoresSelling());
    }

    @Override
    public Map getAllProducts() {
        Map<Integer, ProductDTO> idToProductsDTO = new HashMap();
        idToProduct.values().forEach(product -> {
            double averagePrice = getProductAveragePrice(product.getSerialNumber());
            idToProductsDTO.put(product.getSerialNumber(),
                    new ProductDTO(product.getSerialNumber(), product.getNumberOfStoresSelling(),
                            product.getName(), product.getPricing().toString(), product.getQuantitySold(), averagePrice));
        });  //make all products DTO
        return idToProductsDTO;
    }

    @Override
    public void addProductToStore(int storeSerialNumber, int product, double price) {
        Store storePick = idToStore.get(storeSerialNumber);
        Product productPick = idToProduct.get(product);
        productPick.setNumberOfStoresSelling(productPick.getNumberOfStoresSelling() + 1);
        storePick.getIdToSell().put(productPick.getSerialNumber(), new Sell(price, productPick));
    }

    @Override
    public void addSaleToStore(Sale sale, int storeId) {
        idToStore.get(storeId).addSale(sale);
    }

    @Override
    public void addNewProduct(Product productToAdd) {
        idToProduct.put(productToAdd.getSerialNumber(), productToAdd);
    }

    @Override
    public void deleteProduct(int storeSerialNumber, int productSerialNumber) throws InvalidActivityException {
        Store storePick = idToStore.get(storeSerialNumber);
        Sell productSell = storePick.getIdToSell().get(productSerialNumber);

        if(productSell.getProduct().getNumberOfStoresSelling() > 1){
            if(storePick.getIdToSell().size() > 1){
                productSell.getProduct().setNumberOfStoresSelling(productSell.getProduct().getNumberOfStoresSelling() - 1);
                storePick.getIdToSell().remove(productSerialNumber);
            }
            else{
                throw new InvalidActivityException("Invalid action, " + productSell.getProduct().getName() + " is the only product in " + storePick.getName());
            }
        }
        else{
            throw new InvalidActivityException("Invalid action, store " + storePick.getName() + " is the only store that sell " + productSell.getProduct().getName());
        }
    }

    @Override
    public int getNumberOfOrders() {
        return idToOrder.size();
    }

    @Override
    public void saveOrderHistory(Path path) {
        this.setOrdersFilePath(path);
        Map<Double, OrderDTO> orders = makeOrdersDTO();
        ArrayList<OrderDTO> ordersToSave = new ArrayList<>(orders.values());
        try (
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path.toString()))) {
            out.writeObject(ordersToSave);
            out.flush();
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        fileLoaded = true;
    }

    @Override
    public void loadOrderHistoryFromFile(Path path) throws InvalidActionException {
        ArrayList<OrderDTO> orders = null;

        if(!fileLoaded) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path.toString()))) {
                orders = (ArrayList<OrderDTO>) in.readObject();
            } catch (IOException | ClassNotFoundException ignored) {
                ignored.printStackTrace();
            }

            int counter = 1;
            while (orders != null && !orders.isEmpty()) {
                int finalCounter = counter;
                List<OrderDTO> allOrders = orders.stream().filter(order -> order.getSerialNumber() == finalCounter).collect(Collectors.toList());
                this.dynamicOrder = allOrders;
                addSavedOrders(allOrders);
                orders.removeAll(allOrders);
                counter++;
            }
            fileLoaded = true;
        }
        else{
            throw new InvalidActionException("File already loaded");
        }
    }

    private void addSavedOrders(List<OrderDTO> allOrders) {
        Order newOrder = new Order(allOrders.get(0).getCUSTOMER_NAME(), null, allOrders.get(0).getDate(), allOrders.get(0).getCUSTOMER_LOCATION());
        idToOrder.put(newOrder.getSerialNumber(), newOrder);
        Map<Integer, StoreDTO> newStores = new HashMap<>();

        for(OrderDTO order : allOrders){
            Set<Integer> productsAddedFromOrder = new HashSet<>();
            Store store = idToStore.get(order.getStoreSerialNumber());
            SubOrder sub = new SubOrder(order.getIdToQuantity(), order.getSerialNumber(), order.getSALES_USED(), order.getProductsPrice(),
                    order.getDELIVERY_PRICE(), newOrder, store);
            Map<Integer, Double> idToQuantity = sub.getIdToQuantity();
            idToQuantity.keySet().forEach(productId -> {
                productsAddedFromOrder.add(productId);
                idToProduct.get(productId).setQuantitySold(idToProduct.get(productId).getQuantitySold() + idToQuantity.get(productId));
                store.getIdToSell().get(productId).setQuantitySold(store.getIdToSell().get(productId).getQuantitySold() + idToQuantity.get(productId));
                store.getIdToSell().get(productId).setTimesSold(store.getIdToSell().get(productId).getTimesSold() + 1);
            });
            newOrder.addSubOrder(sub);
            idToStore.get(order.getStoreSerialNumber()).addOrder(sub);
            order.getSALES_USED().addDataToStoreSell(idToStore.get(order.getStoreSerialNumber()), productsAddedFromOrder);
        }
    }

    @Override
    public void updateProductPrice(int storeSerialNumber, int productSerialNumber, double newPrice) {
        idToStore.get(storeSerialNumber).getIdToSell().get(productSerialNumber).setPrice(newPrice);
    }

    @Override
    public boolean isProductExist(int key) {
        return idToProduct.containsKey(key);
    }

    @Override
    public String getStoreOwner(int storeSerial) {
        return idToStore.get(storeSerial).getOwner();
    }

    public SaleDTO getSale(String saleName) {
        for(Store store : idToStore.values()){
            Sale sale = store.containSale(saleName);
            if(sale != null){
                List<OfferDTO> offers = new ArrayList<>();
                sale.getWhatYouGet().getOffers().forEach(offer -> offers.add(new OfferDTO(offer.getQuantity(), offer.getId(), offer.getExtraCost())));
                return new SaleDTO(saleName,
                        new BuyDTO(sale.getWhatYouBuy().getQuantity(), sale.getWhatYouBuy().getItemId()),
                        new GetDTO(offers, sale.getWhatYouGet().getOperator()));
            }
        }
        return null;
    }

    public Sale findSale(String saleName) {
        Sale saleToReturn = null;
        if(idToStore != null){
            for(Store store : idToStore.values()){
                saleToReturn = store.containSale(saleName);
                if(saleToReturn != null){
                    break;
                }
            }
        }
        return saleToReturn;
    }

    private double getProductsPriceFromCart(Cart cart){
        Map<Integer, Double> idToQuantity = cart.getIdToQuantity();
        Store store = idToStore.get(cart.getStore());
        return cart.getIdToQuantity().keySet().stream().mapToDouble(id -> idToQuantity.get(id) * store.getIdToSell().get(id).getPrice()).sum();
    }

    @Override
    public void addOrder(Cart cart, SalesUsed salesUsed, UserManager userManager, Customer buyer) {
        Store store = idToStore.get(cart.getStore());
        Order newOrder = new Order(cart.getCustomer(), salesUsed, cart.getDate(), cart.getCustomerLocation());
        SubOrder newSubOrder = new SubOrder(cart.getIdToQuantity(), 1, salesUsed,
                (getProductsPriceFromCart(cart) + salesUsed.getProductsPrice(store)),
                (LocationDTO.getDistance(cart.getCustomerLocation(), store.getCoordinates())* store.getDeliveryPPK()) , newOrder, store);
        newOrder.addSubOrder(newSubOrder);
        this.idToOrder.put(newOrder.getSerialNumber(), newOrder);
        store.addOrder(newSubOrder);

        Set<Integer> productsAddedFromCart = new HashSet<>();
        cart.getIdToQuantity().keySet().forEach(productId -> {
            productsAddedFromCart.add(productId);
            idToProduct.get(productId).setQuantitySold(idToProduct.get(productId).getQuantitySold() + cart.getIdToQuantity().get(productId));
            store.getIdToSell().get(productId).setTimesSold(store.getIdToSell().get(productId).getTimesSold() + 1);
            store.getIdToSell().get(productId).setQuantitySold(store.getIdToSell().get(productId).getQuantitySold() + cart.getIdToQuantity().get(productId));
        });
        salesUsed.addDataToStoreSell(idToStore.get(cart.getStore()), productsAddedFromCart);
        transferMoneyToSeller(store.getOwner(), newOrder.getTotalPrice(), userManager, newOrder.getDate());
        transferMoneyFromBuyer(buyer, (newSubOrder.getDeliveryPrice() + newSubOrder.getProductsPrice()), newOrder.getDate());
        addOrderUpdate(userManager, store.getOwner(), buyer.NAME, newSubOrder.getProductsPrice(), newSubOrder.getDeliveryPrice() ,newOrder.getSerialNumber(),
                newSubOrder.getSerialNumber(), newSubOrder.getNumberOfProductsTypes(this));

    }

    @Override
    public void addDynamicOrder(SalesUsed salesUsed, List<OrderDTO> orders, UserManager userManager, Customer buyer) {
        Order newOrder = new Order(dynamicOrder.listIterator().next().getCUSTOMER_NAME(), salesUsed, orders.get(0).getDate(), orders.get(0).getCUSTOMER_LOCATION());
        idToOrder.put(newOrder.getSerialNumber(), newOrder);

        for(OrderDTO order : orders){
            Set<Integer> productsAddedFromOrder = new HashSet<>();
            Store store = idToStore.get(order.getStoreSerialNumber());
            SubOrder sub = new SubOrder(order.getIdToQuantity(), order.getSubOrderSerialNumber(), salesUsed, order.getProductsPrice(),
                    order.getDELIVERY_PRICE(), newOrder, idToStore.get(order.getStoreSerialNumber()));
            Map<Integer, Double> idToQuantity = sub.getIdToQuantity();
            idToQuantity.keySet().forEach(productId -> {
                productsAddedFromOrder.add(productId);
                idToProduct.get(productId).setQuantitySold(idToProduct.get(productId).getQuantitySold() + idToQuantity.get(productId));
                store.getIdToSell().get(productId).setQuantitySold(store.getIdToSell().get(productId).getQuantitySold() + idToQuantity.get(productId));
                store.getIdToSell().get(productId).setTimesSold(store.getIdToSell().get(productId).getTimesSold() + 1);
            });
            newOrder.addSubOrder(sub);
            idToStore.get(order.getStoreSerialNumber()).addOrder(sub);
            salesUsed.addDataToStoreSell(idToStore.get(order.getStoreSerialNumber()), productsAddedFromOrder);
            transferMoneyToSeller(store.getOwner(), (sub.getDeliveryPrice() + sub.getProductsPrice()), userManager, newOrder.getDate());
            addOrderUpdate(userManager, store.getOwner(), buyer.NAME, sub.getProductsPrice(), sub.getDeliveryPrice() ,newOrder.getSerialNumber(),
                    sub.getSerialNumber(),sub.getNumberOfProductsTypes(this));
        }
        transferMoneyFromBuyer(buyer, newOrder.getTotalPrice(), newOrder.getDate());
    }

    private void addOrderUpdate(UserManager userManager,String ownerName, String buyerName, double productsPrice, double deliveryPrice, int orderSerialNumber,
                                int subOrderSerialNumber, int numberOfProductsTypes) {
        Customer storeOwner = userManager.getUser(ownerName);
        storeOwner.getUpdates().addOrderUpdate(new Updates.OrderUpdate(orderSerialNumber, subOrderSerialNumber, buyerName,
                numberOfProductsTypes, productsPrice, deliveryPrice));

    }

    private void transferMoneyToSeller(String owner, double money, UserManager userManager, String date) {
        Customer seller = userManager.getUser(owner);
        double currentBalance = seller.getWallet().getBalance();
        seller.getWallet().addTransaction(new Transactions(Action.RECEIVE, money, currentBalance, date));

    }

    private void transferMoneyFromBuyer(Customer buyer, double money, String date) {
        double currentBalance = buyer.getWallet().getBalance();
        buyer.getWallet().addTransaction(new Transactions(Action.PAY, money, currentBalance, date));
    }

    @Override
    public List<OrderDTO> buildDynamicOrder(Map<Integer, Double> idToQuantity, String date, LocationDTO customerLocation, String customerName) {
        Map<Integer, Double> idToPrice = new HashMap<>();
        Map<Integer, Integer> idToStoreSelling = new HashMap<>();
        Map<Integer, Set<Integer>> storeSellingToIds = new HashMap<>();

        for(Store store : idToStore.values()){
            if(!store.getCoordinates().equals(customerLocation)) {
                idToQuantity.keySet().forEach(key -> {
                    if (store.getIdToSell().containsKey(key)) {
                        if (idToPrice.getOrDefault(key, 0.0) == 0.0) {
                            idToPrice.put(key, store.getIdToSell().get(key).getPrice());
                            idToStoreSelling.put(key, store.getSerialNumber());
                            if(!storeSellingToIds.containsKey(store.getSerialNumber())) {
                                storeSellingToIds.put(store.getSerialNumber(),new HashSet<>());
                            }
                            storeSellingToIds.get(store.getSerialNumber()).add(key);
                        }
                        if (store.getIdToSell().get(key).getPrice() < idToPrice.get(key)) {
                            idToPrice.put(key, store.getIdToSell().get(key).getPrice());
                            storeSellingToIds.get(idToStoreSelling.get(key)).remove(key);
                            if(storeSellingToIds.get(idToStoreSelling.get(key)).size() == 0){
                                storeSellingToIds.remove(idToStoreSelling.get(key));
                            }
                            idToStoreSelling.put(key, store.getSerialNumber());
                            if(!storeSellingToIds.containsKey(store.getSerialNumber())) {
                                storeSellingToIds.put(store.getSerialNumber(), new HashSet<>());
                            }
                            storeSellingToIds.get(store.getSerialNumber()).add(key);
                        }
                    }
                });
            }
        }

        this.dynamicOrder = buildOrderDTO(idToPrice, storeSellingToIds, idToQuantity, date, customerName, customerLocation);
        Collections.sort(dynamicOrder, new SortBySerialNumber());
        return this.dynamicOrder;
    }

    private List<OrderDTO> buildOrderDTO(Map<Integer, Double> idToPrice, Map<Integer, Set<Integer>> storeSellingToIds,
                                         Map<Integer, Double> idToQuantity, String date, String customerName, LocationDTO customerLocation){
        List<OrderDTO> orders = new ArrayList<>();
        int orderSerialNumber = Order.serialNumberCounter;
        double productsPrice = 0;
        int counter = 1 ;

        for(Integer storeId : storeSellingToIds.keySet()) {
            Map<Integer, Double> idToQuantityStore = new HashMap<>();
            productsPrice = 0;

            for(Integer productId : storeSellingToIds.get(storeId)){
                productsPrice += idToPrice.get(productId) * idToQuantity.get(productId);
                idToQuantityStore.put(productId, idToQuantity.get(productId));
            }

            Store store = idToStore.get(storeId);
            orders.add(new OrderDTO(orderSerialNumber, date, counter++ ,
                   SubOrder.getNumOfProducts(this, storeSellingToIds.get(storeId), idToQuantityStore) , productsPrice,
                    customerName, customerLocation, null, storeId, idToQuantityStore, store.getCoordinates(), store.getDeliveryPPK()));
        }

        return orders;
    }

    private void setOrdersFilePath(Path ordersFilePath) {
        this.ordersFilePath = ordersFilePath;
    }

    protected static class SortBySerialNumber implements Comparator<OrderDTO> {
        public int compare(OrderDTO a, OrderDTO b)
        {
            if(a.getSerialNumber() > b.getSerialNumber()){
                return 1;
            }
            else if(a.getSerialNumber() < b.getSerialNumber()){
                return -1;
            }
            else{
                if(a.getSubOrderSerialNumber() > b.getSubOrderSerialNumber()){
                    return 1;
                }
                else if(a.getSubOrderSerialNumber() < b.getSubOrderSerialNumber()){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        }
    }

    @Override
    public int getNUmberOfProducts() {
        return idToProduct.size();
    }

    @Override
    public int getNumberOfStores() {
        return idToStore.size();
    }

    @Override
    public double getAverageOrderPrice() {
        double totalPrice = idToOrder.values().stream().mapToDouble(Order::getProductsPrice).sum();
        return idToOrder.size() != 0 ? totalPrice / idToOrder.size() : 0;
    }
}

