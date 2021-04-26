package unikom.gery.damang.sqlite.dml;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import unikom.gery.damang.model.User;
import unikom.gery.damang.sqlite.ddl.DBHelper;
import unikom.gery.damang.sqlite.table.HeartRate;
import unikom.gery.damang.util.SharedPreference;

public class HeartRateHelper {
    static String TABLE_HEART_RATE = "heart_rate_activity";
    static String EMAIL = "email";
    static String ID_SPORT = "id_sport";
    static String ID_SLEEP = "id_sleep";
    static String DATE_TIME = "date_time";
    static String HEART_RATE = "heart_rate";
    static String MODE = "mode";
    static String STATUS = "status";
    static String LATITUDE = "latitude";
    static String LONGITUDE = "longitude";
    static String TABLE_USER = "user";
    static String NAME = "name";
    static String DATE_OF_BIRTH = "date_of_birth";
    static String GENDER = "gender";
    static String WEIGHT = "weight";
    static String HEIGHT = "height";
    static String PHOTO = "photo";
    private static DBHelper dbHelper;
    private static HeartRateHelper INSTANCE;
    private static SQLiteDatabase database;
    private SharedPreference sharedPreference;

    private HeartRateHelper(Context context) {
        dbHelper = new DBHelper(context);
        sharedPreference = new SharedPreference(context);
    }

    public static HeartRateHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HeartRateHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        if (database.isOpen()) {
            database.close();
        }
    }

    public long insertHeartRateNormalMode(HeartRate heartRate) {
        ContentValues args = new ContentValues();
        database = dbHelper.getWritableDatabase();
        args.put(EMAIL, heartRate.getEmail());
        args.put(DATE_TIME, heartRate.getDate_time());
        args.put(HEART_RATE, heartRate.getHeart_rate());
        args.put(MODE, heartRate.getMode());
        args.put(STATUS, heartRate.getStatus());
        sharedPreference.setHeartRate(heartRate.getHeart_rate());
        return database.insert(TABLE_HEART_RATE, null, args);
    }

    public long insertUser(User user) {
        ContentValues args = new ContentValues();
        database = dbHelper.getWritableDatabase();
        args.put(EMAIL, user.getEmail());
        args.put(NAME, user.getName());
        args.put(DATE_OF_BIRTH, user.getDateofBirth());
        args.put(GENDER, user.getGender());
        args.put(WEIGHT, user.getWeight());
        args.put(HEIGHT, user.getHeight());
        args.put(PHOTO, user.getPhoto());
        return database.insert(TABLE_USER, null, args);
    }

    public int getCurrentHeartRate(String email, String date) {
        int bpm = 0;
        Cursor cursor = database.rawQuery("SELECT avg(heart_rate) FROM heart_rate_activity WHERE email = ? AND date_time LIKE ? AND mode = ?", new String[]{email, "%" + date + "%", "Normal"});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                bpm = cursor.getInt(cursor.getColumnIndexOrThrow("avg(heart_rate)"));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return bpm;
    }

}
