package com.underconstruction.underconstruction;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Anik on 5/8/2016.
 */
public class PostSuggestionItem {
    String voteCount;
    String informalLocation;
    String informalProblemDescription;
    String username;
    String date;
    byte[] img;

    public PostSuggestionItem(String voteCount, String informalLocation, String informalProblemDescription, String username, String date, byte[] img) {
        this.voteCount = voteCount;
        this.informalLocation = informalLocation;
        this.informalProblemDescription = informalProblemDescription;
        this.username = username;
        this.date = date;
        this.img = img;
    }

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
