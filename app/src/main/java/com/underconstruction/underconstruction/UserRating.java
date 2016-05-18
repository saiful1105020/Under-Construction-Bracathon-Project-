package com.underconstruction.underconstruction;

import java.util.Stack;

/**
 * Created by wasif on 5/17/16.
 * THis class holds all the necessary data and methods for showing user rating point and graph.Is used by the
 */
public class UserRating {

    //an stack containing items to draw the rating graph
    private Stack<RatingGraphItem> allRatingGraphItems;
    //the current rating point of the user
    private int userRatingPoint;
    //the max rating achieved
    private int maxRating;
    //the min rating
    private int minRating;


    public UserRating(Stack<RatingGraphItem> allRatingGraphItems, int userRatingPoint, int maxRating, int minRating) {

        this.allRatingGraphItems = allRatingGraphItems;
        this.userRatingPoint = userRatingPoint;
        this.maxRating = maxRating;
        this.minRating = minRating;
    }

    @Override
    public String toString() {
        return "UserRating{" +
                "allRatingGraphItems=" + allRatingGraphItems +
                ", userRatingPoint=" + userRatingPoint +
                ", maxRating=" + maxRating +
                ", minRating=" + minRating +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRating)) return false;

        UserRating that = (UserRating) o;

        if (getUserRatingPoint() != that.getUserRatingPoint()) return false;
        if (getMaxRating() != that.getMaxRating()) return false;
        if (getMinRating() != that.getMinRating()) return false;
        return getAllRatingGraphItems().equals(that.getAllRatingGraphItems());

    }

    @Override
    public int hashCode() {
        int result = getAllRatingGraphItems().hashCode();
        result = 31 * result + getUserRatingPoint();
        result = 31 * result + getMaxRating();
        result = 31 * result + getMinRating();
        return result;
    }

    //All the getters, setters have the usual meaning

    public int getMaxRating() {

        return maxRating;
    }

    public void setMaxRating(int maxRating) {
        this.maxRating = maxRating;
    }

    public int getMinRating() {
        return minRating;
    }

    public void setMinRating(int minRating) {
        this.minRating = minRating;
    }




    public Stack<RatingGraphItem> getAllRatingGraphItems() {

        return allRatingGraphItems;
    }

    public void setAllRatingGraphItems(Stack<RatingGraphItem> allRatingGraphItems) {
        this.allRatingGraphItems = allRatingGraphItems;
    }

    public int getUserRatingPoint() {
        return userRatingPoint;
    }

    public void setUserRatingPoint(int userRatingPoint) {
        this.userRatingPoint = userRatingPoint;
    }
}
