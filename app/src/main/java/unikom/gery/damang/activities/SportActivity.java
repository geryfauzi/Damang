package unikom.gery.damang.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import unikom.gery.damang.GBApplication;
import unikom.gery.damang.R;
import unikom.gery.damang.adapter.SportAdapter;
import unikom.gery.damang.devices.DeviceManager;
import unikom.gery.damang.impl.GBDevice;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.sqlite.table.Sport;
import unikom.gery.damang.util.GB;

import static unikom.gery.damang.util.GB.toast;

public class SportActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnBack;
    private TextView btnViewAll;
    private ConstraintLayout cvNoData;
    private CardView btnOtherSport, btnJogging, btnCardio;
    private RecyclerView rvSport;
    private ArrayList<Sport> arrayList;
    private HeartRateHelper heartRateHelper;
    private DeviceManager deviceManager;
    private List<GBDevice> deviceList;
    private GBDevice device;
    private SportAdapter sportAdapter;

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
        setContentView(R.layout.activity_sport);

        rvSport = findViewById(R.id.rvSportData);
        btnBack = findViewById(R.id.btnBack);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnOtherSport = findViewById(R.id.btnSportOther);
        btnJogging = findViewById(R.id.btnJogging);
        btnCardio = findViewById(R.id.btnCardioLantai);
        cvNoData = findViewById(R.id.cvNoData);
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        arrayList = heartRateHelper.getSportData();
        deviceManager = ((GBApplication) getApplication()).getDeviceManager();
        deviceList = deviceManager.getDevices();

        btnBack.setOnClickListener(this);
        btnOtherSport.setOnClickListener(this);
        btnJogging.setOnClickListener(this);
        btnCardio.setOnClickListener(this);
        btnViewAll.setOnClickListener(this);
        setView();
    }

    private void setView() {
        if (arrayList.size() > 0) {
            btnViewAll.setVisibility(View.VISIBLE);
            cvNoData.setVisibility(View.GONE);
            rvSport.setVisibility(View.VISIBLE);
            sportAdapter = new SportAdapter(arrayList, getApplicationContext());
            rvSport.setHasFixedSize(true);
            rvSport.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rvSport.setAdapter(sportAdapter);
        } else {
            btnViewAll.setVisibility(View.INVISIBLE);
            rvSport.setVisibility(View.GONE);
            cvNoData.setVisibility(View.VISIBLE);
        }
    }

    private void checkGPS() {
        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            if (lManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                startActivity(new Intent(getApplicationContext(), JoggingSportActivity.class));
                finish();
            } else {
                toast(SportActivity.this, "Harap nyalakan GPS untuk menggunakan fitur ini", Toast.LENGTH_SHORT, GB.ERROR);
                SportActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                toast(SportActivity.this, "Harap nyalakan GPS untuk menggunakan fitur ini", Toast.LENGTH_SHORT, GB.ERROR);
                return;
            }
        } catch (Exception error) {

        }
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

    private void showAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bagimana kemampuan anda dalam berolahraga ?");
        builder.setMessage("Kedepannya level olahraga akan menyesuaikan secara otomatis berdasarkan detak jantung.");
        final View layout = getLayoutInflater().inflate(R.layout.custom_alert_dialog_cardio, null);
        builder.setView(layout);

        final RadioGroup radioGroup = layout.findViewById(R.id.btnGroup);
        builder.setPositiveButton("Terapkan", (dialogInterface, i) -> {
            int selectedID = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = layout.findViewById(selectedID);
            Intent intent = new Intent(getApplicationContext(), CardioMenuActivity.class);
            intent.putExtra("level", radioButton.getText().toString());
            startActivity(intent);
            finish();
            dialogInterface.dismiss();
        });
        builder.setNegativeButton("Batal", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view == btnBack)
            finish();
        else if (view == btnOtherSport) {
            if (checkDevice()) {
                startActivity(new Intent(getApplicationContext(), OtherSportActivity.class));
                finish();
            } else
                Toast.makeText(getApplicationContext(), "Harap hubungkan dahulu sistem dengan perangkat wearable device", Toast.LENGTH_SHORT).show();
        } else if (view == btnJogging) {
            if (checkDevice()) {
                checkPermission();
                checkGPS();
            } else
                Toast.makeText(getApplicationContext(), "Harap hubungkan dahulu sistem dengan perangkat wearable device", Toast.LENGTH_SHORT).show();
        } else if (view == btnCardio) {
            if (checkDevice()) {
                showAlertDialog();
            } else
                Toast.makeText(getApplicationContext(), "Harap hubungkan dahulu sistem dengan perangkat wearable device", Toast.LENGTH_SHORT).show();
        } else if (view == btnViewAll) {
            startActivity(new Intent(getApplicationContext(), DataOlahragaActivity.class));
        }
    }

    private void checkPermission() {
        List<String> wantedPermissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "Harap berikan izin lokasi untuk menggunakan fitur ini", Toast.LENGTH_SHORT).show();
            wantedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "Harap berikan izin lokasi untuk menggunakan fitur ini", Toast.LENGTH_SHORT).show();
            wantedPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            return;
        }
    }
}