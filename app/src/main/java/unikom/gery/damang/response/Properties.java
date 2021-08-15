package unikom.gery.damang.response;

import com.google.gson.annotations.SerializedName;

public class Properties {
    @SerializedName("type")
    private String type;
    @SerializedName("properties")
    private Place properties;

    public Properties(String type, Place properties){
        this.type = type;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Place getProperties() {
        return properties;
    }

    public void setProperties(Place properties) {
        this.properties = properties;
    }
}
