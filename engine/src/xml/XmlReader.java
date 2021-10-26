package xml;

import enums.Pricing;
import market.Coordinates;
import market.*;
import exception.*;
import xml.schema.generated.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public abstract class XmlReader {

    private final static String PACKAGE_NAME = "xml.schema.generated";

    private static void readAllData(SuperDuperMarketDescriptor marketToRead, Market marketReceiving) {
        Map<Integer, Product> idToProduct = getProductsMap(marketToRead);
        Map<Integer, Store> idToStore = getStoresMap(marketToRead);
        addProductsToStores(marketToRead, idToStore, idToProduct);
        marketReceiving.setIdToStore(idToStore);
        marketReceiving.setIdToProduct(idToProduct);
    }

    private static void addProductsToStores(SuperDuperMarketDescriptor market, Map<Integer, Store> idToStore, Map<Integer, Product> idToProduct) {
        List<SDMStore> stores = market.getSDMStores().getSDMStore();
        Store storeToAdd;

        for (SDMStore store : stores) {
            storeToAdd = idToStore.get(store.getId());

            for (SDMSell sell : store.getSDMPrices().getSDMSell()) {
                storeToAdd.addProduct(sell.getItemId(), new Sell(sell.getPrice(), idToProduct.get(sell.getItemId())));
                idToProduct.get(sell.getItemId()).setNumberOfStoresSelling(idToProduct.get(sell.getItemId()).getNumberOfStoresSelling() + 1);
            }
        }
    }

    public static IMarketEngine readFile(Path path) throws JAXBException, IOException, DuplicateException, ValueOutOfRangeException, InvalidActionException {
        Market newMarket = new Market();
        testAndLoad(path, newMarket);
        return newMarket;
    }

    public static Zone readFile(StringBuilder fileContent) throws FileNotFoundException, DuplicateException, InvalidActionException, ValueOutOfRangeException, JAXBException {
        Market newMarket = new Market();
        String zoneName = testAndLoad(fileContent, newMarket);
        return new Zone(zoneName, newMarket);
    }

    private static void testAndLoad(Path path, Market newMarket) throws FileNotFoundException, JAXBException, DuplicateException, InvalidActionException, ValueOutOfRangeException {
        InputStream inputStream = new FileInputStream(new File(path.toString()));
        SuperDuperMarketDescriptor SDMarket = deserializeFrom(inputStream);
        checkData(SDMarket, newMarket);
    }

    private static String testAndLoad(StringBuilder fileContent, Market newMarket) throws FileNotFoundException, JAXBException, DuplicateException, InvalidActionException, ValueOutOfRangeException {
//        InputStream inputStream = new FileInputStream(fileContent.toString());
        InputStream inputStream = new ByteArrayInputStream(fileContent.toString().getBytes(StandardCharsets.UTF_8));
        SuperDuperMarketDescriptor SDMarket = deserializeFrom(inputStream);
        checkData(SDMarket, newMarket);
        return SDMarket.getSDMZone().getName();
    }

    public static IMarketEngine readFile(String absolutePath) throws JAXBException, IOException, DuplicateException, ValueOutOfRangeException, InvalidActionException {
        Path path = Paths.get(absolutePath);
        return readFile(path);
    }

    private static Map<Integer, Store> getStoresMap(SuperDuperMarketDescriptor market) {
        return market.getSDMStores()
                .getSDMStore()
                .stream()
                .collect(Collectors.toMap(SDMStore::getId, store -> new Store(store)));
    }

    private static Map<Integer, Product> getProductsMap(SuperDuperMarketDescriptor market) {
        return market.getSDMItems()
                .getSDMItem()
                .stream()
                .collect(Collectors
                        .toMap(SDMItem::getId, item -> new Product(item.getName(), item.getId(),
                                item.getPurchaseCategory().equals("Weight") ? Pricing.WEIGHT : Pricing.PRODUCT, 0, 0)));
    }

    public static void checkData(SuperDuperMarketDescriptor SDMarket, Market marketReceiving) throws DuplicateException, ValueOutOfRangeException, InvalidActionException {
        Map<Integer, Integer> storesIdToOccurrences = SDMarket.getSDMStores().getSDMStore().stream()
                .collect(Collectors.toMap(SDMStore::getId, v -> 1, Integer::sum));
        Map<Integer, Integer> itemsIdToOccurrences = SDMarket.getSDMItems().getSDMItem().stream()
                .collect(Collectors.toMap(SDMItem::getId, v -> 1, Integer::sum));

        verifySales(SDMarket);
        verifyLocations(SDMarket);
        verifyOccurrences(SDMarket, storesIdToOccurrences, itemsIdToOccurrences);
        checkStoresItems(itemsIdToOccurrences, SDMarket.getSDMStores().getSDMStore());
        readAllData(SDMarket, marketReceiving);
    }

    private static void verifyOccurrences(SuperDuperMarketDescriptor market, Map<Integer, Integer> storesIdToOccurrences, Map<Integer, Integer> itemsIdToOccurrences) throws DuplicateException, ValueOutOfRangeException {
        if (!checkIdShows(storesIdToOccurrences)) {
            throw new DuplicateException("There are duplicate stores");
        }
        if (!checkIdShows(itemsIdToOccurrences)) {
            throw new DuplicateException("There are duplicate items");
        }
    }

    private static void verifyLocations(SuperDuperMarketDescriptor market) throws DuplicateException {
        Map<Integer, Set<Integer>> xLocationToYLocation = new HashMap<>();
        int countNumberOfStores = 0;

        for (SDMStore store : market.getSDMStores().getSDMStore()) { //add store locations to map
            countNumberOfStores++;
            if (!xLocationToYLocation.containsKey(store.getLocation().getX())) {
                xLocationToYLocation.put(store.getLocation().getX(), new HashSet<>());
            }
            xLocationToYLocation.get(store.getLocation().getX()).add(store.getLocation().getY());
        }

        if (countNumberOfStores != xLocationToYLocation.values().stream().mapToInt(Set::size).sum()) {
            throw new DuplicateException("There are duplicate locations");
        }
    }

    private static void verifySales(SuperDuperMarketDescriptor market) throws InvalidActionException {
        for(SDMStore store : market.getSDMStores().getSDMStore()){
            Set<Integer> productsIDS = new HashSet<>();
            store.getSDMPrices().getSDMSell().forEach(sell -> productsIDS.add(sell.getItemId()));
            if(store.getSDMDiscounts() != null) {
                for (SDMDiscount discount : store.getSDMDiscounts().getSDMDiscount()) {
                    List<SDMOffer> offers = discount.getThenYouGet().getSDMOffer();
                    if (!productsIDS.contains(discount.getIfYouBuy().getItemId())) {
                        throw new InvalidActionException("Store " + store.getName() + " doesnt sell items from discount " + discount.getName());
                    }
                    if (offers.stream().filter(offer -> !productsIDS.contains(offer.getItemId())).count() != 0) {
                        throw new InvalidActionException("Store " + store.getName() + " doesnt sell items from discount " + discount.getName());
                    }
                }
            }
        }
    }

    private static void checkStoresItems(Map<Integer, Integer> items, List<SDMStore> stores) throws DuplicateException, ValueOutOfRangeException {
        for(Integer key : items.keySet()) {  //make better
            items.replace(key,0);
        }
        for(SDMStore store : stores){
            int count = (int) store.getSDMPrices().getSDMSell().stream()                    //counts legit items in store
                    .filter(item -> items.containsKey(item.getItemId()))
                    .count();
            if(!Coordinates.isValidCoordinate(store.getLocation().getX(),store.getLocation().getY())){ //check location validation
                throw new ValueOutOfRangeException(store.getName() + " location", 1, 50);
            }

            store.getSDMPrices().getSDMSell().                                      //count item sells - to see if all are selling
                    forEach(item -> increaseKeyValue(items,item.getItemId()));

            Map<Integer, Integer> itemsToOccurrences = store.getSDMPrices().getSDMSell().stream()   //store item to occurrences
                    .collect(Collectors.toMap(SDMSell::getItemId, v -> 1, Integer::sum));

            if(!checkIdShows(itemsToOccurrences)){                                          //check duplication
                throw new DuplicateException("There are duplicate items in the store");
            }
            if(count != store.getSDMPrices().getSDMSell().size()){                          //check items id
                throw new ValueOutOfRangeException("There are items in the store that does not exist");
            }
        }
        checkForUnsoldItems(items.values());
    }

    private static boolean checkIdShows(Map map){
        Collection<Integer> shows = map.values();
        int counter = (int) shows.stream()
                                 .filter(i -> i > 1)
                                 .count();
        return counter == 0;
    }

    public static void checkForUnsoldItems(Collection<Integer> list){
        List<Integer> items = list.stream()
                .filter(i -> i == 0)
                .collect(Collectors.toList());

        if(items.size() != 0){
            throw new InputMismatchException("There are items that are not sold by any store");
        }
    }

    private static void increaseKeyValue(Map<Integer,Integer> map, int key){
        int number = map.getOrDefault(key, -1);
        if(number != -1){
            map.put(key, ++number);
        }
    }

    private static SuperDuperMarketDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (SuperDuperMarketDescriptor) u.unmarshal(in);
    }

    public static class SortByLocation implements Comparator<Location> {
        @Override
        public int compare(Location o1, Location o2) {
            if(o1.getX() > o2.getX()){
                return 1;
            }else if (o1.getX() < o2.getX()){
                return -1;
            }else {
                if(o1.getY() > o2.getY()){
                    return 1;
                }
                else if(o1.getY() < o2.getY()){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        }
    }
}