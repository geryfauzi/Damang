package unikom.gery.damang.test;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.Date;

public class TimeStampTest {

    @Test
    public void test(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp.getTime() / 1000L);
    }
}
