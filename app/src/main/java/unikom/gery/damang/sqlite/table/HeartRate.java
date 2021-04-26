package unikom.gery.damang.sqlite.table;

import android.os.Parcel;
import android.os.Parcelable;

public class HeartRate implements Parcelable {

    public static final Creator<HeartRate> CREATOR = new Creator<HeartRate>() {
        @Override
        public HeartRate createFromParcel(Parcel source) {
            return new HeartRate(source);
        }

        @Override
        public HeartRate[] newArray(int size) {
            return new HeartRate[size];
        }
    };
    private String email;
    private String id_sport;
    private String id_sleep;
    private String date_time;
    private int heart_rate;
    private String mode;
    private String status;
    private String latitude;
    private String longitude;

    public HeartRate() {
    }

    protected HeartRate(Parcel in) {
        this.email = in.readString();
        this.id_sport = in.readString();
        this.id_sleep = in.readString();
        this.date_time = in.readString();
        this.heart_rate = in.readInt();
        this.mode = in.readString();
        this.status = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId_sport() {
        return id_sport;
    }

    public void setId_sport(String id_sport) {
        this.id_sport = id_sport;
    }

    public String getId_sleep() {
        return id_sleep;
    }

    public void setId_sleep(String id_sleep) {
        this.id_sleep = id_sleep;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public int getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(int heart_rate) {
        this.heart_rate = heart_rate;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.id_sport);
        dest.writeString(this.id_sleep);
        dest.writeString(this.date_time);
        dest.writeInt(this.heart_rate);
        dest.writeString(this.mode);
        dest.writeString(this.status);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
    }

    public void readFromParcel(Parcel source) {
        this.email = source.readString();
        this.id_sport = source.readString();
        this.id_sleep = source.readString();
        this.date_time = source.readString();
        this.heart_rate = source.readInt();
        this.mode = source.readString();
        this.status = source.readString();
        this.latitude = source.readString();
        this.longitude = source.readString();
    }
}
