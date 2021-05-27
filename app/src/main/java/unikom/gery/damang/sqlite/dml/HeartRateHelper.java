package unikom.gery.damang.sqlite.dml;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import unikom.gery.damang.model.DetailHeartRate;
import unikom.gery.damang.model.User;
import unikom.gery.damang.sqlite.ddl.DBHelper;
import unikom.gery.damang.sqlite.table.HeartRate;
import unikom.gery.damang.sqlite.table.Sport;
import unikom.gery.damang.util.SharedPreference;

public class HeartRateHelper {
    static String TABLE_HEART_RATE = "heart_rate_activity";
    static String TABLE_SPORT = "sport_activity";
    static String EMAIL = "email";
    static String _ID = "_id";
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
    static String START_TIME = "start_time";
    static String TNS_TARGET = "tns_target";
    static String TYPE = "type";
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

    public long insertHeartRateSportMode(HeartRate heartRate) {
        ContentValues args = new ContentValues();
        database = dbHelper.getWritableDatabase();
        args.put(EMAIL, heartRate.getEmail());
        args.put(ID_SPORT, heartRate.getId_sport());
        args.put(DATE_TIME, heartRate.getDate_time());
        args.put(HEART_RATE, heartRate.getHeart_rate());
        args.put(MODE, heartRate.getMode());
        args.put(STATUS, heartRate.getStatus());
        sharedPreference.setHeartRate(heartRate.getHeart_rate());
        return database.insert(TABLE_HEART_RATE, null, args);
    }

    public long insertSportData(Sport sport) {
        ContentValues args = new ContentValues();
        database = dbHelper.getWritableDatabase();
        args.put(_ID, sport.getId());
        args.put(START_TIME, sport.getStart_time());
        args.put(TNS_TARGET, sport.getTns_target());
        args.put(TYPE, sport.getType());
        return database.insert(TABLE_SPORT, null, args);
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
        database = dbHelper.getWritableDatabase();
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

    public ArrayList<unikom.gery.damang.model.HeartRate> getDailyCondition(String email) {
        database = dbHelper.getWritableDatabase();
        ArrayList<unikom.gery.damang.model.HeartRate> arrayList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT DATE(date_time), AVG(heart_rate) FROM heart_rate_activity WHERE email = ? AND mode = ? GROUP BY DATE(date_time) ORDER BY DATE(date_time) DESC", new String[]{email, "Normal"});
        cursor.moveToFirst();
        unikom.gery.damang.model.HeartRate heartRate;
        if (cursor.getCount() > 0) {
            do {
                heartRate = new unikom.gery.damang.model.HeartRate();
                heartRate.setDate(cursor.getString(cursor.getColumnIndexOrThrow("DATE(date_time)")));
                heartRate.setAverageHeartRate(cursor.getInt(cursor.getColumnIndexOrThrow("AVG(heart_rate)")));
                arrayList.add(heartRate);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<DetailHeartRate> getDetailDailyCondition(String email, String date) {
        database = dbHelper.getWritableDatabase();
        ArrayList<DetailHeartRate> arrayList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT strftime(?,date_time) AS hour, heart_rate FROM heart_rate_activity WHERE DATE(date_time) = ? AND email = ? AND mode = ?", new String[]{"%H:%M", date, email, "Normal"});
        cursor.moveToFirst();
        DetailHeartRate heartRate;
        if (cursor.getCount() > 0) {
            do {
                heartRate = new DetailHeartRate();
                heartRate.setHour(cursor.getString(cursor.getColumnIndexOrThrow("hour")));
                heartRate.setHeartRate(cursor.getInt(cursor.getColumnIndexOrThrow("heart_rate")));
                arrayList.add(heartRate);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

}
