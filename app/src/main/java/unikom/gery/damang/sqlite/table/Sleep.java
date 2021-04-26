package unikom.gery.damang.sqlite.table;

import android.os.Parcel;
import android.os.Parcelable;

public class Sleep implements Parcelable {
    public static final Creator<Sleep> CREATOR = new Creator<Sleep>() {
        @Override
        public Sleep createFromParcel(Parcel source) {
            return new Sleep(source);
        }

        @Override
        public Sleep[] newArray(int size) {
            return new Sleep[size];
        }
    };
    private String id;
    private String start_time;
    private String end_time;
    private int duration;
    private int average_heart_rate;
    private String status;

    public Sleep() {
    }

    protected Sleep(Parcel in) {
        this.id = in.readString();
        this.start_time = in.readString();
        this.end_time = in.readString();
        this.duration = in.readInt();
        this.average_heart_rate = in.readInt();
        this.status = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAverage_heart_rate() {
        return average_heart_rate;
    }

    public void setAverage_heart_rate(int average_heart_rate) {
        this.average_heart_rate = average_heart_rate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.start_time);
        dest.writeString(this.end_time);
        dest.writeInt(this.duration);
        dest.writeInt(this.average_heart_rate);
        dest.writeString(this.status);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.start_time = source.readString();
        this.end_time = source.readString();
        this.duration = source.readInt();
        this.average_heart_rate = source.readInt();
        this.status = source.readString();
    }
}
