package market;

import java.util.ArrayList;
import java.util.List;

public class Seller extends Customer {

    private final String TYPE = "Seller";
    private List<Feedback> feedbacks = new ArrayList<>();
    private Updates updates = new Updates();


    public Seller(String name) {
        super(name);
    }

    public List<Feedback> getFeedbacks() {
        return this.feedbacks;
    }

    @Override
    public void addFeedback(Feedback feedback) {
        this.feedbacks.add(feedback);
    }

    @Override
    public Updates getUpdates() {
        return this.updates;
    }

    @Override
    public List<Feedback> getFeedbacksEntries(int fromIndex){
        if (fromIndex < 0 || fromIndex > feedbacks.size()) {
            fromIndex = 0;
        }
        return feedbacks.subList(fromIndex, feedbacks.size());
    }

    @Override
    public int getFeedbacksVersion() {
        return feedbacks.size();
    }
}