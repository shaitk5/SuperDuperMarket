package users;

import market.Buyer;
import market.Customer;
import market.Seller;

import java.util.*;

public class UserManager {

    private final Map<String, Customer> USERS = new HashMap<>();

    public synchronized void addUser(String username, boolean isBuyer) {
        USERS.put(username.toLowerCase(), isBuyer ? makeNewBuyer(username) : makeNewSeller(username));
    }

    public synchronized void removeUser(String username) {
        USERS.remove(username.toLowerCase());
    }

    public synchronized Collection<Customer> getUsers() {
        return Collections.unmodifiableCollection(USERS.values());
    }

    public boolean isUserExists(String username) {
        return username != null && USERS.containsKey(username.toLowerCase());
    }

    private Customer makeNewBuyer(String userName){
        return new Buyer(userName);
    }

    private Customer makeNewSeller(String userName){
        return new Seller(userName);
    }

    public Customer getUser(String username) {
        return USERS.getOrDefault(username.toLowerCase(), null);
    }
}
