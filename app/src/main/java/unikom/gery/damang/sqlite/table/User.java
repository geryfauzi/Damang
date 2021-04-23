package unikom.gery.damang.sqlite.table;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String email;
    private String name;
    private String date_of_birth;
    private String gender;
    private String weight;
    private String height;
    private String photo;

    public User() {
    }

    protected User(Parcel in) {
        this.email = in.readString();
        this.name = in.readString();
        this.date_of_birth = in.readString();
        this.gender = in.readString();
        this.weight = in.readString();
        this.height = in.readString();
        this.photo = in.readString();
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

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.name);
        dest.writeString(this.date_of_birth);
        dest.writeString(this.gender);
        dest.writeString(this.weight);
        dest.writeString(this.height);
        dest.writeString(this.photo);
    }

    public void readFromParcel(Parcel source) {
        this.email = source.readString();
        this.name = source.readString();
        this.date_of_birth = source.readString();
        this.gender = source.readString();
        this.weight = source.readString();
        this.height = source.readString();
        this.photo = source.readString();
    }
}
