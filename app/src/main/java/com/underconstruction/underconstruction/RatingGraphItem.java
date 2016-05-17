package com.underconstruction.underconstruction;

/**
 * Created by wasif on 5/17/16.
 */
public class RatingGraphItem {

    int rating;
    String label;

    public RatingGraphItem(int rating, String label) {
        this.rating = rating;
        this.label = label;
    }

    @Override
    public String toString() {
        return "RatingGraphItem{" +
                "rating=" + rating +
                ", label='" + label + '\'' +
                '}';
    }
}
