package unikom.gery.damang.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailHeartRate implements Parcelable {
    public static final Creator<DetailHeartRate> CREATOR = new Creator<DetailHeartRate>() {
        @Override
        public DetailHeartRate createFromParcel(Parcel source) {
            return new DetailHeartRate(source);
        }

        @Override
        public DetailHeartRate[] newArray(int size) {
            return new DetailHeartRate[size];
        }
    };
    private String hour;
    private int heartRate;

    public DetailHeartRate() {

    }

    public DetailHeartRate(String hour, int heartRate) {
        this.hour = hour;
        this.heartRate = heartRate;
    }

    protected DetailHeartRate(Parcel in) {
        this.hour = in.readString();
        this.heartRate = in.readInt();
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.hour);
        dest.writeInt(this.heartRate);
    }

    public void readFromParcel(Parcel source) {
        this.hour = source.readString();
        this.heartRate = source.readInt();
    }
}
