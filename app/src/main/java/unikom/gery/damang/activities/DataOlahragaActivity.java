package unikom.gery.damang.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import unikom.gery.damang.R;
import unikom.gery.damang.adapter.SportAdapter;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.sqlite.table.Sport;

public class DataOlahragaActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rvSport;
    private ArrayList<Sport> list = new ArrayList<>();
    private SportAdapter sportAdapter;
    private HeartRateHelper heartRateHelper;

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
        setContentView(R.layout.activity_data_olahraga);

        rvSport = findViewById(R.id.rvSportData);
        btnBack = findViewById(R.id.btnBack);

        rvSport.setHasFixedSize(true);
        rvSport.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        list = heartRateHelper.getAllSportData();
        sportAdapter = new SportAdapter(list, getApplicationContext());
        rvSport.setAdapter(sportAdapter);

        btnBack.setOnClickListener(view -> {
            finish();
        });

    }
}