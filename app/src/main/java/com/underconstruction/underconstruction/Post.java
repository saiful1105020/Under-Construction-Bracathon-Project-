package com.underconstruction.underconstruction;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

/**
 *
 */
public class Post {
    //the global id of the post in main database
    private int postId;
    //the user who created the post in the first place
    private int userId;
    //the name of the above mentioned user
    private String userName;
    //the selected category of the problem reported
    private int category;
    // a byte array representation of the image
    private byte[] imageBytes;
    //a byte array representation of the video captured(not implemented)
    private byte[] videoBytes;
    //the location from which the post was given(derived form reverse geo-tagging)
    private String formalLocation;
    //the informal location given by the user
    private String informalLocation;
    //the description of the problem given by the user
    private String problemDescription;
    //the current status of the problem, solved, pending etc
    private int status;
    //the rating of the user who has reported the post
    private int userRating;
    //nnumber of upvotes received in this post
    private int upCount;
    //number of downvoted received in this post
    private int downCount;
    //when was the post reported?
    private String timeOfPost;
    //the id of the users who have upvoted the post
    private HashSet<Voter> upvotersId =new HashSet<Voter>();
    //the id of the users who have downvoted the post
    private HashSet<Voter> downvotersId=new HashSet<Voter>();
    //how much rating has changed due to this post
    private int ratingChange;


    /**
     * The constructor. All the fields have the above mentioned meaning. Not used in this project.
     * @param userId
     * @param category
     * @param downCount
     * @param formalLocation
     * @param imageBytes
     * @param informalLocation
     * @param postId
     * @param problemDescription
     * @param userRating
     * @param status
     * @param timeOfPost
     * @param upCount
     * @param userName
     * @param videoBytes
     * @param ratingChange
    */

    public Post(int userId, int category, int downCount, String formalLocation, byte[] imageBytes, String informalLocation, int postId, String problemDescription, int userRating, int status, String timeOfPost, int upCount, String userName, byte[] videoBytes, int ratingChange) {
        this.userId = userId;
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



    /**
     * Another constructor provided for flexibility. The parameters have the above mentioned meaning
     * @param userId
     * @param category
     * @param downCount
     * @param formalLocation
     * @param imageBytes
     * @param informalLocation
     * @param postId
     * @param problemDescription
     * @param userRating
     * @param timeOfPost
     * @param upCount
     * @param userName
     * @param videoBytes
     */

    public Post(int userId, int category, int downCount, String formalLocation, byte[] imageBytes, String informalLocation, int postId, String problemDescription, int userRating, String timeOfPost, int upCount, String userName, byte[] videoBytes) {
        this.userId = userId;
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


    /**
     * A new POst is created by extracting fields from a jsonObject
     * @param jsonObject a jsonObject containing all the fields of the post
     * @return A new Post Object
     */
    public static Post createPost(JSONObject jsonObject) {
        try {
            return new Post(
                    jsonObject.getInt("userId"),
                    jsonObject.getInt("category"),
                    jsonObject.getInt("downCount"),
                    jsonObject.getString("formalLocation"),
                    Base64.decode(jsonObject.getString("image"),0),
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



    //The getters and setters. They have the default meaning

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

    public boolean hasTheUserUpvoted(Voter voter){
        return upvotersId.contains(voter);
    }

    public boolean hasTheUserDownvoted(Voter voter){
        return downvotersId.contains(voter);
    }



    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Adds a new voter in this post. Can handle both upvote and downvote
     * @param voter A voter object which the id of the voter
     * @param voteType The type of vote, up-vote or down-vote
     */
    public void addVoter(Voter voter, int voteType){

        //an upvote has been cast
        if(voteType>0) {
        //can vote only if the voter has not voted before
            if (!upvotersId.contains(voter)) {
                upvotersId.add(voter);
                upCount++;
            }
        }
        //a downvote has been cast
        else {
            //can vote only if the voter has not voted before
            if (!downvotersId.contains(voter)) {
                downvotersId.add(voter);
                downCount++;
            }
        }
    }

    /**
     * Removes a new voter in this post. Can handle both upvote and downvote
     * @param voter A voter object which the id of the voter
     * @param voteType The type of vote, up-vote or down-vote
     */

    public void removeVoter(Voter voter, int voteType){
        //can remove upvote only if he has already given a upvote
        if(voteType>0) {                                    //upvote
            if (upvotersId.contains(voter)) {
                upvotersId.remove(voter);
                upCount--;
            }
        }
        //can remove downvote only if he has already given a upvote
        else {                                              //downvote
            if (downvotersId.contains(voter)) {
                downvotersId.remove(voter);
                downCount--;
            }
        }
    }

    /**
     * Used to load the votes already cast.
     * @param voter voter object with id
     * @param voteType upvote or downvote
     */
    public void addVoterFromDB(Voter voter, int voteType) {
        if(voteType>0)
            upvotersId.add(voter);
        else
            downvotersId.add(voter);
    }

}
