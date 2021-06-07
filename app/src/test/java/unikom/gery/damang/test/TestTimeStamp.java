package unikom.gery.damang.test;

import org.junit.Test;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TestTimeStamp {

    @Test
    public void timeStamp() throws ParseException {
        DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String today = sdf.format(new Date(System.currentTimeMillis()));
        long epoch = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(today + " 23:59:59").getTime() / 1000;
        System.out.println(epoch);
    }
}
