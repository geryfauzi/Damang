package unikom.gery.damang.test;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import unikom.gery.damang.devices.amazfitbip.BipActivitySummary;
import unikom.gery.damang.entities.Device;
import unikom.gery.damang.entities.User;
import unikom.gery.damang.export.GPXExporter;
import unikom.gery.damang.model.ActivityPoint;
import unikom.gery.damang.model.ActivityTrack;
import unikom.gery.damang.service.btle.BLETypeConversions;
import unikom.gery.damang.service.devices.huami.HuamiActivityDetailsParser;
import unikom.gery.damang.util.DateTimeUtils;
import unikom.gery.damang.util.FileUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HuamiActivityDetailsParserTest extends TestBase {
    private static final URL DETAILS_1 = HuamiActivityDetailsParserTest.class.getClassLoader().getResource("ActivityDetailsDump1.txt");
    private static final long MAX_DETAILS = 1024 * 1024;
    private static Date baseTime;

    @BeforeClass
    public static void setUpSuite() throws Exception {
        baseTime = DateTimeUtils.ISO_8601_FORMAT.parse("2017-01-20T14:00:00-00:00"); // yyyy-mm-dd'T'hh:mm:ssZ
    }

    @Test
    public void testActivityDetails() throws Exception {
        BipActivitySummary summary = createSummary();

        HuamiActivityDetailsParser parser = new HuamiActivityDetailsParser(summary);
        parser.setSkipCounterByte(true);
        try (InputStream in = getContents(DETAILS_1)) {
            ActivityTrack track = parser.parse(FileUtils.readAll(in, MAX_DETAILS));
            assertEquals("SuperBand 2000", track.getDevice().getName());
            assertEquals("Elvis", track.getUser().getName());

            List<ActivityPoint> trackPoints = track.getTrackPoints();
            assertEquals(972, trackPoints.size());
        }
    }

    private BipActivitySummary createSummary() {
        BipActivitySummary summary = new BipActivitySummary();
        summary.setBaseLongitude(1);
        summary.setBaseLatitude(1);
        summary.setBaseAltitude(1);
        summary.setStartTime(baseTime);
        User dummyUser = new User(0L);
        dummyUser.setName("Elvis");
        summary.setName("testtrack");
        summary.setUser(dummyUser);
        Device device = new Device(0l);
        device.setName("SuperBand 2000");
        summary.setDevice(device);

        return summary;
    }

    @Test
    public void testGPXExport() throws Exception {
        BipActivitySummary summary = createSummary();

        int baseLongi = BLETypeConversions.toUint32((byte) 0xd6, (byte) 0xc4,(byte) 0x62,(byte) 0x02);
        int baseLati = BLETypeConversions.toUint32((byte) 0xff, (byte) 0xa9, (byte) 0x61, (byte) 0x9);
        int baseAlti = BLETypeConversions.toUint32((byte) 0x30, (byte) 0x0, (byte) 0x0, (byte) 0x0);

        summary.setBaseLongitude(baseLongi);
        summary.setBaseLatitude(baseLati);
        summary.setBaseAltitude(baseAlti);

        HuamiActivityDetailsParser parser = new HuamiActivityDetailsParser(summary);
        parser.setSkipCounterByte(true);
        try (InputStream in = getContents(DETAILS_1)) {
            ActivityTrack track = parser.parse(FileUtils.readAll(in, MAX_DETAILS));

            List<ActivityPoint> trackPoints = track.getTrackPoints();
            assertEquals(972, trackPoints.size());


            GPXExporter exporter = new GPXExporter();
            exporter.setIncludeHeartRate(false);
            exporter.setCreator(getClass().getName());
            File targetFile = File.createTempFile("gadgetbridge-track", ".gpx");
            System.out.println("Writing GPX file: " + targetFile);
            exporter.performExport(track, targetFile);

            assertTrue(targetFile.length() > 1024);
        }

    }

    private InputStream getContents(URL hexFile) throws IOException {
        return new HexToBinaryInputStream(hexFile.openStream());
    }


}
