package unikom.gery.damang.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unikom.gery.damang.R;
import unikom.gery.damang.api.Api;
import unikom.gery.damang.api.BaseApi;
import unikom.gery.damang.model.DeviceService;
import unikom.gery.damang.response.CheckUser;
import unikom.gery.damang.service.NormalReceiver;
import unikom.gery.damang.service.SportReceiver;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.sqlite.table.Sport;
import unikom.gery.damang.util.SharedPreference;

public class JoggingSportActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private String provider;
    private Location lastLocation;
    private SharedPreference sharedPreference;
    private Chronometer chronometer;
    private ImageView btnBack;
    private Button btnMulai, btnSelesai;
    private CardView cvMulai, cvSelesai;
    private long pauseOffset = 0;
    private HeartRateHelper heartRateHelper;
    private int age, tns;
    private String id = "";
    private TextView txtDetakJantung, txtTNS, txtJarak;
    private long duration = 0;
    private String tnsStatus = "Belum";
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
    private double cLatitude, cLongitude, distance;
    private Api api;
    private Call<CheckUser> response;

    @SuppressLint("MissingPermission")
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_jogging_sport);
        //view Binding
        chronometer = findViewById(R.id.txtTimer);
        txtDetakJantung = findViewById(R.id.txtDetakJantung);
        txtTNS = findViewById(R.id.txtTargetTNS);
        txtJarak = findViewById(R.id.txtJarak);
        btnBack = findViewById(R.id.btnBack);
        btnMulai = findViewById(R.id.btnMulai);
        btnSelesai = findViewById(R.id.btnSelesai);
        cvMulai = findViewById(R.id.cardView16);
        cvSelesai = findViewById(R.id.cardView17);
        //
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);
        lastLocation = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 1000, 0, this);
        //
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocation();
        //
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        sharedPreference = new SharedPreference(getApplicationContext());
        btnBack.setOnClickListener(this);
        btnMulai.setOnClickListener(this);
        btnSelesai.setOnClickListener(this);
        cvSelesai.setVisibility(View.INVISIBLE);
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(DeviceService.ACTION_REALTIME_SAMPLES);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filterLocal);
        //
        chronometer.setBase(SystemClock.elapsedRealtime());
        try {
            age = getCurrentAge(getTodayDate(), sharedPreference.getUser().getDateofBirth());
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        tns = calculateTNS(age);
        txtTNS.setText(Integer.toString(tns));
        api = BaseApi.getRetrofit().create(Api.class);
    }

    private void saveToDB(String id) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = format.format(new Date(System.currentTimeMillis()));
        Sport sport = new Sport();
        sport.setId(id);
        sport.setStart_time(date);
        sport.setTns_target(tns);
        sport.setType("Jogging");
        heartRateHelper.insertSportData(sport);
        response = api.insertSportData(sport.getId(), sport.getStart_time(), sport.getTns_target(), sport.getType());
        response.enqueue(new Callback<CheckUser>() {
            @Override
            public void onResponse(Call<CheckUser> call, Response<CheckUser> response) {

            }

            @Override
            public void onFailure(Call<CheckUser> call, Throwable t) {

            }
        });
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


    @SuppressLint("MissingPermission")
    private void getLocation() {
        if (lastLocation == null) {
            locationManager.requestLocationUpdates(provider, 1000, 0, this);
            lastLocation = locationManager.getLastKnownLocation(provider);
            getLocation();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        this.cLatitude = lastLocation.getLatitude();
        this.cLongitude = lastLocation.getLongitude();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        LatLng myPosition = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 20));
    }

    @Override
    public void onBackPressed() {
        if (id.equals("")) {
            sharedPreference.resetLatitudeLongitude();
            startActivity(new Intent(getApplicationContext(), SportActivity.class));
            finish();
        } else
            showAlertDialog();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        LatLng myPosition = new LatLng(location.getLatitude(), location.getLongitude());
        sharedPreference.setLatitudeLongitude(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 20));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 20));
        //calculate distance
        distance = distance + calculateDistance(cLatitude, cLongitude, location.getLatitude(), location.getLongitude());
        txtJarak.setText(String.format("%.2f", distance));
        this.cLatitude = location.getLatitude();
        this.cLongitude = location.getLongitude();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist * 1.609);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 1000, 0, this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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

    private void pause() {
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
    }

    private void updateDB() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = format.format(new Date(System.currentTimeMillis()));
        Sport sport = new Sport();
        sport.setId(id);
        sport.setEnd_time(date);
        sport.setDuration((int) duration);
        sport.setTns_status(tnsStatus);
        sport.setDistance((float) distance);
        sport.setAverage_heart_rate(heartRateHelper.getAverageSportHearRate(id, sharedPreference.getUser().getEmail()));
        sport.setCalories_burned(calculateBurnedCalories(heartRateHelper.getAverageSportHearRate(id, sharedPreference.getUser().getEmail())));
        heartRateHelper.updateSportData(sport);
        response = api.updateSportData(sport.getId(), sport.getEnd_time(), sport.getDuration(), sport.getTns_status(), sport.getAverage_heart_rate(), sport.getCalories_burned());
        response.enqueue(new Callback<CheckUser>() {
            @Override
            public void onResponse(Call<CheckUser> call, Response<CheckUser> response) {

            }

            @Override
            public void onFailure(Call<CheckUser> call, Throwable t) {

            }
        });
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
        if (heartRate >= tns && tnsStatus.equals("Belum")) {
            tnsStatus = "Iya";
            createNotificationNormalMode();
            Toast.makeText(getApplicationContext(), "Selamat ! Anda telah mencapai TNS Anda !", Toast.LENGTH_LONG).show();
        }
        duration = SystemClock.elapsedRealtime() - chronometer.getBase();
        duration = Math.round(duration / 60000);
        if (tnsStatus.equals("Belum")) {
            if (duration >= 20 && duration < 30) {
                Toast.makeText(getApplicationContext(), "Sudah 20 menit anda belum mecapai TNS, istirahat sejenak" +
                        " apabila merasa lelah. Apabila sangat lelah, boleh berhenti", Toast.LENGTH_LONG).show();
            } else if (duration >= 30) {
                Toast.makeText(getApplicationContext(), "Sistem mendeteksi sudah 30 menit tapi anda belum mencapai TNS," +
                        " mohon segera menghentikan olahraga anda apabila merasa lelah", Toast.LENGTH_LONG).show();
            }
        }
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
        sharedPreference.resetLatitudeLongitude();
        chronometer.stop();
        duration = SystemClock.elapsedRealtime() - chronometer.getBase();
        duration = Math.round(duration / 60000);
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghentikan olahraga ?");
        builder.setTitle("Perhatian!");
        builder.setCancelable(false);
        builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (heartRateHelper.checkHeartRateSportMode(id, sharedPreference.getUser().getEmail())) {
                    stop();
                    updateDB();
                    startActivity(new Intent(getApplicationContext(), SportActivity.class));
                    finish();
                } else {
                    stop();
                    heartRateHelper.deleteSportData(id);
                    response = api.deleteSportData(id);
                    response.enqueue(new Callback<CheckUser>() {
                        @Override
                        public void onResponse(Call<CheckUser> call, Response<CheckUser> response) {

                        }

                        @Override
                        public void onFailure(Call<CheckUser> call, Throwable t) {

                        }
                    });
                    startActivity(new Intent(getApplicationContext(), SportActivity.class));
                    finish();
                }

            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        if (view == btnMulai) {
            if (btnMulai.getText().equals("Mulai")) {
                start();
                btnMulai.setText("Jeda");
            } else {
                pause();
                btnMulai.setText("Mulai");
            }
            cvSelesai.setVisibility(View.VISIBLE);
        } else if (view == btnSelesai) {
            if (!heartRateHelper.checkHeartRateSportMode(id, sharedPreference.getUser().getEmail()))
                showAlertDialog();
            else {
                stop();
                updateDB();
                Intent intent = new Intent(getApplicationContext(), OtherSportDetailActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
            }

        } else if (view == btnBack) {
            if (id.equals("")) {
                sharedPreference.resetLatitudeLongitude();
                startActivity(new Intent(getApplicationContext(), SportActivity.class));
                finish();
            } else
                showAlertDialog();
        }
    }
}