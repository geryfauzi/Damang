package unikom.gery.damang.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import unikom.gery.damang.R;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.sqlite.table.Sleep;
import unikom.gery.damang.util.SharedPreference;

public class SleepActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Sleep> list = new ArrayList<>();
    private HeartRateHelper heartRateHelper;
    private RecyclerView rvSleep;
    private Button btnMulai;
    private TextView btnViewAll;
    private ConstraintLayout cvNoData;
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
        setContentView(R.layout.activity_sleep);

        btnMulai = findViewById(R.id.btnMulai);
        rvSleep = findViewById(R.id.rvSleepData);
        btnViewAll = findViewById(R.id.btnViewAll);
        cvNoData = findViewById(R.id.cvNoData);
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        list = heartRateHelper.getSleepData();
        sharedPreference = new SharedPreference(getApplicationContext());

        btnViewAll.setOnClickListener(this);
        btnMulai.setOnClickListener(this);

        viewSleepData();
    }

    public void viewSleepData() {
        if (list.size() > 0) {
            rvSleep.setVisibility(View.VISIBLE);
            btnViewAll.setVisibility(View.VISIBLE);
            cvNoData.setVisibility(View.GONE);
        } else {
            rvSleep.setVisibility(View.GONE);
            btnViewAll.setVisibility(View.INVISIBLE);
            cvNoData.setVisibility(View.VISIBLE);
        }
        if (sharedPreference.getMode().equals("Normal")) {
            btnMulai.setText("Aktifkan Mode Tidur");
        } else if (sharedPreference.getMode().equals("Sleep")) {
            btnMulai.setText("Hentikan Mode Tidur");
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnMulai) {
            if (sharedPreference.getMode().equals("Normal")) {
                //TODO Sleep Mode
            } else if (sharedPreference.getMode().equals("Sleep")) {
                //TODO Normal Mode
            }
        }
    }
}