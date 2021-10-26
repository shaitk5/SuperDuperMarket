package market;

import enums.Rating;

public class Feedback {
    private String customerName;
    private String date;
    private Rating rating;
    private String comment;

    public Feedback(String customerName, String date, Rating rating, String comment) {
        this.customerName = customerName;
        this.date = date;
        this.rating = rating;
        this.comment = comment;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
