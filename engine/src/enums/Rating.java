package enums;

public enum Rating {
    STAR(1), TWO_STARS(2), THREE_STARS(3), FOUR_STARS(4), FIVE_STARS(5);

    private int rating;

    Rating(int rating) {
        this.rating = rating;
    }

    public int getNumVal() {
        return rating;
    }


    @Override
    public String toString() {
        return String.valueOf(this.getNumVal());
    }
}
