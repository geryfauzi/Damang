package unikom.gery.damang.response;

import com.google.gson.annotations.SerializedName;

public class CheckUser {
    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("dateofBirth")
    private String dateofBirth;
    @SerializedName("gender")
    private String gender;
    @SerializedName("weight")
    private Float weight;
    @SerializedName("height")
    private Float height;
    @SerializedName("photo")
    private String photo;

    public CheckUser(int code, String message, String email, String name, String dateofBirth, String gender, Float weight, Float height, String photo) {
        this.code = code;
        this.message = message;
        this.email = email;
        this.name = name;
        this.dateofBirth = dateofBirth;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.photo = photo;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getDateofBirth() {
        return dateofBirth;
    }

    public Float getWeight() {
        return weight;
    }

    public String getGender() {
        return gender;
    }

    public Float getHeight() {
        return height;
    }

    public String getPhoto() {
        return photo;
    }
}
