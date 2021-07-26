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
    static String END_TIME = "end_time";
    static String DURATION = "duration";
    static String TNS_STATUS = "tns_status";
    static String AVERAGE_HEART_RATE = "average_heart_rate";
    static String CALORIES_BURNED = "calories_burned";
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
    static String DISTANCE = "distance";
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
        if (heartRate.getLatitude() != null) {
            args.put(LATITUDE, heartRate.getLatitude());
            args.put(LONGITUDE, heartRate.getLongitude());
        }
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

    public int updateSportData(Sport sport) {
        ContentValues args = new ContentValues();
        database = dbHelper.getWritableDatabase();
        args.put(END_TIME, sport.getEnd_time());
        args.put(DURATION, sport.getDuration());
        args.put(TNS_STATUS, sport.getTns_status());
        args.put(AVERAGE_HEART_RATE, sport.getAverage_heart_rate());
        args.put(CALORIES_BURNED, sport.getCalories_burned());
        if (sport.getDistance() != 0.0f)
            args.put(DISTANCE, sport.getDistance());
        return database.update(TABLE_SPORT, args, _ID + "= '" + sport.getId() + "'", null);
    }

    public int deleteSportData(String id) {
        database = dbHelper.getWritableDatabase();
        return database.delete(TABLE_SPORT, _ID + " = '" + id + "'", null);
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

    public int getLatesHeartRateSportMode(String id, String email) {
        database = dbHelper.getWritableDatabase();
        int bpm = 0;
        Cursor cursor = database.rawQuery("SELECT heart_rate FROM heart_rate_activity WHERE email = ? AND id_sport = ? ORDER BY date_time DESC LIMIT 1", new String[]{email, id});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                bpm = cursor.getInt(cursor.getColumnIndexOrThrow("heart_rate"));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return bpm;
    }

    public boolean checkHeartRateSportMode(String id, String email) {
        database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT heart_rate FROM heart_rate_activity WHERE email = ? AND id_sport = ? ORDER BY date_time DESC", new String[]{email, id});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public int getAverageSportHearRate(String id, String email) {
        database = dbHelper.getWritableDatabase();
        int bpm = 0;
        Cursor cursor = database.rawQuery("SELECT avg(heart_rate) FROM heart_rate_activity WHERE email = ? AND id_sport = ? ", new String[]{email, id});
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

    public Sport getOtherSportDetail(String id) {
        database = dbHelper.getWritableDatabase();
        Sport sport = new Sport();
        Cursor cursor = database.rawQuery("SELECT DATE(start_time), time(start_time), time(end_time), duration, tns_target, tns_status, average_heart_rate, calories_burned FROM sport_activity WHERE _id = ? ", new String[]{id});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                sport.setId(cursor.getString(cursor.getColumnIndexOrThrow("DATE(start_time)")));
                sport.setStart_time(cursor.getString(cursor.getColumnIndexOrThrow("time(start_time)")));
                sport.setEnd_time(cursor.getString(cursor.getColumnIndexOrThrow("time(end_time)")));
                sport.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow("duration")));
                sport.setTns_target(cursor.getInt(cursor.getColumnIndexOrThrow("tns_target")));
                sport.setTns_status(cursor.getString(cursor.getColumnIndexOrThrow("tns_status")));
                sport.setAverage_heart_rate(cursor.getInt(cursor.getColumnIndexOrThrow("average_heart_rate")));
                sport.setCalories_burned(cursor.getInt(cursor.getColumnIndexOrThrow("calories_burned")));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return sport;
    }

    public ArrayList<DetailHeartRate> getSportDetailHeartRate(String email, String id) {
        database = dbHelper.getWritableDatabase();
        ArrayList<DetailHeartRate> arrayList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT strftime(?,date_time) AS hour, heart_rate FROM heart_rate_activity WHERE email = ? AND id_sport = ? ", new String[]{"%H:%M", email, id});
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

    public ArrayList<unikom.gery.damang.model.HeartRate> getDailyCondition(String email) {
        database = dbHelper.getWritableDatabase();
        ArrayList<unikom.gery.damang.model.HeartRate> arrayList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT DATE(date_time), AVG(heart_rate) FROM heart_rate_activity WHERE email = ? AND mode = ? GROUP BY DATE(date_time) ORDER BY DATE(date_time) DESC", new String[]{email, "Normal"});
        cursor.moveToFirst();
        unikom.gery.damang.model.HeartRate heartRate;
        if (cursor.getCount() > 0) {
            do {
                Cursor cLastHeartRate = database.rawQuery("SELECT date_time, heart_rate FROM heart_rate_activity WHERE email = ? AND mode = ? AND DATE(date_time) = ? ORDER BY date_time DESC LIMIT 1", new String[]{email, "Normal", cursor.getString(cursor.getColumnIndexOrThrow("DATE(date_time)"))});
                cLastHeartRate.moveToFirst();
                heartRate = new unikom.gery.damang.model.HeartRate();
                heartRate.setArrayList(getDetailDailyCondition(email, cursor.getString(cursor.getColumnIndexOrThrow("DATE(date_time)"))));
                heartRate.setDate(cursor.getString(cursor.getColumnIndexOrThrow("DATE(date_time)")));
                heartRate.setAverageHeartRate(cursor.getInt(cursor.getColumnIndexOrThrow("AVG(heart_rate)")));
                heartRate.setCurrentHeartRate(cLastHeartRate.getInt(cLastHeartRate.getColumnIndexOrThrow("heart_rate")));
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

    public ArrayList<Sport> getSportData() {
        database = dbHelper.getWritableDatabase();
        ArrayList<Sport> arrayList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT _id, average_heart_rate, type, DATE(start_time) FROM sport_activity ORDER BY DATE(start_time) DESC LIMIT 3", new String[]{});
        cursor.moveToFirst();
        Sport sport;
        if (cursor.getCount() > 0) {
            do {
                sport = new Sport();
                sport.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                sport.setAverage_heart_rate(cursor.getInt(cursor.getColumnIndexOrThrow("average_heart_rate")));
                sport.setStart_time(cursor.getString(cursor.getColumnIndexOrThrow("DATE(start_time)")));
                sport.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                arrayList.add(sport);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

}
