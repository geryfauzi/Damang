package unikom.gery.damang;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    private static final String firstTime = "firstTime";
    private static final String prefName = "damang_pref";
    private static final String isLoggedIn = "isLoggedIn";
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
}
