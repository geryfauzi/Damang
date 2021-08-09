package unikom.gery.damang.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import unikom.gery.damang.R;
import unikom.gery.damang.adapter.RumahSakitAdapter;
import unikom.gery.damang.api.Api;
import unikom.gery.damang.api.BaseApi;
import unikom.gery.damang.response.PlaceResponse;
import unikom.gery.damang.response.Properties;

public class NearHospitalActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private String provider;
    private Location lastLocation;
    private ArrayList<PlaceResponse> list = new ArrayList<>();
    private ImageView btnBack;
    private RecyclerView rvHospital;
    private RumahSakitAdapter rumahSakitAdapter;
    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_hospital);
        //Change statusbar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().hide();
        //
        btnBack = findViewById(R.id.btnBack);
        rvHospital = findViewById(R.id.rvRumahSakit);
        rvHospital.setHasFixedSize(true);
        rvHospital.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        progressDialog = new ProgressDialog(NearHospitalActivity.this);
        progressDialog.setTitle("Harap Tunggu...");
        //
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);
        lastLocation = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 1000, 0, this);
        getLocation();
        //
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        if (lastLocation == null) {
            locationManager.requestLocationUpdates(provider, 1000, 0, this);
            lastLocation = locationManager.getLastKnownLocation(provider);
            getLocation();
        } else if (lastLocation != null) {
            searchNearPlace();
        }
    }

    private void searchNearPlace() {
        progressDialog.show();
        String categories = "healthcare.hospital";
        String filter = "circle:" + lastLocation.getLongitude() + "," + lastLocation.getLatitude() + ",5000";
        String bias = "proximity:" + lastLocation.getLongitude() + "," + lastLocation.getLatitude();
        String apiKey = "8a5772921fcb4a08aee2bc08d87a6540";

        Api api = BaseApi.getRetrofit("https://api.geoapify.com/").create(Api.class);
        Call<PlaceResponse> response = api.getNearbyPlace(categories, filter, bias, 20, apiKey);
        response.enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                if (response.body().getType() != null) {
                    progressDialog.dismiss();
                    ArrayList<Properties> list = (ArrayList<Properties>) response.body().getFeatures();
                    rumahSakitAdapter = new RumahSakitAdapter(list, getApplicationContext());
                    rvHospital.setAdapter(rumahSakitAdapter);
                }
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

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
}