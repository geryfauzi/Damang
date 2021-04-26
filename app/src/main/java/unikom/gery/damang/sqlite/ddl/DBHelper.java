package unikom.gery.damang.sqlite.ddl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABSE_VERSION = 1;
    private static final String SQL_CREATE_TABLE_SPORT = String.format("CREATE TABLE %s" +
                    " (%s TEXT PRIMARY KEY NOT NULL, %s TEXT NOT NULL, %s TEXT, " +
                    "%s INTEGER, %s INTEGER NOT NULL, %s TEXT, %s INTEGER, " +
                    "%s INTEGER, %s TEXT NOT NULL, %s REAL, %s TEXT)",
            DBContract.TABLE_SPORT,
            DBContract.SportColumns._ID,
            DBContract.SportColumns.START_TIME,
            DBContract.SportColumns.END_TIME,
            DBContract.SportColumns.DURATION,
            DBContract.SportColumns.TNS_TARGET,
            DBContract.SportColumns.TNS_STATUS,
            DBContract.SportColumns.AVERAGE_HEART_RATE,
            DBContract.SportColumns.CALORIES_BURNED,
            DBContract.SportColumns.TYPE,
            DBContract.SportColumns.DISTANCE,
            DBContract.SportColumns.STATUS
    );
    private static final String SQL_CREATE_TABLE_SLEEP = String.format("CREATE TABLE %s" +
                    " (%s TEXT PRIMARY KEY NOT NULL, %s TEXT NOT NULL, %s TEXT," +
                    " %s INTEGER, %s INTEGER, %s TEXT)",
            DBContract.TABLE_SLEEP,
            DBContract.SleepColumns._ID,
            DBContract.SleepColumns.START_TIME,
            DBContract.SleepColumns.END_TIME,
            DBContract.SleepColumns.DURATION,
            DBContract.SleepColumns.AVERAGE_HEART_RATE,
            DBContract.SleepColumns.STATUS);

    private static final String SQL_CREATE_TABLE_USER = String.format("CREATE TABLE %s" +
                    " (%s TEXT PRIMARY KEY NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL," +
                    " %s REAL NOT NULL, %s REAL NOT NULL, %s TEXT NOT NULL)",
            DBContract.TABLE_USER,
            DBContract.UserColumns.EMAIL,
            DBContract.UserColumns.NAME,
            DBContract.UserColumns.DATE_OF_BIRTH,
            DBContract.UserColumns.GENDER,
            DBContract.UserColumns.WEIGHT,
            DBContract.UserColumns.HEIGHT,
            DBContract.UserColumns.PHOTO);

    private static final String SQL_CREATE_TABLE_HEART_RATE = String.format("CREATE TABLE %s" +
                    " (%s TEXT NOT NULL, %s TEXT, %s TEXT, %s TEXT NOT NULL, %s INTEGER NOT NULL, %s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, %s TEXT, %s TEXT," +
                    " FOREIGN KEY (%s) REFERENCES %s (%s), " +
                    "FOREIGN KEY (%s) REFERENCES %s (%s), " +
                    "FOREIGN KEY (%s) REFERENCES %s (%s))",
            DBContract.TABLE_HEART_RATE,
            DBContract.HeartRateColumn.EMAIL,
            DBContract.HeartRateColumn.ID_SPORT,
            DBContract.HeartRateColumn.ID_SLEEP,
            DBContract.HeartRateColumn.DATE_TIME,
            DBContract.HeartRateColumn.HEART_RATE,
            DBContract.HeartRateColumn.MODE,
            DBContract.HeartRateColumn.STATUS,
            DBContract.HeartRateColumn.LATITUDE,
            DBContract.HeartRateColumn.LONGITUDE,
            DBContract.HeartRateColumn.EMAIL, DBContract.TABLE_USER, DBContract.UserColumns.EMAIL,
            DBContract.HeartRateColumn.ID_SPORT, DBContract.TABLE_SPORT, DBContract.SportColumns._ID,
            DBContract.HeartRateColumn.ID_SLEEP, DBContract.TABLE_SLEEP, DBContract.SleepColumns._ID);

    public static String DATABASE_NAME = "db_damang";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_SLEEP);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_SPORT);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_USER);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_HEART_RATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.TABLE_HEART_RATE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.TABLE_SPORT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.TABLE_SLEEP);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.TABLE_USER);
        onCreate(sqLiteDatabase);
    }
}
