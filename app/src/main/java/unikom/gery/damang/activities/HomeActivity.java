/*  Copyright (C) 2016-2020 Andreas Shimokawa, Carsten Pfeiffer, Daniele
    Gobbetti, Johannes Tysiak, Taavi Eomäe, vanous

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
package unikom.gery.damang.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import cyanogenmod.providers.CMSettings;
import unikom.gery.damang.GBApplication;
import unikom.gery.damang.R;
import unikom.gery.damang.adapter.DeviceAdapter;
import unikom.gery.damang.devices.DeviceManager;
import unikom.gery.damang.devices.miband.MiBandPreferencesActivity;
import unikom.gery.damang.impl.GBDevice;
import unikom.gery.damang.model.DetailHeartRate;
import unikom.gery.damang.model.DeviceService;
import unikom.gery.damang.service.NormalReceiver;
import unikom.gery.damang.service.SportReceiver;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.util.AndroidUtils;
import unikom.gery.damang.util.GB;
import unikom.gery.damang.util.Prefs;
import unikom.gery.damang.util.SharedPreference;

//TODO: extend AbstractGBActivity, but it requires actionbar that is not available
public class HomeActivity extends AppCompatActivity
        implements GBActivity {

    public static final int MENU_REFRESH_CODE = 1;
    private static PhoneStateListener fakeStateListener;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ArrayList<DetailHeartRate> arrayList;
    private CardView cvNoDevice, cvHeartRate, cvRumahSakit, cvArtikel, cvPengaturanAlat, cvOlahraga;
    private ImageView btnAddDevice;
    private TextView txtHeartRate, txtCurrentCondition, txtUser, txtJumlahLangkah, txtKaloriTerbakar;
    private ImageView imgProfile, btnSettings;
    private SharedPreference sharedPreference;
    private DeviceManager deviceManager;
    private DeviceAdapter mGBDeviceAdapter;
    private RecyclerView deviceListView;
    private boolean isLanguageInvalid = false;
    private HeartRateHelper heartRateHelper;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (Objects.requireNonNull(action)) {
                case GBApplication.ACTION_LANGUAGE_CHANGE:
                    setLanguage(GBApplication.getLanguage(), true);
                    break;
                case GBApplication.ACTION_QUIT:
                    finish();
                    break;
                case DeviceManager.ACTION_DEVICES_CHANGED:
                    refreshPairedDevices();
                    break;
                case DeviceService.ACTION_REALTIME_SAMPLES:
                    try {
                        checkIfNoData();
                    } catch (ParseException error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    private boolean pesterWithPermissions = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AbstractGBActivity.init(this, AbstractGBActivity.NO_ACTIONBAR);

        //Change statusbar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#E5EBFF"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        deviceManager = ((GBApplication) getApplication()).getDeviceManager();
        imgProfile = findViewById(R.id.imgProfileHome);
        deviceListView = findViewById(R.id.rvDeviceHome);
        cvNoDevice = findViewById(R.id.cvNoDevice);
        cvHeartRate = findViewById(R.id.cardView6);
        cvArtikel = findViewById(R.id.cvArtikelKesehatan);
        cvRumahSakit = findViewById(R.id.cvRumahSakit);
        cvPengaturanAlat = findViewById(R.id.cvPengaturanAlat);
        cvOlahraga = findViewById(R.id.cvOlahraga);
        btnAddDevice = findViewById(R.id.btnAddDevice);
        btnSettings = findViewById(R.id.imageView6);
        txtHeartRate = findViewById(R.id.txtHeartRate);
        txtCurrentCondition = findViewById(R.id.txtStatusKesehatan);
        txtUser = findViewById(R.id.txtUser);
        txtJumlahLangkah = findViewById(R.id.txtJumlahLangkah);
        txtKaloriTerbakar = findViewById(R.id.txtKaloriTerbakar);
        deviceListView.setHasFixedSize(true);
        deviceListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        final List<GBDevice> deviceList = deviceManager.getDevices();
        mGBDeviceAdapter = new DeviceAdapter(this, deviceList);
        deviceListView.setAdapter(this.mGBDeviceAdapter);

        registerForContextMenu(deviceListView);
        if (deviceList.size() > 0)
            cvNoDevice.setVisibility(View.INVISIBLE);

        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(GBApplication.ACTION_LANGUAGE_CHANGE);
        filterLocal.addAction(GBApplication.ACTION_QUIT);
        filterLocal.addAction(DeviceManager.ACTION_DEVICES_CHANGED);
        filterLocal.addAction(DeviceService.ACTION_REALTIME_SAMPLES);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filterLocal);

        refreshPairedDevices();

        /*
         * Ask for permission to intercept notifications on first run.
         */
        Prefs prefs = GBApplication.getPrefs();
        pesterWithPermissions = prefs.getBoolean("permission_pestering", true);

        Set<String> set = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (pesterWithPermissions) {
            if (!set.contains(this.getPackageName())) { // If notification listener access hasn't been granted
                Intent enableIntent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(enableIntent);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        }

        GBApplication.deviceService().start();

        if (GB.isBluetoothEnabled() && deviceList.isEmpty() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startActivity(new Intent(this, DiscoveryActivity.class));
        } else {
            GBApplication.deviceService().requestDeviceInfo();
        }

        sharedPreference = new SharedPreference(this);
        Glide.with(getApplicationContext()).load(sharedPreference.getUser().getPhoto()).into(imgProfile);
        sharedPreference.setSportId("null");
        txtUser.setText(sharedPreference.getUser().getName());
        if (!sharedPreference.getMode().equals("Normal")) {
            //Start Normal Mode
            ComponentName normalMode = new ComponentName(this, NormalReceiver.class);
            PackageManager packageManager = this.getPackageManager();
            packageManager.setComponentEnabledSetting(normalMode,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            //Pause Sport Mode
            ComponentName sportMode = new ComponentName(this, SportReceiver.class);
            packageManager.setComponentEnabledSetting(sportMode,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            sharedPreference.setMode("Normal");
        }
        NormalReceiver normalReceiver = new NormalReceiver();
        normalReceiver.setReceiver(this);
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        try {
            checkIfNoData();
        } catch (ParseException error) {
            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
        }

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDiscoveryActivity();
            }
        });

        cvHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DailyConditionActivity.class));
            }
        });

        cvPengaturanAlat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MiBandPreferencesActivity.class);
                startActivity(intent);
            }
        });

        cvOlahraga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SportActivity.class));
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SystemSettingsActivity.class));
            }
        });
    }

    private String getTodayDate() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(System.currentTimeMillis()));
    }

    private void checkIfNoData() throws ParseException {
        if (heartRateHelper.getCurrentHeartRate(sharedPreference.getUser().getEmail(), getTodayDate()) > 0) {
            txtHeartRate.setVisibility(View.VISIBLE);
            updateCurrentCondition();
        } else {
            txtHeartRate.setVisibility(View.INVISIBLE);
            txtCurrentCondition.setText("Belum ada data detak jantung");
        }
    }

    private void updateCurrentCondition() throws ParseException {
        //Get Data From Database
        arrayList = heartRateHelper.getDetailDailyCondition(sharedPreference.getUser().getEmail(), getTodayDate());
        int currentHeartRate = arrayList.get(arrayList.size() - 1).getHeartRate();
        int averageHearRate = heartRateHelper.getCurrentHeartRate(sharedPreference.getUser().getEmail(), getTodayDate());
        int age = getCurrentAge(getTodayDate(), sharedPreference.getUser().getDateofBirth());
        boolean isDecreased = isSuddenlyDecrease(arrayList);
        boolean isIncreased = isSuddenlyIncrease(arrayList);
        //Processing Data
        String burnedCalories = String.format("%.2f", getBurnedCalories(sharedPreference.getSteps(), Math.round(sharedPreference.getUser().getWeight())));
        String averageHeartStatus = getCurrentHeartRateStatus(age, averageHearRate);
        String currentHeartStatus = getCurrentHeartRateStatus(age, currentHeartRate);
        String condition = getCurrentCondition(averageHeartStatus, currentHeartStatus, isIncreased, isDecreased);
        //Set to View
        txtHeartRate.setText(averageHearRate + " bpm");
        txtCurrentCondition.setText(condition);
        txtJumlahLangkah.setText(sharedPreference.getSteps() + " langkah");
        txtKaloriTerbakar.setText(burnedCalories + " kalori");
    }

    private float getBurnedCalories(int jumlahLangkah, int beratBadan) {
        float calories = 0;
        if (beratBadan >= 45 && beratBadan <= 54)
            calories = (float) ((28.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 55 && beratBadan <= 63)
            calories = (float) ((33.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 64 && beratBadan <= 72)
            calories = (float) ((38.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 73 && beratBadan <= 81)
            calories = (float) ((40.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 82 && beratBadan <= 90)
            calories = (float) ((45.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 91 && beratBadan <= 99)
            calories = (float) ((50.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 100 && beratBadan <= 113)
            calories = (float) ((55.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 114 && beratBadan <= 124)
            calories = (float) ((62.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 125 && beratBadan <= 135)
            calories = (float) ((68.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 136)
            calories = (float) ((75.0 / 1000) * jumlahLangkah);
        return calories;
    }

    private int getCurrentAge(String todayDate, String dayOfBirth) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = simpleDateFormat.parse(dayOfBirth);
        Date date2 = simpleDateFormat.parse(todayDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int month1 = calendar.get(Calendar.MONTH);
        int year1 = calendar.get(Calendar.YEAR);
        calendar.setTime(date2);
        int month2 = calendar.get(Calendar.MONTH);
        int year2 = calendar.get(Calendar.YEAR);
        int monthResult = ((year2 - year1) * 12) + (month2 - month1);
        return monthResult / 12;
    }

    private String getCurrentHeartRateStatus(int age, int heartRate) {
        String status = "";
        if (age < 2) {
            if (heartRate >= 80 && heartRate <= 160)
                status = "Normal";
            else if (heartRate > 160)
                status = "Tinggi";
            else
                status = "Rendah";
        } else if (age <= 10) {
            if (heartRate >= 70 && heartRate <= 110)
                status = "Normal";
            else if (heartRate > 110)
                status = "Tinggi";
            else
                status = "Rendah";
        } else {
            if (heartRate >= 54 && heartRate <= 120)
                status = "Normal";
            else if (heartRate > 120)
                status = "Tinggi";
            else
                status = "Rendah";
        }
        return status;
    }

    private boolean isSuddenlyIncrease(ArrayList<DetailHeartRate> list) {
        boolean status = false;
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                if ((list.get(i).getHeartRate() - list.get(i - 1).getHeartRate()) >= 40) {
                    status = true;
                    break;
                }
            }
        }
        return status;
    }

    private boolean isSuddenlyDecrease(ArrayList<DetailHeartRate> list) {
        boolean status = false;
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                list.size();
                if ((list.get(i - 1).getHeartRate() - list.get(i).getHeartRate()) >= 40) {
                    status = true;
                    break;
                }
            }
        }
        return status;
    }

    private String getCurrentCondition(String average, String current, boolean isIncreased, boolean isDecreased) {
        String status = "Kesehatan anda baik";
        //Fuzzy Logic Dengan Rata - Rata Normal dan terkini normal
        if (average.equals("Normal") && current.equals("Normal") && !isIncreased && !isDecreased)
            status = "Kesehatan anda baik";
        else if (average.equals("Normal") && current.equals("Normal") && isIncreased && !isDecreased)
            status = "Kesehatan anda baik";
        else if (average.equals("Normal") && current.equals("Normal") && !isIncreased && isDecreased)
            status = "Kesehatan anda baik";
        else if (average.equals("Normal") && current.equals("Normal") && isIncreased && isDecreased)
            status = "Kesehatan anda baik";
            //Fuzzy Logic Dengan Rata - Rata Normal dan terkini Tinggi
        else if (average.equals("Normal") && current.equals("Tinggi") && !isIncreased && !isDecreased)
            status = "Kesehatan anda baik";
        else if (average.equals("Normal") && current.equals("Tinggi") && isIncreased && !isDecreased)
            status = "Kesehatan anda baik*";
        else if (average.equals("Normal") && current.equals("Tinggi") && !isIncreased && isDecreased)
            status = "Kesehatan anda baik*";
        else if (average.equals("Normal") && current.equals("Tinggi") && isIncreased && isDecreased)
            status = "Kesehatan anda kurang baik";
            //Fuzzy Logic Dengan Rata - Rata Normal dan terkini Rendah
        else if (average.equals("Normal") && current.equals("Rendah") && !isIncreased && !isDecreased)
            status = "Kesehatan anda baik";
        else if (average.equals("Normal") && current.equals("Rendah") && isIncreased && !isDecreased)
            status = "Kesehatan anda baik";
        else if (average.equals("Normal") && current.equals("Rendah") && !isIncreased && isDecreased)
            status = "Kesehatan anda baik";
        else if (average.equals("Normal") && current.equals("Rendah") && isIncreased && isDecreased)
            status = "Kesehatan anda kurang baik";
            //Fuzzy Login dengan Rata - rata Tinggi dan terkini Normal
        else if (average.equals("Tinggi") && current.equals("Normal") && !isIncreased && !isDecreased)
            status = "Kesehatan anda baik*";
        else if (average.equals("Tinggi") && current.equals("Normal") && isIncreased && !isDecreased)
            status = "Kesehatan anda baik*";
        else if (average.equals("Tinggi") && current.equals("Normal") && !isIncreased && isDecreased)
            status = "Kesehatan anda baik*";
        else if (average.equals("Tinggi") && current.equals("Normal") && isIncreased && isDecreased)
            status = "Kesehatan anda kurang baik";
            //Fuzzy Login dengan Rata - rata Tinggi dan terkini Tinggi
        else if (average.equals("Tinggi") && current.equals("Tinggi") && !isIncreased && !isDecreased)
            status = "Kesehatan anda kurang baik";
        else if (average.equals("Tinggi") && current.equals("Tinggi") && isIncreased && !isDecreased)
            status = "Kesehatan anda tidak baik";
        else if (average.equals("Tinggi") && current.equals("Tinggi") && !isIncreased && isDecreased)
            status = "Kesehatan anda kurang baik";
        else if (average.equals("Tinggi") && current.equals("Tinggi") && isIncreased && isDecreased)
            status = "Kesehatan anda tidak baik";
            //Fuzzy Login dengan Rata - rata Tinggi dan terkini Rendah
        else if (average.equals("Tinggi") && current.equals("Rendah") && !isIncreased && !isDecreased)
            status = "Kesehatan anda tidak baik";
        else if (average.equals("Tinggi") && current.equals("Rendah") && isIncreased && !isDecreased)
            status = "Kesehatan anda tidak baik";
        else if (average.equals("Tinggi") && current.equals("Rendah") && !isIncreased && isDecreased)
            status = "Kesehatan anda tidak baik";
        else if (average.equals("Tinggi") && current.equals("Rendah") && isIncreased && isDecreased)
            status = "Kesehatan anda tidak baik";
            //Fuzzy Login dengan Rata - rata Rendah dan terkini Normal
        else if (average.equals("Rendah") && current.equals("Normal") && !isIncreased && !isDecreased)
            status = "Kesehatan anda baik";
        else if (average.equals("Rendah") && current.equals("Normal") && isIncreased && !isDecreased)
            status = "Kesehatan anda baik*";
        else if (average.equals("Rendah") && current.equals("Normal") && !isIncreased && isDecreased)
            status = "Kesehatan anda kurang baik";
        else if (average.equals("Rendah") && current.equals("Normal") && isIncreased && isDecreased)
            status = "Kesehatan anda tidak baik";
            //Fuzzy Login dengan Rata - rata Rendah dan terkini Tinggi
        else if (average.equals("Rendah") && current.equals("Tinggi") && !isIncreased && !isDecreased)
            status = "Kesehatan anda tidak baik";
        else if (average.equals("Rendah") && current.equals("Tinggi") && isIncreased && !isDecreased)
            status = "Kesehatan anda tidak baik";
        else if (average.equals("Rendah") && current.equals("Tinggi") && !isIncreased && isDecreased)
            status = "Kesehatan anda tidak baik";
        else if (average.equals("Rendah") && current.equals("Tinggi") && isIncreased && isDecreased)
            status = "Kesehatan anda tidak baik";
            //Fuzzy Login dengan Rata - rata Rendah dan terkini Rendah
        else if (average.equals("Rendah") && current.equals("Rendah") && !isIncreased && !isDecreased)
            status = "Kesehatan anda kurang baik";
        else if (average.equals("Rendah") && current.equals("Rendah") && isIncreased && !isDecreased)
            status = "Kesehatan anda kurang baik";
        else if (average.equals("Rendah") && current.equals("Rendah") && !isIncreased && isDecreased)
            status = "Kesehatan anda tidak baik";
        else if (average.equals("Rendah") && current.equals("Rendah") && isIncreased && isDecreased)
            status = "Kesehatan anda tidak baik";
        return status;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLanguageInvalid) {
            isLanguageInvalid = false;
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterForContextMenu(deviceListView);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void launchDiscoveryActivity() {
        startActivity(new Intent(this, DiscoveryActivity.class));
    }

    private void refreshPairedDevices() {
        mGBDeviceAdapter.notifyDataSetChanged();
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermissions() {
        List<String> wantedPermissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.BLUETOOTH);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.READ_CONTACTS);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.CALL_PHONE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.READ_CALL_LOG);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.READ_PHONE_STATE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.RECEIVE_SMS);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.READ_SMS);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.SEND_SMS);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.READ_CALENDAR);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
            wantedPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.MEDIA_CONTENT_CONTROL) == PackageManager.PERMISSION_DENIED)
                wantedPermissions.add(Manifest.permission.MEDIA_CONTENT_CONTROL);
        } catch (Exception ignored) {
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (pesterWithPermissions) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_DENIED) {
                    wantedPermissions.add(Manifest.permission.ANSWER_PHONE_CALLS);
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
                wantedPermissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
        }

        if (!wantedPermissions.isEmpty()) {
            Prefs prefs = GBApplication.getPrefs();
            // If this is not the first run, we can rely on
            // shouldShowRequestPermissionRationale(String permission)
            // and ignore permissions that shouldn't or can't be requested again
            if (prefs.getBoolean("permissions_asked", false)) {
                // Don't request permissions that we shouldn't show a prompt for
                // e.g. permissions that are "Never" granted by the user or never granted by the system
                Set<String> shouldNotAsk = new HashSet<>();
                for (String wantedPermission : wantedPermissions) {
                    if (!shouldShowRequestPermissionRationale(wantedPermission)) {
                        shouldNotAsk.add(wantedPermission);
                    }
                }
                wantedPermissions.removeAll(shouldNotAsk);
            } else {
                // Permissions have not been asked yet, but now will be
                prefs.getPreferences().edit().putBoolean("permissions_asked", true).apply();
            }

            if (!wantedPermissions.isEmpty()) {
                GB.toast(this, getString(R.string.permission_granting_mandatory), Toast.LENGTH_LONG, GB.ERROR);
                ActivityCompat.requestPermissions(this, wantedPermissions.toArray(new String[0]), 0);
                GB.toast(this, getString(R.string.permission_granting_mandatory), Toast.LENGTH_LONG, GB.ERROR);
            }
        }

        /* In order to be able to set ringer mode to silent in GB's PhoneCallReceiver
           the permission to access notifications is needed above Android M
           ACCESS_NOTIFICATION_POLICY is also needed in the manifest */
        if (pesterWithPermissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).isNotificationPolicyAccessGranted()) {
                    GB.toast(this, getString(R.string.permission_granting_mandatory), Toast.LENGTH_LONG, GB.ERROR);
                    startActivity(new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
                }
            }
        }

        // HACK: On Lineage we have to do this so that the permission dialog pops up
        if (fakeStateListener == null) {
            fakeStateListener = new PhoneStateListener();
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            telephonyManager.listen(fakeStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            telephonyManager.listen(fakeStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    public void setLanguage(Locale language, boolean invalidateLanguage) {
        if (invalidateLanguage) {
            isLanguageInvalid = true;
        }
        AndroidUtils.setLanguage(this, language);
    }

}
