package unikom.gery.damang.sqlite.ddl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABSE_VERSION = 1;
    private static final String SQL_CREATE_TABLE_SPORT = String.format("CREATE TABLE %s" +
                    " (%s TEXT PRIMARY KEY NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL, " +
                    "%s INTEGER NOT NULL, %s INTEGER NOT NULL, %s TEXT NOT NULL, %s INTEGER NOT NULL, " +
                    "%s INTEGER NOT NULL, %s TEXT NOT NULL, %s REAL NOT NULL, %s TEXT NOT NULL)",
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
    public static String DATABASE_NAME = "db_damang";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_SPORT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.TABLE_SPORT);
        onCreate(sqLiteDatabase);
    }
}
