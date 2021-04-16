package unikom.gery.damang.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int i) {
            return new User[i];
        }
    };
    private String email;
    private String name;
    private String dateofBirth;
    private String gender;
    private Float weight;
    private Float height;
    private String photo;

    public User() {

    }

    protected User(Parcel in) {
        email = in.readString();
        name = in.readString();
        dateofBirth = in.readString();
        gender = in.readString();
        weight = in.readFloat();
        height = in.readFloat();
        photo = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(dateofBirth);
        parcel.writeString(gender);
        parcel.writeDouble(height);
        parcel.writeDouble(weight);
        parcel.writeString(photo);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateofBirth() {
        return dateofBirth;
    }

    public void setDateofBirth(String dateofBirth) {
        this.dateofBirth = dateofBirth;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
