package unikom.gery.damang.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
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
import android.os.Handler;
import android.os.Looper;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import unikom.gery.damang.GBApplication;
import unikom.gery.damang.R;
import unikom.gery.damang.model.Cardio;
import unikom.gery.damang.model.DetailHeartRate;
import unikom.gery.damang.model.DeviceService;
import unikom.gery.damang.model.NotificationSpec;
import unikom.gery.damang.model.NotificationType;
import unikom.gery.damang.service.NormalReceiver;
import unikom.gery.damang.service.SportReceiver;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.sqlite.table.Sport;
import unikom.gery.damang.util.SharedPreference;

public class CardioSportActivity extends AppCompatActivity implements View.OnClickListener {

    private Chronometer chronometer;
    private TextView txtDetakJantung, txtTargetTNS, txtTNSStatus, txtCardioName, txtCardioCount;
    private ImageView imgAnimation, btnBack;
    private Button btnMulai, btnSelesai;
    private ArrayList<Cardio> arrayList = new ArrayList<>();
    private CardView cvMulai, cvSelesai;
    private long pauseOffset;
    private SharedPreference sharedPreference;
    private HeartRateHelper heartRateHelper;
    private int age, tns, iteration;
    private String id = "";
    private long duration;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (Objects.requireNonNull(action)) {
                case DeviceService.ACTION_REALTIME_SAMPLES:
                    updateView();
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_cardio_sport);
        //View Binding
        txtDetakJantung = findViewById(R.id.txtDetakJantung);
        txtTargetTNS = findViewById(R.id.txtTargetTNS);
        txtTNSStatus = findViewById(R.id.txtStatusTNS);
        imgAnimation = findViewById(R.id.imgAnimation);
        btnBack = findViewById(R.id.btnBack);
        imgAnimation = findViewById(R.id.imgAnimation);
        btnMulai = findViewById(R.id.btnMulai);
        btnSelesai = findViewById(R.id.btnSelesai);
        cvMulai = findViewById(R.id.cardView16);
        cvSelesai = findViewById(R.id.cardView17);
        txtCardioName = findViewById(R.id.txtCardioName);
        txtCardioCount = findViewById(R.id.txtCardioCount);
        chronometer = findViewById(R.id.txtTimer);
        sharedPreference = new SharedPreference(getApplicationContext());
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());

        btnBack.setOnClickListener(this);
        btnMulai.setOnClickListener(this);
        btnSelesai.setOnClickListener(this);

        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(DeviceService.ACTION_REALTIME_SAMPLES);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filterLocal);

        arrayList = (ArrayList<Cardio>) getIntent().getSerializableExtra("list");

        iteration = 0;
        chronometer.setBase(SystemClock.elapsedRealtime());
        try {
            age = getCurrentAge(getTodayDate(), sharedPreference.getUser().getDateofBirth());
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        chronometer.setVisibility(View.INVISIBLE);
        tns = calculateTNS(age);
        txtTargetTNS.setText(Integer.toString(tns));
        start();
        firstTimer();
    }

    private void sendNotification(String value) {
        NotificationSpec notificationSpec = new NotificationSpec();
        String testString = value;
        notificationSpec.phoneNumber = testString;
        notificationSpec.body = testString;
        notificationSpec.sender = testString;
        notificationSpec.subject = testString;
        notificationSpec.type = NotificationType.values()[0];
        GBApplication.deviceService().onNotification(notificationSpec);
    }

    private void firstTimer() {
        Glide.with(getApplicationContext()).load(R.raw.timer_20).into(imgAnimation);
        txtCardioCount.setVisibility(View.INVISIBLE);
        txtCardioName.setText("Bersiaplah");
        cvSelesai.setVisibility(View.INVISIBLE);
        cvMulai.setVisibility(View.INVISIBLE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed((Runnable) () -> {
            displayAnimation(iteration);
        }, 21000);
    }

    private void timer() {
        Glide.with(getApplicationContext()).load(R.raw.timer_10).into(imgAnimation);
        txtCardioCount.setVisibility(View.INVISIBLE);
        txtCardioName.setText("Beristirahatlah");
        cvSelesai.setVisibility(View.INVISIBLE);
        cvMulai.setVisibility(View.INVISIBLE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed((Runnable) () -> {
            displayAnimation(iteration);
        }, 11000);
    }

    private void displayAnimation(int iteration) {
        txtCardioCount.setVisibility(View.VISIBLE);
        cvSelesai.setVisibility(View.VISIBLE);
        cvMulai.setVisibility(View.VISIBLE);

        Glide.with(getApplicationContext()).load(arrayList.get(iteration).getAnimation()).into(imgAnimation);
        txtCardioName.setText(arrayList.get(iteration).getName());
        txtCardioCount.setText("x" + (arrayList.get(iteration).getCount()));
        this.iteration += 1;
    }

    private void saveToDB(String id) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = format.format(new Date(System.currentTimeMillis()));
        Sport sport = new Sport();
        sport.setId(id);
        sport.setStart_time(date);
        sport.setTns_target(tns);
        sport.setType("Cardio Lantai");
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startSportMode() {
        ComponentName sportMode = new ComponentName(this, SportReceiver.class);
        PackageManager packageManager = this.getPackageManager();
        packageManager.setComponentEnabledSetting(sportMode,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        SportReceiver sportReceiver = new SportReceiver();
        sportReceiver.setReceiver(this);
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

    private void pauseSportMode() {
        ComponentName sportMode = new ComponentName(this, SportReceiver.class);
        PackageManager packageManager = this.getPackageManager();
        packageManager.setComponentEnabledSetting(sportMode,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void generateSportID() {
        if (sharedPreference.getSportId().equals("null")) {
            String charId = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            StringBuilder newID = new StringBuilder();
            Random random = new Random();
            while (newID.length() < 10) {
                int index = (int) (random.nextFloat() * charId.length());
                newID.append(charId.charAt(index));
            }
            id = newID.toString();
            sharedPreference.setSportId(newID.toString());
            saveToDB(newID.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void start() {
        if (sharedPreference.getMode().equals("Normal")) {
            startSportMode();
            pauseNormalMode();
            sharedPreference.setMode("Sport");
        }
        generateSportID();
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chronometer.start();
    }

    private void updateDB() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = format.format(new Date(System.currentTimeMillis()));
        Sport sport = new Sport();
        sport.setId(id);
        sport.setEnd_time(date);
        sport.setDuration((int) duration);
        sport.setTns_status(txtTNSStatus.getText().toString());
        sport.setAverage_heart_rate(heartRateHelper.getAverageSportHearRate(id, sharedPreference.getUser().getEmail()));
        sport.setCalories_burned(calculateBurnedCalories(heartRateHelper.getAverageSportHearRate(id, sharedPreference.getUser().getEmail())));
        heartRateHelper.updateSportData(sport);
    }

    private int calculateBurnedCalories(int heartRate) {
        int burnedCalories;
        if (sharedPreference.getUser().getGender().equals("Laki - Laki"))
            burnedCalories = (int) Math.round(((-55.0969 + (0.6309 * heartRate) + (0.1988 * sharedPreference.getUser().getWeight()) + (0.2017 * age)) / 4.184) * duration);
        else
            burnedCalories = (int) Math.round(((-20.4022 + (0.4472 * heartRate) - (0.1263 * sharedPreference.getUser().getWeight()) + (0.074 * age)) / 4.184) * duration);
        return burnedCalories;
    }

    private void updateView() {
        int heartRate = heartRateHelper.getLatesHeartRateSportMode(id, sharedPreference.getUser().getEmail());
        txtDetakJantung.setText(String.valueOf(heartRate));
        if (heartRate >= tns && txtTNSStatus.getText().toString().equals("Belum")) {
            txtTNSStatus.setText("Iya");
            createNotificationNormalMode();
            sendNotification("Selamat, anda telah mencapai TNS anda ! Pertahankan fase ini sampai cardio selesai untuk mendapat hasil maksimal!");
            Toast.makeText(getApplicationContext(), "Selamat ! Anda telah mencapai TNS Anda !", Toast.LENGTH_LONG).show();
        }
        checkTNS();
    }

    private void checkTNS() {
        if (sharedPreference.getMode().equals("Sport")) {
            String message = "";
            ArrayList<DetailHeartRate> list = heartRateHelper.getDetailSportData(id);
            boolean isDecreased = isDecreased(list);
            duration = SystemClock.elapsedRealtime() - chronometer.getBase();
            duration = Math.round(duration / 60000);
            if (duration >= 20 && duration < 30 && txtTNSStatus.getText().equals("Belum")) {
                if (isDecreased) {
                    message = message + "Detak jantung anda mengalami penurunan.";
                    message = message + "Jika anda sudah merasa lelah, istirahat dahulu dari olahraga anda" +
                            " atau hentikan olahraga anda.";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    sendNotification(message);
                }
            } else if (duration >= 30 && txtTNSStatus.getText().equals("Belum"))
                if (isDecreased) {
                    message = message + "Detak jantung anda mengalami penuruan.";
                    message = message + "Segera hentikan olahraga anda sekarang juga!";
                    sendNotification(message);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
        }
    }

    private boolean isDecreased(ArrayList<DetailHeartRate> list) {
        boolean status = false;
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    if (list.get(i).getHeartRate() < list.get(i - 1).getHeartRate()) {
                        status = true;
                        break;
                    }
                }
            }
        }
        return status;
    }

    private void createNotificationNormalMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("sport_notif", "sport_notifikasi", importance);
            @SuppressLint("WrongConstant") Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext(), "sport_notif").setSmallIcon(R.drawable.ic_tns)
                    .setContentTitle("Anda Mencapai TNS !")
                    .setContentText("Selamat, anda telah mencapai TNS anda ! Pertahankan fase ini selama minimal 10 menit untuk mendapat hasil maksimal!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new Notification.BigTextStyle().bigText("Selamat, anda telah mencapai TNS anda ! Pertahankan fase ini selama minimal 10 menit untuk mendapat hasil maksimal!"));
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(0, notificationBuilder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "sport_notif")
                    .setSmallIcon(R.drawable.ic_tns)
                    .setContentTitle("Anda Mencapai TNS !")
                    .setContentText("Selamat, anda telah mencapai TNS anda ! Pertahankan fase ini selama minimal 10 menit untuk mendapat hasil maksimal!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(0, builder.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void stop() {
        startNormalMode();
        pauseSportMode();
        sharedPreference.setMode("Normal");
        sharedPreference.setSportId("null");
        chronometer.stop();
        duration = SystemClock.elapsedRealtime() - chronometer.getBase();
        duration = Math.round(duration / 60000);
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghentikan olahraga ?");
        builder.setTitle("Perhatian!");
        builder.setCancelable(false);
        builder.setPositiveButton("Iya", (dialog, which) -> {
            if (heartRateHelper.checkHeartRateSportMode(id, sharedPreference.getUser().getEmail())) {
                stop();
                updateDB();
                startActivity(new Intent(getApplicationContext(), SportActivity.class));
                finish();
            } else {
                stop();
                heartRateHelper.deleteSportData(id);
                startActivity(new Intent(getApplicationContext(), SportActivity.class));
                finish();
            }

        });
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        if (view == btnMulai) {
            if (iteration == arrayList.size()) {
                stop();
                updateDB();
                Intent intent = new Intent(getApplicationContext(), OtherSportDetailActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
            } else
                timer();
        } else if (view == btnSelesai)
            showAlertDialog();
        else if (view == btnBack)
            showAlertDialog();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBackPressed() {
        showAlertDialog();
    }
}