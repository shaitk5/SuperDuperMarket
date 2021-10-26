package servlets;

import DTOClass.OrderDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.InvalidActionException;
import json.JsonCart;
import json.SalesChosen;
import market.*;
import users.UserManager;
import utils.Constants;
import utils.ServletUtils;
import utils.SessionUtils;
import zone.ZonesManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class FinishOrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processFinishOrder(request, response);
        } catch (InvalidActionException e) {
            e.printStackTrace();
        }
    }

    private void processFinishOrder(HttpServletRequest request, HttpServletResponse response) throws InvalidActionException {
        response.setContentType("text");
        // get all data from ajax request
        ZonesManager zonesManager = ServletUtils.getZonesManager(getServletContext());
        UserManager userManager =  ServletUtils.getUserManager(getServletContext());
        String cart = request.getParameter(Constants.CART);
        String orders = request.getParameter(Constants.ORDERS);
        String map = request.getParameter(Constants.ID_TO_QUANTITY);
        String sales = request.getParameter(Constants.SALES_CHOSEN);
        String zone = request.getParameter(Constants.ZONE);
        Customer buyer = userManager.getUser(SessionUtils.getUsername(request));

        // transfer all data from JSON
        Gson gson = new Gson();
        JsonCart userCart = gson.fromJson(cart, JsonCart.class);
        Type mapType = new TypeToken<Map<Integer, Double>>() {}.getType();
        Type arrayType = new TypeToken<List<SalesChosen>>() {}.getType();
        Type arrayType2 = new TypeToken<List<OrderDTO>>() {}.getType();
        Map<Integer, Double> idToQuantity = gson.fromJson(map, mapType);
        List<SalesChosen> salesChosen = gson.fromJson(sales, arrayType);
        List<OrderDTO> finalOrders = gson.fromJson(orders, arrayType2);
        userCart.setIdToQuantity(idToQuantity);

        // transfer json to market objects
        Zone zoneSelected = zonesManager.getZone(zone);
        SalesUsed salesUsed = new SalesUsed(salesChosen);
        Cart finalCart = new Cart(userCart.getCustomer(), userCart.getStore(), userCart.getDate(), idToQuantity, userCart.getLocation());

        // add order!
        if(userCart.isDynamicOrder()){
            zoneSelected.getMarket().addDynamicOrder(salesUsed, finalOrders, userManager, buyer);
        } else {
            zoneSelected.getMarket().addOrder(finalCart, salesUsed, userManager, buyer);
        }
    }
}
