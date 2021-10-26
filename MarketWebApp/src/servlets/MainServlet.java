package servlets;

import DTOClass.ProductDTO;
import DTOClass.StoreDTO;
import com.google.gson.Gson;
import market.Zone;
import users.UserManager;
import utils.*;
import zone.ZonesManager;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(name = "MainServlet", urlPatterns = "/Main")
public class MainServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        ZonesManager zonesManager = ServletUtils.getZonesManager(getServletContext());
        String zoneNameFromParameter = request.getParameter(Constants.ZONE);

        Gson gson = new Gson();
        Zone zone = zonesManager.getZone(zoneNameFromParameter);
        ZoneDTO zoneDTO = new ZoneDTO(zone);
        String jsonResponse = gson.toJson(zoneDTO);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    protected void checkUserName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String usernameFromParameter = request.getParameter(Constants.USERNAME);
        usernameFromParameter = usernameFromParameter.trim();
        String massage = null;
        synchronized (this) {
            if(userManager.isUserExists(usernameFromParameter)){
                massage = "Username " + usernameFromParameter + " already exists";
            } else if (!usernameFromParameter.isEmpty()){
                massage ="Username available";
            }
            try (PrintWriter out = response.getWriter()) {
                out.write(massage);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkUserName(request, response);
    }


    public static class ZoneDTO{
        private final String name;
        private final String owner;
        private final List<StoreDTO> stores;
        private final List<ProductDTO> products;

        public ZoneDTO(Zone zone) {
            this.name = zone.getName();
            this.owner = zone.getOwner();
            Map<Integer, StoreDTO> allData = zone.getMarket().getAllData(true);
            this.stores = new ArrayList<>(allData.values());
            Map<Integer, ProductDTO> allProducts = zone.getMarket().getAllProducts();
            this.products = new ArrayList<>(allProducts.values());
        }
    }
}
