package unikom.gery.damang.response;

import com.google.gson.annotations.SerializedName;

public class Place {
    @SerializedName("name")
    private String name;
    @SerializedName("lon")
    private String lon;
    @SerializedName("lat")
    private String lat;
    @SerializedName("address_line2")
    private String address_line2;

    public Place(String name, String lon, String lat, String address_line2) {
        this.name = name;
        this.lon = lon;
        this.lat = lat;
        this.address_line2 = address_line2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getAddress_line2() {
        return address_line2;
    }

    public void setAddress_line2(String address_line2) {
        this.address_line2 = address_line2;
    }
}
