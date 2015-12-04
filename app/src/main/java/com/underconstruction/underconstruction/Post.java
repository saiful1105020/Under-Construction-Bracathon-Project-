package com.underconstruction.underconstruction;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;

public class Post {

    private int postId;
    private String userName;
    private int category;
    private ResultSet resultSet;
    private byte[] imageBytes;
    private byte[] videoBytes;
    private String formalLocation;
    private String informalLocation;
    private String problemDescription;
    private int status;
    private int userRating;
    private int ratingChange;
    private int upCount;
    private int downCount;
    private String timeOfPost;

    public Post(int category, int downCount, String formalLocation, byte[] imageBytes, String informalLocation, int postId, String problemDescription, int userRating, int status, String timeOfPost, int upCount, String userName, byte[] videoBytes, int ratingChange) {
        this.category = category;
        this.downCount = downCount;
        this.formalLocation = formalLocation;
        this.imageBytes = imageBytes;
        this.informalLocation = informalLocation;
        this.postId = postId;
        this.problemDescription = problemDescription;
        this.userRating = userRating;
        this.ratingChange = ratingChange;
        this.status = status;
        this.timeOfPost = timeOfPost;
        this.upCount = upCount;
        this.userName = userName;
        this.videoBytes = videoBytes;
    }

    public Post(int category, int downCount, String formalLocation, byte[] imageBytes, String informalLocation, int postId, String problemDescription, int userRating, String timeOfPost, int upCount, String userName, byte[] videoBytes) {
        this.category = category;
        this.downCount = downCount;
        this.formalLocation = formalLocation;
        this.imageBytes = imageBytes;
        this.informalLocation = informalLocation;
        this.postId = postId;
        this.problemDescription = problemDescription;
        this.userRating = userRating;
        this.timeOfPost = timeOfPost;
        this.upCount = upCount;
        this.userName = userName;
        this.videoBytes = videoBytes;
    }


    public static Post createPost(JSONObject jsonObject) {
        try {
            return new Post(
                    jsonObject.getInt("category"),
                    jsonObject.getInt("downCount"),
                    jsonObject.getString("formalLocation"),
                    null,
                    jsonObject.getString("informalLocation"),
                    jsonObject.getInt("postId"),
                    jsonObject.getString("problemDescription"),
                    jsonObject.getInt("userRating"),
                    jsonObject.getString("timeOfPost"),
                    jsonObject.getInt("upCount"),
                    jsonObject.getString("userName"),
                    null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getFormalLocation() {
        return formalLocation;
    }

    public void setFormalLocation(String formalLocation) {
        this.formalLocation = formalLocation;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public String getInformalLocation() {
        return informalLocation;
    }

    public void setInformalLocation(String informalLocation) {
        this.informalLocation = informalLocation;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTimeOfPost() {
        return timeOfPost;
    }

    public void setTimeOfPost(String timeOfPost) {
        this.timeOfPost = timeOfPost;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getVideoBytes() {
        return videoBytes;
    }

    public void setVideoBytes(byte[] videoBytes) {
        this.videoBytes = videoBytes;
    }

    public int getDownCount() {
        return downCount;
    }

    public void setDownCount(int downCount) {
        this.downCount = downCount;
    }

    public int getUpCount() {
        return upCount;
    }

    public void setUpCount(int upCount) {
        this.upCount = upCount;
    }
}
