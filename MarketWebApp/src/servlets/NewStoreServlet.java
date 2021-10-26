package servlets;

import DTOClass.StoreDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import json.JsonStore;
import market.Coordinates;
import market.Customer;
import market.Updates;
import users.UserManager;
import utils.Constants;
import utils.ServletUtils;
import zone.ZonesManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Map;

public class NewStoreServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        newStoreRequest(request, response);
    }

    private void newStoreRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text");
        ZonesManager zonesManager = ServletUtils.getZonesManager(getServletContext());
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        String zoneNameFromParameter = request.getParameter(Constants.ZONE);
        Customer zoneOwner = userManager.getUser(zonesManager.getZone(zoneNameFromParameter).getOwner());
        String idToPriceFromParameter = request.getParameter(Constants.ID_TO_PRICE);
        String storeFromParameter = request.getParameter(Constants.STORE);

        //
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<Integer, Double>>() {}.getType();
        Map<Integer, Double> idToPrice = gson.fromJson(idToPriceFromParameter, mapType);
        JsonStore store = gson.fromJson(storeFromParameter, JsonStore.class);
        store.setIdToPrice(idToPrice);

        synchronized (this){
            zoneOwner.getUpdates().addStoreUpdate(new Updates.StoreUpdate(store.getOwner(), store.getName(), zoneNameFromParameter, store.getLocation(),
                    store.getIdToPrice().size(), zonesManager.getZone(zoneNameFromParameter).getMarket().getNUmberOfProducts()));
            zonesManager.addStore(store, zoneNameFromParameter);
            PrintWriter out = response.getWriter();
            out.print("Store added successfully!");
            out.flush();
        }
    }

}
