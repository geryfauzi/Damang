/*  Copyright (C) 2016-2021 Andreas Shimokawa, Carsten Pfeiffer, Daniele
    Gobbetti, João Paulo Barraca, José Rebelo, Lesur Frederic

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package unikom.gery.damang.devices.hplus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelUuid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import androidx.annotation.NonNull;

import de.greenrobot.dao.query.QueryBuilder;
import unikom.gery.damang.GBApplication;
import unikom.gery.damang.GBException;
import unikom.gery.damang.R;
import unikom.gery.damang.activities.SettingsActivity;
import unikom.gery.damang.activities.devicesettings.DeviceSettingsPreferenceConst;
import unikom.gery.damang.devices.AbstractDeviceCoordinator;
import unikom.gery.damang.devices.InstallHandler;
import unikom.gery.damang.devices.SampleProvider;
import unikom.gery.damang.entities.DaoSession;
import unikom.gery.damang.entities.Device;
import unikom.gery.damang.entities.HPlusHealthActivitySampleDao;
import unikom.gery.damang.impl.GBDevice;
import unikom.gery.damang.impl.GBDeviceCandidate;
import unikom.gery.damang.model.ActivitySample;
import unikom.gery.damang.model.ActivityUser;
import unikom.gery.damang.model.DeviceType;
import unikom.gery.damang.util.GBPrefs;
import unikom.gery.damang.util.Prefs;

import static unikom.gery.damang.GBApplication.getContext;

public class HPlusCoordinator extends AbstractDeviceCoordinator {
    protected static final Logger LOG = LoggerFactory.getLogger(HPlusCoordinator.class);
    protected static Prefs prefs = GBApplication.getPrefs();

    @NonNull
    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Collection<? extends ScanFilter> createBLEScanFilters() {
        ParcelUuid hpService = new ParcelUuid(HPlusConstants.UUID_SERVICE_HP);
        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(hpService).build();
        return Collections.singletonList(filter);
    }

    @NonNull
    @Override
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name != null && name.startsWith("HPLUS")) {
            return DeviceType.HPLUS;
        }

        return DeviceType.UNKNOWN;
    }

    @Override
    public int getBondingStyle() {
        return BONDING_STYLE_NONE;
    }

    @Override
    public boolean supportsCalendarEvents() {
        return false;
    }

    @Override
    public boolean supportsRealtimeData() {
        return true;
    }

    @Override
    public boolean supportsWeather() {
        return false;
    }

    @Override
    public boolean supportsFindDevice() {
        return true;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.HPLUS;
    }

    @Override
    public Class<? extends Activity> getPairingActivity() {
        return null;
    }

    @Override
    public InstallHandler findInstallHandler(Uri uri, Context context) {
        return null;
    }

    @Override
    public boolean supportsActivityDataFetching() {
        return true;
    }

    @Override
    public boolean supportsActivityTracking() {
        return true;
    }

    @Override
    public SampleProvider<? extends ActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return new HPlusHealthSampleProvider(device, session);
    }

    @Override
    public boolean supportsScreenshots() {
        return false;
    }

    @Override
    public int getAlarmSlotCount() {
        return 3; // FIXME - check the real value
    }

    @Override
    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    @Override
    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    @Override
    public String getManufacturer() {
        return "Zeblaze";
    }

    @Override
    public boolean supportsAppsManagement() {
        return false;
    }

    @Override
    public Class<? extends Activity> getAppsManagementActivity() {
        return null;
    }

    @Override
    protected void deleteDevice(@NonNull GBDevice gbDevice, @NonNull Device device, @NonNull DaoSession session) throws GBException {
        Long deviceId = device.getId();
        QueryBuilder<?> qb = session.getHPlusHealthActivitySampleDao().queryBuilder();
        qb.where(HPlusHealthActivitySampleDao.Properties.DeviceId.eq(deviceId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public static byte getLanguage(String address) {
        String language = prefs.getString("language", "default");
        Locale locale;

        if (language.equals("default")) {
            locale = Locale.getDefault();
        } else {
            locale = new Locale(language);
        }

        if (locale.getLanguage().equals(new Locale("cn").getLanguage())) {
            return HPlusConstants.ARG_LANGUAGE_CN;
        } else {
            return HPlusConstants.ARG_LANGUAGE_EN;
        }
    }

    public static byte getTimeMode(String deviceAddress) {
        GBPrefs gbPrefs = new GBPrefs(new Prefs(GBApplication.getDeviceSpecificSharedPrefs(deviceAddress)));

        String tmode = gbPrefs.getTimeFormat();

        if ("24h".equals(tmode)) {
            return HPlusConstants.ARG_TIMEMODE_24H;
        } else {
            return HPlusConstants.ARG_TIMEMODE_12H;
        }
    }

    public static byte getUnit(String address) {
        String units = prefs.getString(SettingsActivity.PREF_MEASUREMENT_SYSTEM, getContext().getString(R.string.p_unit_metric));

        if (units.equals(getContext().getString(R.string.p_unit_metric))) {
            return HPlusConstants.ARG_UNIT_METRIC;
        } else {
            return HPlusConstants.ARG_UNIT_IMPERIAL;
        }
    }

    public static byte getUserWeight() {
        ActivityUser activityUser = new ActivityUser();

        return (byte) (activityUser.getWeightKg() & 0xFF);
    }

    public static byte getUserHeight() {
        ActivityUser activityUser = new ActivityUser();

        return (byte) (activityUser.getHeightCm() & 0xFF);
    }

    public static byte getUserAge() {
        ActivityUser activityUser = new ActivityUser();

        return (byte) (activityUser.getAge() & 0xFF);
    }

    public static byte getUserGender() {
        ActivityUser activityUser = new ActivityUser();

        if (activityUser.getGender() == ActivityUser.GENDER_MALE)
            return HPlusConstants.ARG_GENDER_MALE;

        return HPlusConstants.ARG_GENDER_FEMALE;
    }

    public static int getGoal() {
        ActivityUser activityUser = new ActivityUser();

        return activityUser.getStepsGoal();
    }

    public static byte getScreenTime(String address) {
        return (byte) (prefs.getInt(HPlusConstants.PREF_HPLUS_SCREENTIME, 5) & 0xFF);
    }

    public static byte getAllDayHR(String address) {
        boolean value = (prefs.getBoolean(HPlusConstants.PREF_HPLUS_ALLDAYHR, true));

        if (value) {
            return HPlusConstants.ARG_HEARTRATE_ALLDAY_ON;
        } else {
            return HPlusConstants.ARG_HEARTRATE_ALLDAY_OFF;
        }
    }

    public static byte getSocial(String address) {
        //TODO: Figure what this is. Returning the default value

        return (byte) 255;
    }

    //FIXME: unused
    public static byte getUserWrist(String deviceAddress) {
        SharedPreferences sharedPreferences = GBApplication.getDeviceSpecificSharedPrefs(deviceAddress);
        String value = sharedPreferences.getString(DeviceSettingsPreferenceConst.PREF_WEARLOCATION, "left");

        if ("left".equals(value)) {
            return HPlusConstants.ARG_WRIST_LEFT;
        } else {
            return HPlusConstants.ARG_WRIST_RIGHT;
        }
    }

    public static int getSITStartTime(String address) {
        return prefs.getInt(HPlusConstants.PREF_HPLUS_SIT_START_TIME, 0);
    }

    public static int getSITEndTime(String address) {
        return prefs.getInt(HPlusConstants.PREF_HPLUS_SIT_END_TIME, 0);
    }

    public static void setDisplayIncomingMessageIcon(String address, boolean state) {
        SharedPreferences.Editor editor = prefs.getPreferences().edit();
        editor.putBoolean(HPlusConstants.PREF_HPLUS_DISPLAY_NOTIFICATION_ICON + "_" + address, state);
        editor.apply();
    }

    public static boolean getDisplayIncomingMessageIcon(String address) {
        return (prefs.getBoolean(HPlusConstants.PREF_HPLUS_DISPLAY_NOTIFICATION_ICON + "_" + address, false));
    }

    public static void setUnicodeSupport(String address, boolean state) {
        SharedPreferences.Editor editor = prefs.getPreferences().edit();
        editor.putBoolean(HPlusConstants.PREF_HPLUS_UNICODE + "_" + address, state);
        editor.apply();
    }

    public static boolean getUnicodeSupport(String address) {
        return (prefs.getBoolean(HPlusConstants.PREF_HPLUS_UNICODE + "_" + address, false));
    }

    public static void setNotificationLinesNumber(String address, int lineNumber) {
        SharedPreferences.Editor editor = prefs.getPreferences().edit();
        editor.putInt(HPlusConstants.PREF_HPLUS_NOTIFICATION_LINES + "_" + address, lineNumber);
        editor.apply();
    }

    public static int getNotificationLinesNumber(String address) {
        return (prefs.getInt(HPlusConstants.PREF_HPLUS_NOTIFICATION_LINES + "_" + address, 5));
    }

    @Override
    public int[] getSupportedDeviceSpecificSettings(GBDevice device) {
        return new int[]{
                //R.xml.devicesettings_wearlocation, // disabled, since it is never used in code
                R.xml.devicesettings_timeformat
        };
    }

}
    