package unikom.gery.damang.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import unikom.gery.damang.R;
import unikom.gery.damang.util.SharedPreference;

public class CaloriesActivity extends AppCompatActivity {

    private TextView txtJumlahLangkah, txtKaloriTerbakar, txtTinggiBadan, txtBeratBadan;
    private TextView txtStatus, txtKebutuhanKalori;
    private ImageView btnBack;
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
        getSupportActionBar().hide();
        //
        setContentView(R.layout.activity_calories);

        txtStatus = findViewById(R.id.txtStatus);
        txtKebutuhanKalori = findViewById(R.id.txtKebutuhanKalori);
        txtBeratBadan = findViewById(R.id.txtBeratBadan);
        txtTinggiBadan = findViewById(R.id.txtTinggiBadan);
        txtKaloriTerbakar = findViewById(R.id.txtKaloriTerbakar);
        txtJumlahLangkah = findViewById(R.id.txtJumlahLangkah);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(view -> {
            finish();
        });

        sharedPreference = new SharedPreference(getApplicationContext());
        try {
            viewCalories();
        } catch (Exception error) {
            Toast.makeText(getApplicationContext(), " Terjadi kesalahan!", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewCalories() throws ParseException {
        String burnedCalories = String.format("%.2f", getBurnedCalories(sharedPreference.getSteps(), Math.round(sharedPreference.getUser().getWeight())));
        int age = getCurrentAge(getTodayDate(), sharedPreference.getUser().getDateofBirth());
        int calories = calculateCalories(Math.round(sharedPreference.getUser().getWeight()), Math.round(sharedPreference.getUser().getHeight()), age);
        String status = getStatusBMI(Math.round(sharedPreference.getUser().getWeight()), Math.round(sharedPreference.getUser().getHeight()));
        //Update View
        txtJumlahLangkah.setText(sharedPreference.getSteps() + " Langkah");
        txtKaloriTerbakar.setText(burnedCalories + " Kalori");
        txtTinggiBadan.setText(Math.round(sharedPreference.getUser().getHeight()) + " CM");
        txtBeratBadan.setText(Math.round(sharedPreference.getUser().getWeight()) + " KG");
        txtKebutuhanKalori.setText(calories + " Kalori");
        txtStatus.setText(status);
    }

    private String getStatusBMI(int beratBadan, int tinggiBadan) {
        String staus = "Normal";
        float tinggiMeter = Float.valueOf(tinggiBadan) / 100;
        float bmi = beratBadan / (tinggiMeter * tinggiMeter);
        if (bmi < 18.5)
            staus = "Berat Badan Kurang";
        else if (bmi >= 18.5 && bmi <= 24.9)
            staus = "Berat Badan Normal";
        else if (bmi >= 25.0 && bmi <= 29.9)
            staus = "Berat Badan Berlebih";
        else if (bmi >= 30.0)
            staus = "Obesitas";
        return staus;
    }

    private int calculateCalories(int beratBadan, int tinggiBadan, int usia) {
        int kalori = 0;
        if (sharedPreference.getUser().getGender().equals("Laki - Laki"))
            kalori = (int) (66 + (13.7 * beratBadan) + (5 * tinggiBadan) - (6.8 * usia));
        else
            kalori = (int) (655 + (9.6 * beratBadan) + (1.8 * tinggiBadan) - (4.7 * usia));
        return kalori;
    }

    private String getTodayDate() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(System.currentTimeMillis()));
    }

    private float getBurnedCalories(int jumlahLangkah, int beratBadan) {
        float calories = 0;
        if (beratBadan >= 45 && beratBadan <= 54)
            calories = (float) ((28.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 55 && beratBadan <= 63)
            calories = (float) ((33.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 64 && beratBadan <= 72)
            calories = (float) ((38.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 73 && beratBadan <= 81)
            calories = (float) ((40.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 82 && beratBadan <= 90)
            calories = (float) ((45.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 91 && beratBadan <= 99)
            calories = (float) ((50.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 100 && beratBadan <= 113)
            calories = (float) ((55.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 114 && beratBadan <= 124)
            calories = (float) ((62.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 125 && beratBadan <= 135)
            calories = (float) ((68.0 / 1000) * jumlahLangkah);
        else if (beratBadan >= 136)
            calories = (float) ((75.0 / 1000) * jumlahLangkah);
        return calories;
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

}