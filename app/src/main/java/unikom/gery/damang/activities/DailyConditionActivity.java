package unikom.gery.damang.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import unikom.gery.damang.R;
import unikom.gery.damang.adapter.DailyConditionAdapter;
import unikom.gery.damang.model.HeartRate;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.util.SharedPreference;

public class DailyConditionActivity extends AppCompatActivity implements View.OnClickListener {

    private HeartRateHelper heartRateHelper;
    private ArrayList<HeartRate> arrayList;
    private RecyclerView rvDailyCondition;
    private DailyConditionAdapter dailyConditionAdapter;
    private SharedPreference sharedPreference;
    private ImageView btnBack, btnFilter;

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
        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);
        //RecyclerView Binding
        rvDailyCondition = findViewById(R.id.rvDailyCondition);
        rvDailyCondition.setHasFixedSize(true);
        rvDailyCondition.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        arrayList = heartRateHelper.getDailyCondition(sharedPreference.getUser().getEmail());
        dailyConditionAdapter = new DailyConditionAdapter(arrayList, getApplicationContext());
        rvDailyCondition.setAdapter(dailyConditionAdapter);
        //
        btnFilter.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void showAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih tipe laporan");
        final View layout = getLayoutInflater().inflate(R.layout.custom_alert_dialog_laporan_kesehatan, null);
        builder.setView(layout);

        final RadioGroup radioGroup = layout.findViewById(R.id.btnGroup);
        builder.setPositiveButton("Terapkan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int selectedID = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = layout.findViewById(selectedID);
                Toast.makeText(getApplicationContext(), radioButton.getText(), Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view == btnBack)
            finish();
        else if (view == btnFilter) {
            showAlertDialog();
        }
    }
}