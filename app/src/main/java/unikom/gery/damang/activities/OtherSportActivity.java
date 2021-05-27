package unikom.gery.damang.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import unikom.gery.damang.R;
import unikom.gery.damang.service.NormalReceiver;
import unikom.gery.damang.service.SportReceiver;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.sqlite.table.Sport;
import unikom.gery.damang.util.SharedPreference;

public class OtherSportActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtDetakJantung, txtTNS, txtTNSStatus;
    private Chronometer chronometer;
    private ImageView imgSportPaused, btnBack;
    private LottieAnimationView imgSportStarted;
    private Button btnMulai, btnSelesai;
    private CardView cvMulai, cvSelesai;
    private long pauseOffset;
    private SharedPreference sharedPreference;
    private HeartRateHelper heartRateHelper;
    private int age, tns;

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
        setContentView(R.layout.activity_other_sport);
        //View Binding
        txtDetakJantung = findViewById(R.id.txtDetakJantung);
        txtTNS = findViewById(R.id.txtTargetTNS);
        txtTNSStatus = findViewById(R.id.txtStatusTNS);
        chronometer = findViewById(R.id.txtTimer);
        imgSportPaused = findViewById(R.id.imgSportPaused);
        imgSportStarted = findViewById(R.id.imgSportStart);
        btnMulai = findViewById(R.id.btnMulai);
        btnSelesai = findViewById(R.id.btnSelesai);
        cvMulai = findViewById(R.id.cardView16);
        cvSelesai = findViewById(R.id.cardView17);
        btnBack = findViewById(R.id.btnBack);
        sharedPreference = new SharedPreference(getApplicationContext());
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        pauseOffset = 0;

        btnBack.setOnClickListener(this);
        btnMulai.setOnClickListener(this);
        btnSelesai.setOnClickListener(this);
        cvSelesai.setVisibility(View.INVISIBLE);
        imgSportStarted.setVisibility(View.INVISIBLE);

        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        try {
            age = getCurrentAge(getTodayDate(), sharedPreference.getUser().getDateofBirth());
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        tns = calculateTNS(age);
        txtTNS.setText(Integer.toString(tns));
    }

    private void saveToDB(String id) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = format.format(new Date(System.currentTimeMillis()));
        Sport sport = new Sport();
        sport.setId(id);
        sport.setStart_time(date);
        sport.setTns_target(tns);
        sport.setType("Lainnya");
        heartRateHelper.insertSportData(sport);
    }

    private int calculateTNS(int age) {
        Double tns = (220 - age) * 0.85;
        int parsedTNS = (int) Math.round(tns);
        return parsedTNS;
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

    private void generateSportID() throws ParseException {
        if (sharedPreference.getSportId().equals("null")) {
            String charId = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            StringBuilder newID = new StringBuilder();
            Random random = new Random();
            while (newID.length() < 10) {
                int index = (int) (random.nextFloat() * charId.length());
                newID.append(charId.charAt(index));
            }
            sharedPreference.setSportId(newID.toString());
            saveToDB(newID.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startSportMode() throws ParseException {
        if (sharedPreference.getMode().equals("Normal")) {
            //Start Sport Mode
            ComponentName sportMode = new ComponentName(this, SportReceiver.class);
            PackageManager packageManager = this.getPackageManager();
            packageManager.setComponentEnabledSetting(sportMode,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            SportReceiver sportReceiver = new SportReceiver();
            sportReceiver.setReceiver(this);
            //Pause Normal Mode
            ComponentName normalMode = new ComponentName(this, NormalReceiver.class);
            packageManager.setComponentEnabledSetting(normalMode,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            sharedPreference.setMode("Sport");
        }
        generateSportID();
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chronometer.start();
    }

    private void pause() {
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void stop() {
        //Start Normal Mode
        ComponentName normalMode = new ComponentName(this, NormalReceiver.class);
        PackageManager packageManager = this.getPackageManager();
        packageManager.setComponentEnabledSetting(normalMode,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        NormalReceiver normalReceiver = new NormalReceiver();
        normalReceiver.setReceiver(this);
        //Pause Sport Mode
        ComponentName sportMode = new ComponentName(this, SportReceiver.class);
        packageManager.setComponentEnabledSetting(sportMode,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        sharedPreference.setMode("Normal");
        sharedPreference.setSportId("null");
        chronometer.stop();
        long menit = SystemClock.elapsedRealtime() - chronometer.getBase();
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        Toast.makeText(getApplicationContext(), Long.toString(menit), Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        if (view == btnMulai) {
            if (btnMulai.getText().equals("Mulai")) {
                try {
                    startSportMode();
                    btnMulai.setText("Jeda");
                    cvSelesai.setVisibility(View.VISIBLE);
                    imgSportStarted.setVisibility(View.VISIBLE);
                    imgSportPaused.setVisibility(View.INVISIBLE);
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                pause();
                btnMulai.setText("Mulai");
                cvSelesai.setVisibility(View.VISIBLE);
                imgSportStarted.setVisibility(View.INVISIBLE);
                imgSportPaused.setVisibility(View.VISIBLE);
            }
        } else if (view == btnSelesai) {
            stop();
            btnMulai.setText("Mulai");
            cvSelesai.setVisibility(View.INVISIBLE);
            imgSportStarted.setVisibility(View.INVISIBLE);
            imgSportPaused.setVisibility(View.VISIBLE);
        } else if (view == btnBack) {
            stop();
            startActivity(new Intent(getApplicationContext(), SportActivity.class));
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBackPressed() {
        stop();
        startActivity(new Intent(getApplicationContext(), SportActivity.class));
        finish();
    }
}