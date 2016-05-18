package com.underconstruction.underconstruction;

/**
 * Created by wasif on 5/17/16.
 * THis class is used to draw the rating graph of the user. THe label is the datem and the rating is the point at that time
 *
 */
public class RatingGraphItem {

    //A date
    String label;
    //rating on that date
    int rating;


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
