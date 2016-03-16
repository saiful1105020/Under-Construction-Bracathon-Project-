package com.underconstruction.underconstruction;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by wasif on 12/5/15.
 */
public class Report implements Serializable{

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private String userName;

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", category='" + category + '\'' +
                //", image=" + Arrays.toString(image) +
                ", time='" + time + '\'' +
                ", informalLocation='" + informalLocation + '\'' +
                ", problemDescription='" + problemDescription + '\'' +
                ", streetNo='" + streetNo + '\'' +
                ", route='" + route + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", sublocality='" + sublocality + '\'' +
                ", locality='" + locality + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }

    private String category;
    private byte[] image;
    private String time;
    private String informalLocation;
    private String problemDescription;
    private String streetNo;
    private String route;



    private String neighborhood;
    private String sublocality;
    private String locality;
    private String latitude;
    private String longitude;

    public Report(int id,int userId, String userName, String category, byte[] image, String time, String informalLocation, String problemDescription, String streetNo, String route, String neighborhood, String sublocality, String locality, String latitude, String longitude) {
        this.id=id;
        this.userId = userId;
        this.userName = userName;
        this.category = category;
        this.image = image;
        this.time = time;
        this.informalLocation = informalLocation;
        this.problemDescription = problemDescription;
        this.streetNo = streetNo;
        this.route = route;
        this.neighborhood = neighborhood;
        this.sublocality = sublocality;
        this.locality = locality;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getInformalLocation() {
        return informalLocation;
    }

    public void setInformalLocation(String informalLocation) {
        this.informalLocation = informalLocation;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getSublocality() {
        return sublocality;
    }

    public void setSublocality(String sublocality) {
        this.sublocality = sublocality;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report)) return false;

        Report report = (Report) o;

        if (!getUserName().equals(report.getUserName())) return false;
        if (!getCategory().equals(report.getCategory())) return false;
        if (!Arrays.equals(getImage(), report.getImage())) return false;
        if (!getTime().equals(report.getTime())) return false;
        if (getInformalLocation() != null ? !getInformalLocation().equals(report.getInformalLocation()) : report.getInformalLocation() != null)
            return false;
        if (getProblemDescription() != null ? !getProblemDescription().equals(report.getProblemDescription()) : report.getProblemDescription() != null)
            return false;
        if (getStreetNo() != null ? !getStreetNo().equals(report.getStreetNo()) : report.getStreetNo() != null)
            return false;
        if (getRoute() != null ? !getRoute().equals(report.getRoute()) : report.getRoute() != null)
            return false;
        if (getNeighborhood() != null ? !getNeighborhood().equals(report.getNeighborhood()) : report.getNeighborhood() != null)
            return false;
        if (getSublocality() != null ? !getSublocality().equals(report.getSublocality()) : report.getSublocality() != null)
            return false;
        if (getLocality() != null ? !getLocality().equals(report.getLocality()) : report.getLocality() != null)
            return false;
        if (!getLatitude().equals(report.getLatitude())) return false;
        return getLongitude().equals(report.getLongitude());

    }

    @Override
    public int hashCode() {
        int result = getUserName().hashCode();
        result = 31 * result + getCategory().hashCode();
        result = 31 * result + Arrays.hashCode(getImage());
        result = 31 * result + getTime().hashCode();
        result = 31 * result + (getInformalLocation() != null ? getInformalLocation().hashCode() : 0);
        result = 31 * result + (getProblemDescription() != null ? getProblemDescription().hashCode() : 0);
        result = 31 * result + (getStreetNo() != null ? getStreetNo().hashCode() : 0);
        result = 31 * result + (getRoute() != null ? getRoute().hashCode() : 0);
        result = 31 * result + (getNeighborhood() != null ? getNeighborhood().hashCode() : 0);
        result = 31 * result + (getSublocality() != null ? getSublocality().hashCode() : 0);
        result = 31 * result + (getLocality() != null ? getLocality().hashCode() : 0);
        result = 31 * result + getLatitude().hashCode();
        result = 31 * result + getLongitude().hashCode();
        return result;
    }

}
