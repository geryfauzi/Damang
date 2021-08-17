package unikom.gery.damang.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import unikom.gery.damang.R;
import unikom.gery.damang.model.DetailHeartRate;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.sqlite.table.Sport;
import unikom.gery.damang.util.SharedPreference;

public class JoggingSportDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private TextView txtJarak, txtDurasi, txtKalori, txtTanggal, txtWaktuMulai, txtWaktuSelesai;
    private TextView txtRataRata, txtTerendah, txtTertinggi;
    private SharedPreference sharedPreference;
    private HeartRateHelper heartRateHelper;
    private ArrayList<DetailHeartRate> arrayList = new ArrayList<>();
    private Sport sport = new Sport();
    private String id;
    private PolylineOptions polylineOptions;
    private ImageView btnBack;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Change statusbar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().hide();
        //
        setContentView(R.layout.activity_jogging_sport_detail);
        txtDurasi = findViewById(R.id.txtWaktu);
        txtTanggal = findViewById(R.id.txtTanggal);
        txtWaktuMulai = findViewById(R.id.txtWaktuMulai);
        txtWaktuSelesai = findViewById(R.id.txtWaktuSelesai);
        txtRataRata = findViewById(R.id.txtRataRata);
        txtTerendah = findViewById(R.id.txtTerendah);
        txtTertinggi = findViewById(R.id.txtTertinggi);
        txtKalori = findViewById(R.id.txtKaloriTerbakar);
        txtJarak = findViewById(R.id.txtJarak);
        btnBack = findViewById(R.id.btnBack);
        sharedPreference = new SharedPreference(getApplicationContext());
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        polylineOptions = new PolylineOptions().color(Color.parseColor("#2DA4E1")).width(15);

        id = getIntent().getStringExtra("id");
        arrayList = heartRateHelper.getSportDetailHeartRate(sharedPreference.getUser().getEmail(), id);
        sport = heartRateHelper.getOtherSportDetail(id);
        btnBack.setOnClickListener(view -> {
            finish();
        });

        try {
            updateView();
        } catch (ParseException exception) {
            Toast.makeText(getApplicationContext(), "Terjadi kesalahan!", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateView() throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sport.getId());
        String parseDate = new SimpleDateFormat("dd MMMM yyyy").format(date);
        txtDurasi.setText(String.valueOf(sport.getDuration()));
        txtTanggal.setText(parseDate);
        txtWaktuMulai.setText(sport.getStart_time());
        txtWaktuSelesai.setText(sport.getEnd_time());
        txtRataRata.setText(sport.getAverage_heart_rate() + " bpm");
        txtTerendah.setText(nilaiTerkecil(arrayList) + " bpm");
        txtTertinggi.setText(nilaiTertinggi(arrayList) + " bpm");
        txtKalori.setText(String.valueOf(sport.getCalories_burned()));
        txtJarak.setText(String.format("%.2f", sport.getDistance()));
    }

    private int nilaiTerkecil(ArrayList<DetailHeartRate> list) {
        int min = list.get(0).getHeartRate();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getHeartRate() < min)
                min = list.get(i).getHeartRate();
        }
        return min;
    }

    private int nilaiTertinggi(ArrayList<DetailHeartRate> list) {
        int max = list.get(0).getHeartRate();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getHeartRate() > max)
                max = list.get(i).getHeartRate();
        }
        return max;
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng middlePoint = new LatLng(arrayList.get(arrayList.size() / 2).getLatitude(), arrayList.get(arrayList.size() / 2).getLongitude());
        for (int i = 0; i < arrayList.size(); i++) {
            LatLng myPosition = new LatLng(arrayList.get(i).getLatitude(), arrayList.get(i).getLongitude());
            polylineOptions.add(myPosition);
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middlePoint, 17));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(middlePoint, 17));
        googleMap.addPolyline(polylineOptions);
    }
}