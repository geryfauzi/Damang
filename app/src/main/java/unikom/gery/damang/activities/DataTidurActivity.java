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
import unikom.gery.damang.adapter.SleepAdapter;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.sqlite.table.Sleep;

public class DataTidurActivity extends AppCompatActivity {

    private SleepAdapter sleepAdapter;
    private ArrayList<Sleep> list = new ArrayList<>();
    private RecyclerView rvTidur;
    private ImageView btnBack;
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
        setContentView(R.layout.activity_data_tidur);

        rvTidur = findViewById(R.id.rvSleepData);
        btnBack = findViewById(R.id.btnBack);
        rvTidur.setHasFixedSize(true);
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        rvTidur.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list = heartRateHelper.getAllSleepData();
        sleepAdapter = new SleepAdapter(list, getApplicationContext());
        rvTidur.setAdapter(sleepAdapter);

        btnBack.setOnClickListener(view -> {
            finish();
        });

    }
}