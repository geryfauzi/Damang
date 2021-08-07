package unikom.gery.damang.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import unikom.gery.damang.GBApplication;
import unikom.gery.damang.R;
import unikom.gery.damang.adapter.SleepAdapter;
import unikom.gery.damang.devices.DeviceManager;
import unikom.gery.damang.impl.GBDevice;
import unikom.gery.damang.service.NormalReceiver;
import unikom.gery.damang.service.SleepReceiver;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.sqlite.table.Sleep;
import unikom.gery.damang.util.SharedPreference;

public class SleepActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Sleep> list = new ArrayList<>();
    private DeviceManager deviceManager;
    private List<GBDevice> deviceList;
    private GBDevice device;
    private HeartRateHelper heartRateHelper;
    private RecyclerView rvSleep;
    private Button btnMulai;
    private TextView btnViewAll;
    private ConstraintLayout cvNoData;
    private SharedPreference sharedPreference;
    private String id = "";
    private SleepAdapter sleepAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Change statusbar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sleep);

        btnMulai = findViewById(R.id.btnMulai);
        rvSleep = findViewById(R.id.rvSleepData);
        btnViewAll = findViewById(R.id.btnViewAll);
        cvNoData = findViewById(R.id.cvNoData);
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        list = heartRateHelper.getSleepData();
        sharedPreference = new SharedPreference(getApplicationContext());
        deviceManager = ((GBApplication) getApplication()).getDeviceManager();
        deviceList = deviceManager.getDevices();
        sleepAdapter = new SleepAdapter(list, getApplicationContext());

        btnViewAll.setOnClickListener(this);
        btnMulai.setOnClickListener(this);

        viewSleepData();
    }

    private boolean checkDevice() {
        boolean status = false;
        if (deviceList.size() <= 0)
            return false;
        else {
            for (int i = 0; i < deviceList.size(); i++) {
                device = deviceList.get(i);
                if (device.isConnected()) {
                    status = true;
                }
            }
        }
        return status;
    }

    public void viewSleepData() {
        if (list.size() > 0) {
            rvSleep.setVisibility(View.VISIBLE);
            btnViewAll.setVisibility(View.VISIBLE);
            cvNoData.setVisibility(View.GONE);
            rvSleep.setHasFixedSize(true);
            rvSleep.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rvSleep.setAdapter(sleepAdapter);
        } else {
            rvSleep.setVisibility(View.GONE);
            btnViewAll.setVisibility(View.INVISIBLE);
            cvNoData.setVisibility(View.VISIBLE);
        }
        if (sharedPreference.getMode().equals("Normal")) {
            btnMulai.setText("Aktifkan Mode Tidur");
        } else if (sharedPreference.getMode().equals("Sleep")) {
            btnMulai.setText("Hentikan Mode Tidur");
        }
    }

    private void saveToDB(String id) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = format.format(new Date(System.currentTimeMillis()));
        sharedPreference.setStartTime(date);
        Sleep sleep = new Sleep();
        sleep.setId(id);
        sleep.setStart_time(date);
        heartRateHelper.insertSleepData(sleep);
    }

    private String getTodayDate() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(System.currentTimeMillis()));
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startSleepMode() {
        ComponentName sleepMode = new ComponentName(this, SleepReceiver.class);
        PackageManager packageManager = this.getPackageManager();
        packageManager.setComponentEnabledSetting(sleepMode,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        SleepReceiver sleepReceiver = new SleepReceiver();
        sleepReceiver.setReceiver(this);
    }

    private void pauseNormalMode() {
        ComponentName normalMode = new ComponentName(this, NormalReceiver.class);
        PackageManager packageManager = this.getPackageManager();
        packageManager.setComponentEnabledSetting(normalMode,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startNormalMode() {
        ComponentName normalMode = new ComponentName(this, NormalReceiver.class);
        PackageManager packageManager = this.getPackageManager();
        packageManager.setComponentEnabledSetting(normalMode,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        NormalReceiver normalReceiver = new NormalReceiver();
        normalReceiver.setReceiver(this);
    }

    private void pauseSleepMode() {
        ComponentName sleepMode = new ComponentName(this, SleepReceiver.class);
        PackageManager packageManager = this.getPackageManager();
        packageManager.setComponentEnabledSetting(sleepMode,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void generateSleepID() {
        if (sharedPreference.getSportId().equals("null")) {
            String charId = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            StringBuilder newID = new StringBuilder();
            Random random = new Random();
            while (newID.length() < 10) {
                int index = (int) (random.nextFloat() * charId.length());
                newID.append(charId.charAt(index));
            }
            id = newID.toString();
            sharedPreference.setSleepId(newID.toString());
            saveToDB(newID.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void start() {
        if (sharedPreference.getMode().equals("Normal")) {
            startSleepMode();
            pauseNormalMode();
            sharedPreference.setMode("Sleep");
            generateSleepID();
        }
    }

    private void updateDB() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = format.format(new Date(System.currentTimeMillis()));
        Sleep sleep = new Sleep();
        sleep.setId(sharedPreference.getSleepId());
        sleep.setEnd_time(date);
        sleep.setDuration((int) calculateTotalMinute(sharedPreference.getStartTime(), date));
        sleep.setAverage_heart_rate(heartRateHelper.getAverageSleepHearRate(sharedPreference.getSleepId(), sharedPreference.getUser().getEmail()));
        sleep.setStatus(String.valueOf(calculateScore(sleep.getDuration())));
        heartRateHelper.updateSleepData(sleep);
    }

    private int calculateScore(int duration) throws ParseException {
        int hour = duration / 60;
        if (hour < 1)
            hour = 1;
        int score = 100;
        int age = getCurrentAge(getTodayDate(), sharedPreference.getUser().getDateofBirth());
        int averageBPM = heartRateHelper.getAverageSleepHearRate(sharedPreference.getSleepId(), sharedPreference.getUser().getEmail());
        //
        if (sharedPreference.getUser().getGender().equals("Laki - Laki")) {
            if (averageBPM > 80)
                score -= 20;
            else if (averageBPM < 50)
                score -= 15;
        } else {
            if (averageBPM > 82)
                score -= 20;
            else if (averageBPM < 53)
                score -= 15;
        }
        //
        if (age >= 3 && age <= 5) {
            if (hour > 13)
                score -= 15;
            else if (hour >= 7 && hour <= 9)
                score -= 20;
            else if (hour < 7)
                score -= 25;
            //
        } else if (age >= 14 && age <= 17) {
            if (hour > 10)
                score -= 15;
            else if (hour >= 5 && hour <= 7)
                score -= 20;
            else if (hour < 5)
                score -= 25;
            //
        } else if (age >= 18 && age <= 25) {
            if (hour > 9)
                score -= 15;
            else if (hour >= 4 && hour <= 6)
                score -= 20;
            else if (hour < 4)
                score -= 25;
            //
        } else if (age >= 26 && age <= 64) {
            if (hour > 9)
                score -= 15;
            else if (hour >= 4 && hour <= 6)
                score -= 20;
            else if (hour < 4)
                score -= 25;
            //
        } else if (age >= 65) {
            if (hour > 8)
                score -= 15;
            else if (hour >= 4 && hour <= 6)
                score -= 20;
            else if (hour < 4)
                score -= 25;
            //
        }
        return score;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showAlertDialog() throws ParseException {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghentikan mode Tidur ?");
        builder.setTitle("Perhatian!");
        builder.setCancelable(false);
        builder.setPositiveButton("Iya", (dialog, which) -> {
            if (heartRateHelper.checkHeartRateSleepMode(sharedPreference.getSleepId(), sharedPreference.getUser().getEmail())) {
                try {
                    stop();
                    updateDB();
                    sharedPreference.setMode("Normal");
                    sharedPreference.setSleepId("null");
                    startActivity(new Intent(getApplicationContext(), SleepActivity.class));
                    finish();
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }

            } else {
                try {
                    stop();
                    heartRateHelper.deleteSleepData(sharedPreference.getSleepId());
                    sharedPreference.setMode("Normal");
                    sharedPreference.setSleepId("null");
                    startActivity(new Intent(getApplicationContext(), SleepActivity.class));
                    finish();
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void stop() throws ParseException {
        startNormalMode();
        pauseSleepMode();
    }

    private long calculateTotalMinute(String startTime, String endTime) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startDate = simpleDateFormat.parse(startTime);
        Date endDate = simpleDateFormat.parse(endTime);
        long difference = endDate.getTime() - startDate.getTime();
        return difference / 60000;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        if (view == btnMulai) {
            if (sharedPreference.getMode().equals("Normal")) {
                if (checkDevice()) {
                    start();
                    btnMulai.setText("Hentikan Mode Tidur");
                } else
                    Toast.makeText(getApplicationContext(), "Harap hubungkan dahulu sistem dengan perangkat wearable device", Toast.LENGTH_SHORT).show();
            } else if (sharedPreference.getMode().equals("Sleep")) {
                try {
                    showAlertDialog();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}