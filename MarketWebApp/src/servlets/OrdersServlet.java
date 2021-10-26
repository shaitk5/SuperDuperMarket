package servlets;

import DTOClass.OrderDTO;
import DTOClass.SaleDTO;
import DTOClass.StoreDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import json.JsonCart;
import market.*;
import order.OrderManager;
import utils.Constants;
import utils.ServletUtils;
import utils.SessionUtils;
import zone.ZonesManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersServlet extends HttpServlet {

    private void processOrderRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        ZonesManager zonesManager = ServletUtils.getZonesManager(getServletContext());
        String cart = request.getParameter(Constants.CART);
        String map = request.getParameter(Constants.ID_TO_QUANTITY);

        // get cart data
        Gson gson = new Gson();
        JsonCart userCart = gson.fromJson(cart, JsonCart.class);
        Type mapType = new TypeToken<Map<Integer, Double>>() {}.getType();
        Map<Integer, Double> idToQuantity = gson.fromJson(map, mapType);
        userCart.setIdToQuantity(idToQuantity);

        // get sales
        OrderManager orderManager = ServletUtils.getOrderManager(getServletContext());
        IMarketEngine market = zonesManager.getZone(userCart.getZone()).getMarket();
        Map<Integer, StoreDTO> idToStore = market.getAllData(Boolean.FALSE);
        Map<SaleDTO, Integer> sales;
        List<OrderDTO> orders = null;
        if(userCart.isDynamicOrder()){
            orders = market.buildDynamicOrder(userCart.getIdToQuantity(), userCart.getDate(), userCart.getLocation(), userCart.getCustomer());
            sales = orderManager.getDynamicOrderSales(orders, idToStore);
        } else {
            sales = orderManager.getRegularOrderSales(idToStore, userCart);
        }

        // object to send
        SalesAndOrders salesAndOrders = new SalesAndOrders(sales, orders);
        String jsonResponse = gson.toJson(salesAndOrders);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        getUserOrders(request, response);
    }


    private void getUserOrders(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        ZonesManager zonesManager = ServletUtils.getZonesManager(getServletContext());
        String zoneNameFromParameter = request.getParameter(Constants.ZONE);
        String username = SessionUtils.getUsername(request);

        Gson gson = new Gson();
        List<OrderDTO> userOrders = zonesManager.getZone(zoneNameFromParameter).getMarket().getUserOrders(username);

        AllOrders allOrders = new AllOrders(userOrders);
        String jsonResponse = gson.toJson(allOrders);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processOrderRequest(request, response);
    }

    private class AllOrders{
        Map<Integer, List<OrderDTO>> idToOrder = new HashMap<>();

        AllOrders(List<OrderDTO> orders){
            orders.forEach(order -> {
                if(idToOrder.containsKey(order.getSerialNumber())){
                    idToOrder.get(order.getSerialNumber()).add(order);
                } else {
                    List<OrderDTO> list = new ArrayList<>();
                    list.add(order);
                    idToOrder.put(order.getSerialNumber(), list);
                }
            });
        }
    }

    private class SalesAndOrders{
        List<SaleDTO> sales = new ArrayList<>();
        List<OrderDTO> orders;

        SalesAndOrders(Map<SaleDTO, Integer> sales, List orders){
            addSales(sales);
            this.orders = orders;
        }

        private void addSales(Map<SaleDTO, Integer> salesToAdd){
            salesToAdd.keySet().forEach(sale -> {
                for(int i = 0; i<salesToAdd.get(sale); i++){
                    sales.add(new SaleDTO(sale));
                }});
        }
    }
}
