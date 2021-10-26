package servlets;

import chat.ChatMassage;
import com.google.gson.Gson;
import market.Customer;
import market.Updates;
import users.UserManager;
import utils.Constants;
import utils.ServletUtils;
import utils.SessionUtils;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class UpdateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        getUpdates(request, response);
    }

    private void getUpdates(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        int feedbackVersion = ServletUtils.getIntParameter(request, Constants.UPDATE_FEEDBACK_VERSION);
        int orderVersion = ServletUtils.getIntParameter(request, Constants.UPDATE_ORDER_VERSION);
        int newStoreVersion = ServletUtils.getIntParameter(request, Constants.UPDATE_NEW_STORE_VERSION);
        String username = SessionUtils.getUsername(request);

        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
        }

        if(feedbackVersion == Constants.INT_PARAMETER_ERROR || orderVersion == Constants.INT_PARAMETER_ERROR || newStoreVersion == Constants.INT_PARAMETER_ERROR){
            return;
        }

        Customer user = userManager.getUser(username);
        Updates updates = user.getUpdates();
        UpdatesAndVersions updatesToSend;
        synchronized (getServletContext()) {
            int managerFeedbackVersion = updates.getFeedbacksVersion();
            int managerOrderVersion = updates.getOrderVersion();
            int managerNewStoreVersion = updates.getNewStoreVersion();
            updatesToSend = new UpdatesAndVersions(managerFeedbackVersion, managerOrderVersion, managerNewStoreVersion,
                    updates.getFeedbackEntries(feedbackVersion), updates.getOrdersEntries(orderVersion), updates.getNewStoreEntries(newStoreVersion));
        }

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(updatesToSend);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    private static class UpdatesAndVersions{
        int feedbackVersion;
        int orderVersion;
        int newStoreVersion;
        List<Updates.FeedbackUpdate> feedbacks;
        List<Updates.OrderUpdate> orders;
        List<Updates.StoreUpdate> stores;

        public UpdatesAndVersions(int feedbackVersion, int orderVersion, int newStoreVersion, List<Updates.FeedbackUpdate> feedbacks, List<Updates.OrderUpdate> orders, List<Updates.StoreUpdate> stores) {
            this.feedbackVersion = feedbackVersion;
            this.orderVersion = orderVersion;
            this.newStoreVersion = newStoreVersion;
            this.feedbacks = feedbacks;
            this.orders = orders;
            this.stores = stores;
        }
    }
}
