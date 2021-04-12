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
    private static final String weight = "weight";
    private static final String height = "height";
    private static final String photo = "photo";
    //
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

    public void setUser(User value) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(email, value.getEmail());
        editor.putString(name, value.getName());
        editor.putString(dateOfBirth, value.getDateofBirth());
        editor.putFloat(weight, value.getWeight());
        editor.putFloat(height, value.getHeight());
        editor.putString(photo, value.getPhoto());
        editor.apply();
    }
}
