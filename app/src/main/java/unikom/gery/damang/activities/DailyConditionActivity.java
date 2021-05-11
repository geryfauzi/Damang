package unikom.gery.damang.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import unikom.gery.damang.R;
import unikom.gery.damang.adapter.DailyConditionAdapter;
import unikom.gery.damang.model.HeartRate;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.util.SharedPreference;

public class DailyConditionActivity extends AppCompatActivity {

    private HeartRateHelper heartRateHelper;
    private ArrayList<HeartRate> arrayList;
    private RecyclerView rvDailyCondition;
    private DailyConditionAdapter dailyConditionAdapter;
    private SharedPreference sharedPreference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Change statusbar color
        getSupportActionBar().hide();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //
        setContentView(R.layout.activity_daily_condition);
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        sharedPreference = new SharedPreference(getApplicationContext());
        //RecyclerView Binding
        rvDailyCondition = findViewById(R.id.rvDailyCondition);
        rvDailyCondition.setHasFixedSize(true);
        rvDailyCondition.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        arrayList = heartRateHelper.getDailyCondition(sharedPreference.getUser().getEmail());
        dailyConditionAdapter = new DailyConditionAdapter(arrayList, getApplicationContext());
        rvDailyCondition.setAdapter(dailyConditionAdapter);
    }
}