package unikom.gery.damang.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceResponse {
    @SerializedName("type")
    private String type;
    @SerializedName("features")
    private List<Properties> features;

    public PlaceResponse(String type, List<Properties> features) {
        this.type = type;
        this.features = features;
    }

    public String getType() {
        return type;
    }

    public List<Properties> getFeatures() {
        return features;
    }
}
