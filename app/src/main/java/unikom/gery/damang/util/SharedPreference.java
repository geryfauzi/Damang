package unikom.gery.damang.util;

import android.content.Context;
import android.content.SharedPreferences;

import unikom.gery.damang.model.User;

public class SharedPreference {
    private static final String firstTime = "firstTime";
    private static final String prefName = "damang_pref";
    private static final String isLoggedIn = "isLoggedIn";
    //User Session
    private static final String email = "email";
    private static final String name = "name";
    private static final String dateOfBirth = "dateOfBirth";
    private static final String gender = "gender";
    private static final String weight = "weight";
    private static final String height = "height";
    private static final String photo = "photo";
    //
    private static final String steps = "steps";
    private static final String heartRate = "heartRate";
    private static final String mode = "mode";
    private static final String sportId = "sportId";
    private static final String latitude = "latitude";
    private static final String longitude = "longitude";
    private final SharedPreferences sharedPreference;


    public SharedPreference(Context context) {
        sharedPreference = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public void setLoggedIn(Boolean value) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putBoolean(isLoggedIn, value);
        editor.apply();
    }

    public Boolean isLoggedIn() {
        return sharedPreference.getBoolean(isLoggedIn, false);
    }

    public void setFirstTime(Boolean value) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putBoolean(firstTime, value);
        editor.apply();
    }

    public Boolean isFirstTime() {
        return sharedPreference.getBoolean(firstTime, true);
    }

    public User getUser() {
        User user = new User();
        user.setEmail(sharedPreference.getString(email, ""));
        user.setName(sharedPreference.getString(name, ""));
        user.setDateofBirth(sharedPreference.getString(dateOfBirth, ""));
        user.setGender(sharedPreference.getString(gender, ""));
        user.setWeight(sharedPreference.getFloat(weight, 0));
        user.setHeight(sharedPreference.getFloat(height, 0));
        user.setPhoto(sharedPreference.getString(photo, ""));
        return user;
    }

    public void setUser(User value) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(email, value.getEmail());
        editor.putString(name, value.getName());
        editor.putString(dateOfBirth, value.getDateofBirth());
        editor.putString(gender, value.getGender());
        editor.putFloat(weight, value.getWeight());
        editor.putFloat(height, value.getHeight());
        editor.putString(photo, value.getPhoto());
        editor.apply();
    }

    public String getMode() {
        return sharedPreference.getString(mode, "Normal");
    }

    public void setMode(String value) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(mode, value);
        editor.apply();
    }

    public String getSportId() {
        return sharedPreference.getString(sportId, "null");
    }

    public void setSportId(String value) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(sportId, value);
        editor.apply();
    }

    //Ini hanya untuk area uji coba saja
    public int getHeartRate() {
        return sharedPreference.getInt(heartRate, 0);
    }

    public void setHeartRate(int value) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putInt(heartRate, value);
        editor.apply();
    }

    public int getSteps() {
        return sharedPreference.getInt(steps, 0);
    }

    public void setSteps(int value) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putInt(steps, value);
        editor.apply();
    }

    public void setLatitudeLongitude(double mLatitude, double mLongitude) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(latitude, String.valueOf(mLatitude));
        editor.putString(longitude, String.valueOf(mLongitude));
        editor.apply();
    }

    public void resetLatitudeLongitude() {
        SharedPreferences.Editor editor = sharedPreference.edit();
        String value = "null";
        editor.putString(latitude, value);
        editor.putString(longitude, value);
        editor.apply();
    }

    public String getLatitude() {
        return sharedPreference.getString(latitude, "null");
    }

    public String getLongitude() {
        return sharedPreference.getString(longitude, "null");
    }
}
