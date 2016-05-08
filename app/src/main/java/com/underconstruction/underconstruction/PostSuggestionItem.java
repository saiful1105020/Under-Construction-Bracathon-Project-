package com.underconstruction.underconstruction;

/**
 * Created by Anik on 5/8/2016.
 */
public class PostSuggestionItem {
    String rating;
    String informalLocation;
    String informalProblemDescription;
    String username;
    String date;
    byte[] img;

    public PostSuggestionItem(String rating, String informalLocation, String informalProblemDescription, String username, String date, byte[] img) {
        this.rating = rating;
        this.informalLocation = informalLocation;
        this.informalProblemDescription = informalProblemDescription;
        this.username = username;
        this.date = date;
        this.img = img;
    }
}
