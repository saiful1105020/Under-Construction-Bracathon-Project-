package com.underconstruction.underconstruction;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Anik on 5/8/2016.
 *
 * THis class holds the necessary data to hold and show a PostSuggestionItem. The items are shown when the user wants to report and there are some othe rmatching posts in the database
 *
 */
public class PostSuggestionItem {
    //NUmber of votes received by the post
    String voteCount;
    //informal location set up by the user
    String informalLocation;
    //informal description of the problem given by the user
    String informalProblemDescription;
    //the name of the user who posted the shown report
    String username;
    //the date on which the report was uploaded
    String date;
    //image captyred by the user
    byte[] img;


    public PostSuggestionItem(String voteCount, String informalLocation, String informalProblemDescription, String username, String date, byte[] img) {
        this.voteCount = voteCount;
        this.informalLocation = informalLocation;
        this.informalProblemDescription = informalProblemDescription;
        this.username = username;
        this.date = date;
        this.img = img;
    }

    /**
     * Creates a new PostSuggestionItem given a jsonObject that holds all the necessary data to extract the post
     * @param jsonObject an object holding the post in json format
     * @return a new PostSuggestionItem
     */
    public static PostSuggestionItem createPost(JSONObject jsonObject) {
        try {
            return new PostSuggestionItem(
                    jsonObject.getString("voteCount"),
                    jsonObject.getString("informalLocation"),
                    jsonObject.getString("problemDescription"),
                    jsonObject.getString("userName"),
                    jsonObject.getString("timeOfPost"),
                    Base64.decode(jsonObject.getString("image"), 0)
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "PostSuggestionItem{" +
                "date='" + date + '\'' +
                ", voteCount='" + voteCount + '\'' +
                ", informalLocation='" + informalLocation + '\'' +
                ", informalProblemDescription='" + informalProblemDescription + '\'' +
                ", username='" + username + '\'' +
                ", img=" + Arrays.toString(img) +
                '}';
    }
}
