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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;

import unikom.gery.damang.R;
import unikom.gery.damang.service.NormalReceiver;
import unikom.gery.damang.service.SportReceiver;
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
        pauseOffset = 0;

        btnMulai.setOnClickListener(this);
        btnSelesai.setOnClickListener(this);
        cvSelesai.setVisibility(View.INVISIBLE);
        imgSportStarted.setVisibility(View.INVISIBLE);

        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startSportMode() {
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
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        if (view == btnMulai) {
            if (btnMulai.getText().equals("Mulai")) {
                startSportMode();
                btnMulai.setText("Jeda");
                cvSelesai.setVisibility(View.VISIBLE);
                imgSportStarted.setVisibility(View.VISIBLE);
                imgSportPaused.setVisibility(View.INVISIBLE);
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
            startActivity(new Intent(getApplicationContext(), SportActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), SportActivity.class));
        finish();
    }
}