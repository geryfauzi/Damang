package unikom.gery.damang.sqlite.table;

import android.os.Parcel;
import android.os.Parcelable;

public class Sport implements Parcelable {
    public static final Creator<Sport> CREATOR = new Creator<Sport>() {
        @Override
        public Sport createFromParcel(Parcel source) {
            return new Sport(source);
        }

        @Override
        public Sport[] newArray(int size) {
            return new Sport[size];
        }
    };
    private String id;
    private String start_time;
    private String end_time;
    private int duration;
    private int tns_target;
    private String tns_status;
    private int average_heart_rate;
    private int calories_burned;
    private String type;
    private float distance;
    private String status;

    public Sport() {
    }

    protected Sport(Parcel in) {
        this.id = in.readString();
        this.start_time = in.readString();
        this.end_time = in.readString();
        this.duration = in.readInt();
        this.tns_target = in.readInt();
        this.tns_status = in.readString();
        this.average_heart_rate = in.readInt();
        this.calories_burned = in.readInt();
        this.type = in.readString();
        this.distance = in.readFloat();
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

    public int getTns_target() {
        return tns_target;
    }

    public void setTns_target(int tns_target) {
        this.tns_target = tns_target;
    }

    public String getTns_status() {
        return tns_status;
    }

    public void setTns_status(String tns_status) {
        this.tns_status = tns_status;
    }

    public int getAverage_heart_rate() {
        return average_heart_rate;
    }

    public void setAverage_heart_rate(int average_heart_rate) {
        this.average_heart_rate = average_heart_rate;
    }

    public int getCalories_burned() {
        return calories_burned;
    }

    public void setCalories_burned(int calories_burned) {
        this.calories_burned = calories_burned;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
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
        dest.writeInt(this.tns_target);
        dest.writeString(this.tns_status);
        dest.writeInt(this.average_heart_rate);
        dest.writeInt(this.calories_burned);
        dest.writeString(this.type);
        dest.writeFloat(this.distance);
        dest.writeString(this.status);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.start_time = source.readString();
        this.end_time = source.readString();
        this.duration = source.readInt();
        this.tns_target = source.readInt();
        this.tns_status = source.readString();
        this.average_heart_rate = source.readInt();
        this.calories_burned = source.readInt();
        this.type = source.readString();
        this.distance = source.readFloat();
        this.status = source.readString();
    }
}
