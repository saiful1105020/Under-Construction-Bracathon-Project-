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


    private String problemDescription;

    private String latitude;
    private String longitude;


    @Override
    public String toString() {
        return "Report{" +
                "recordID='" + recordID + '\'' +
                ", category='" + category + '\'' +

                ", time='" + time + '\'' +
                ", informalLocation='" + informalLocation + '\'' +
                ", problemDescription='" + problemDescription + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report)) return false;

        Report report = (Report) o;

        if (!getRecordID().equals(report.getRecordID())) return false;
        if (!getCategory().equals(report.getCategory())) return false;
        if (!Arrays.equals(getImage(), report.getImage())) return false;
        if (!getTime().equals(report.getTime())) return false;
        if (getInformalLocation() != null ? !getInformalLocation().equals(report.getInformalLocation()) : report.getInformalLocation() != null)
            return false;
        if (getProblemDescription() != null ? !getProblemDescription().equals(report.getProblemDescription()) : report.getProblemDescription() != null)
            return false;
        if (!getLatitude().equals(report.getLatitude())) return false;
        return getLongitude().equals(report.getLongitude());

    }

    @Override
    public int hashCode() {
        int result = getRecordID().hashCode();
        result = 31 * result + getCategory().hashCode();
        result = 31 * result + Arrays.hashCode(getImage());
        result = 31 * result + getTime().hashCode();
        result = 31 * result + (getInformalLocation() != null ? getInformalLocation().hashCode() : 0);
        result = 31 * result + (getProblemDescription() != null ? getProblemDescription().hashCode() : 0);
        result = 31 * result + getLatitude().hashCode();
        result = 31 * result + getLongitude().hashCode();
        return result;
    }

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
