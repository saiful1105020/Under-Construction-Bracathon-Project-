package com.underconstruction.underconstruction;

/**
 * Created by Shabab on 12/5/2015.
 * A Voter class to store the Id of a voter. The voter will be able to Upvote/Downvote in the posts of other user.
 * All the standard methods like constructor, getter, setter, hashcode, equals are implemented
 */
public class Voter {
    //the id of a voter in database

    private int voterId;


    public Voter(int voterId) {
        this.voterId = voterId;
    }

    public int getVoterId() {
        return voterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Voter)) return false;

        Voter voter = (Voter) o;

        if (getVoterId() != voter.getVoterId()) return false;
        else return true;
    }

    @Override
    public int hashCode() {
        int result = getVoterId();
        result = 31 * result + "test".hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Voter{" +
                "voterId=" + voterId +
                '}';
    }

    public void setVoterId(int voterId) {
        this.voterId = voterId;
    }



}
