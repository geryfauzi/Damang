package unikom.gery.damang.sqlite.ddl;

import android.provider.BaseColumns;

public class DBContract {
    static String TABLE_SPORT = "sport_activity";
    static String TABLE_SLEEP = "sleep_activity";
    static String TABLE_HEART_RATE = "heart_rate_activity";
    static String TABLE_USER = "user";

    static final class SportColumns implements BaseColumns {
        static String START_TIME = "start_time";
        static String END_TIME = "end_time";
        static String DURATION = "duration";
        static String TNS_TARGET = "tns_target";
        static String TNS_STATUS = "tns_status";
        static String AVERAGE_HEART_RATE = "average_heart_rate";
        static String CALORIES_BURNED = "calories_burned";
        static String TYPE = "type";
        static String DISTANCE = "distance";
        static String STATUS = "status";
    }

    static final class SleepColumns implements BaseColumns {
        static String START_TIME = "start_time";
        static String END_TIME = "end_time";
        static String DURATION = "duration";
        static String AVERAGE_HEART_RATE = "average_heart_rate";
        static String STATUS = "status";
    }

    static final class UserColumns implements BaseColumns {
        static String EMAIL = "email";
        static String NAME = "name";
        static String DATE_OF_BIRTH = "date_of_birth";
        static String GENDER = "gender";
        static String WEIGHT = "weight";
        static String HEIGHT = "height";
        static String PHOTO = "photo";
    }
}
