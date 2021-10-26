package servlets;

import com.google.gson.Gson;
import enums.Rating;
import market.*;
import users.UserManager;
import utils.Constants;
import utils.ServletUtils;
import utils.SessionUtils;
import zone.ZonesManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FeedbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        customerFeedbacksRequest(request, response);
    }

    private void customerFeedbacksRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
        }

        Customer seller = userManager.getUser(username);
        int feedbackVersion = ServletUtils.getIntParameter(request, Constants.FEEDBACK_VERSION);
        if (feedbackVersion == Constants.INT_PARAMETER_ERROR) {
            return;
        }

        int currentUserFV = 0;
        List<Feedback> feedbacks;
        synchronized (getServletContext()) {
            currentUserFV = seller.getFeedbacksVersion();
            feedbacks = seller.getFeedbacksEntries(feedbackVersion);
        }

        FeedbacksAndVersion feedbacksAndVersion = new FeedbacksAndVersion(feedbacks,currentUserFV);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(feedbacksAndVersion);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }

    }

    private static class FeedbacksAndVersion {

        final private List<Feedback> feedbacks;
        final private int version;

        public FeedbacksAndVersion(List<Feedback> feedbacks, int version) {
            this.feedbacks = feedbacks;
            this.version = version;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        addFeedbackToStore(request, response);
    }

    private void addFeedbackToStore(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text");
        ZonesManager zonesManager = ServletUtils.getZonesManager(getServletContext());
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String customerName = SessionUtils.getUsername(request);

        String feedbackFromParameter = request.getParameter(Constants.FEEDBACK);
        String zoneFromParameter = request.getParameter(Constants.ZONE);

        Zone zone = zonesManager.getZone(zoneFromParameter);

        Gson gson = new Gson();
        FeedbackReceive feedback = gson.fromJson(feedbackFromParameter, FeedbackReceive.class);
        String ownerName = zone.getMarket().getStoreOwner(feedback.storeSerial);
        Customer owner = userManager.getUser(ownerName);
        owner.getUpdates().addFeedbackUpdate(new Updates.FeedbackUpdate(customerName, zoneFromParameter, feedback.rating, feedback.comment, feedback.date));
        owner.addFeedback(new Feedback(customerName, feedback.date, Rating.values()[feedback.rating - 1], feedback.comment));

        try(PrintWriter out = response.getWriter()){
            out.print("Feedback was added successfully");
            out.flush();
        }
    }

    private static class FeedbackReceive{
        int storeSerial;
        int rating;
        String date;
        String comment;

        public FeedbackReceive(int storeSerial, int rating, String date, String comment) {
            this.storeSerial = storeSerial;
            this.rating = rating;
            this.date = date;
            this.comment = comment;
        }
    }
}
