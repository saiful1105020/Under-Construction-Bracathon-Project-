package com.underconstruction.underconstruction;

import org.json.JSONException;
import org.json.JSONObject;

public class YourPosts {
    String timeStamp;
    String exactLocation;

    @Override
    public String toString() {
        return "YourPosts{" +
                "timeStamp='" + timeStamp + '\'' +
                ", exactLocation='" + exactLocation + '\'' +
                ", category='" + category + '\'' +
                ", locationDescription='" + locationDescription + '\'' +
                ", problemDescription='" + problemDescription + '\'' +
                ", ratingChanged=" + ratingChanged +
                ", state=" + state +
                ", upVote=" + upVote +
                ", downVote=" + downVote +
                '}';
    }

    String category;
    String locationDescription;
    String problemDescription;

    int ratingChanged;
    int state;
    int upVote;
    int downVote;


    public String getTimeStamp() {
        return timeStamp;
    }

    public String getExactLocation() {
        return exactLocation;
    }

    public String getCategory() {
        return category;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public int getRatingChanged() {
        return ratingChanged;
    }

    public int getState() {
        return state;
    }

    public int getUpVote() {
        return upVote;
    }

    public int getDownVote() {
        return downVote;
    }

    public YourPosts(YourPosts y) {
        this.timeStamp = y.timeStamp;
        this.exactLocation = y.exactLocation;
        this.category = y.category;
        this.locationDescription = y.locationDescription;
        this.problemDescription = y.problemDescription;
        this.ratingChanged = y.ratingChanged;
        this.state = y.state;
        this.upVote = y.upVote;
        this.downVote = y.downVote;
    }
    public YourPosts(String timeStamp, String exactLocation, String category, String locationDescription, String problemDescription, int ratingChanged, int state, int upVote, int downVote) {

        this.timeStamp = timeStamp;
        this.exactLocation = exactLocation;
        this.category = category;
        this.locationDescription = locationDescription;
        this.problemDescription = problemDescription;
        this.ratingChanged = ratingChanged;
        this.state = state;
        this.upVote = upVote;
        this.downVote = downVote;
    }
    public YourPosts(Report r)
    {
        this.timeStamp = r.getTime();
        //this.exactLocation = r.getRoute() + ", " + r.getNeighborhood() + ", " + r.getNeighborhood();
        this.exactLocation = " ";
        this.category = r.getCategory();
        this.locationDescription = r.getInformalLocation();
        this.problemDescription = r.getProblemDescription();
        this.ratingChanged = 0;
        this.state = -1;
        this.upVote = 0;
        this.downVote = 0;
    }

    public static YourPosts createPost(JSONObject jsonObject) {
        try {
            return new YourPosts(
                    jsonObject.getString("timeStamp"),
                    jsonObject.getString("exactLocation"),
                    "" + jsonObject.getInt("category"),
                    jsonObject.getString("locationDescription"),
                    jsonObject.getString("problemDescription"),
                    jsonObject.getInt("ratingChanged"),
                    jsonObject.getInt("state"),
                    jsonObject.getInt("upVote"),
                    jsonObject.getInt("downVote")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
