package com.underconstruction.underconstruction;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by wasif on 12/5/15.
 */
public class Report implements Serializable{



    private String recordID;
    private String category;
    private byte[] image;
    private String time;
    private String informalLocation;


    @Override
    public String toString() {
        return "Report{" +
                "category='" + category + '\'' +
                ", recordID='" + recordID + '\'' +
                ", time='" + time + '\'' +
                ", informalLocation='" + informalLocation + '\'' +
                ", userId='" + userId + '\'' +
                ", problemDescription='" + problemDescription + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Report report = (Report) o;

        if (recordID != null ? !recordID.equals(report.recordID) : report.recordID != null)
            return false;
        if (category != null ? !category.equals(report.category) : report.category != null)
            return false;
        if (!Arrays.equals(image, report.image)) return false;
        if (time != null ? !time.equals(report.time) : report.time != null) return false;
        if (!informalLocation.equals(report.informalLocation)) return false;
        if (userId != null ? !userId.equals(report.userId) : report.userId != null) return false;
        if (!problemDescription.equals(report.problemDescription)) return false;
        if (latitude != null ? !latitude.equals(report.latitude) : report.latitude != null)
            return false;
        return !(longitude != null ? !longitude.equals(report.longitude) : report.longitude != null);

    }

    @Override
    public int hashCode() {
        int result = recordID != null ? recordID.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (image != null ? Arrays.hashCode(image) : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + informalLocation.hashCode();
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + problemDescription.hashCode();
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        return result;
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public Report(String userId, String category, byte[] image, String informalLocation, String latitude, String longitude, String problemDescription, String recordID, String time) {
        this.userId = userId;
        this.category = category;
        this.image = image;
        this.informalLocation = informalLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.problemDescription = problemDescription;
        this.recordID = recordID;
        this.time = time;
    }

    public Report(String category, byte[] image, String time) {
        this.category = category;
        this.image = image;
        this.time = time;
    }

    private String problemDescription;

    private String latitude;
    private String longitude;


    public Report(String recordID, String category, byte[] image, String time, String informalLocation, String problemDescription, String latitude, String longitude) {
        this.recordID = recordID;
        this.category = category;
        this.image = image;
        this.time = time;
        this.informalLocation = informalLocation;

        this.problemDescription = problemDescription;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getInformalLocation() {
        return informalLocation;
    }

    public void setInformalLocation(String informalLocation) {
        this.informalLocation = informalLocation;
    }

    public String getRecordID() {
        return recordID;
    }

    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
