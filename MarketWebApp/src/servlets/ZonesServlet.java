package servlets;

import com.google.gson.Gson;
import market.Zone;
import utils.ServletUtils;
import utils.SessionUtils;
import zone.ZonesManager;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ZonesServlet", urlPatterns = "/pages/zone/zone")
public class ZonesServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        ZonesManager zonesManager = ServletUtils.getZonesManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
        }

        List<Zone> zones;
        synchronized (getServletContext()) {
            zones = zonesManager.getZoneEntries();
        }

        // log and create the response json string
        ZonesData zonesToSend = new ZonesData(zones);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(zonesToSend);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    private static class ZonesData {
        List<ZoneData> zones;

        public ZonesData(List<Zone> zonesReceived) {
            this.zones = new ArrayList<>();
            zonesReceived.forEach(zone -> zones.add(new ZoneData(zone)));
        }

        private static class ZoneData{
            private String name;
            private String owner;
            private int products;
            private int stores;
            private int orders;
            private double averageOrderPrice;

            public ZoneData(Zone zone) {
                this.name = zone.getName();
                this.owner = zone.getOwner();
                this.products = zone.getMarket().getNUmberOfProducts();
                this.stores = zone.getMarket().getNumberOfStores();
                this.orders = zone.getMarket().getNumberOfOrders();
                this.averageOrderPrice = zone.getMarket().getAverageOrderPrice();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }



}
