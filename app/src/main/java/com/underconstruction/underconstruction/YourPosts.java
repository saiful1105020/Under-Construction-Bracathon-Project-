package com.underconstruction.underconstruction;

import org.json.JSONException;
import org.json.JSONObject;

public class YourPosts {
    //When did the user created the post
    String timeStamp;
    //the location from where it was reported(Not lat-long but address from reverse geotagging)
    String exactLocation;
    //the category of the problem
    String category;
    //the informal description of the location
    String locationDescription;
    //user given description of the problem
    String problemDescription;
    //How much rating changed due to this post
    int ratingChanged;
    //the current state of the post: verified, pending etc.
    int state;
    //number of upvoted in the post
    int upVote;
    //numbe rof downvoted in the post
    int downVote;



    /**
     * The contructor, All the parameters have the same meaning described above
     * @param timeStamp
     * @param exactLocation
     * @param category
     * @param locationDescription
     * @param problemDescription
     * @param ratingChanged
     * @param state
     * @param upVote
     * @param downVote
     */

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


    /**
     * creates a new YourPosts object from JSON
     * @param jsonObject the json object from which attributes of YourPosts are extracted
     * @return a YourPosts object
     */
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

    //The getters and setters. They have the default meaning

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




}
