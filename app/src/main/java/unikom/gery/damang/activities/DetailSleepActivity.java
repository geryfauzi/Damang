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
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.sqlite.table.Sleep;
import unikom.gery.damang.util.SharedPreference;

public class DetailSleepActivity extends AppCompatActivity {

    private String id = "";
    private HeartRateHelper heartRateHelper;
    private Sleep sleep;
    private TextView txtTanggalTidur, txtDurasiTidur, txtSkorTidur, txtKeteranganSkor, txtCatatan;
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
        //
        getSupportActionBar().hide();
        setContentView(R.layout.activity_detail_sleep);

        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        id = getIntent().getStringExtra("id");
        sleep = heartRateHelper.getDetailSleepData(id);
        txtCatatan = findViewById(R.id.txtCatatan);
        txtDurasiTidur = findViewById(R.id.textView41);
        txtKeteranganSkor = findViewById(R.id.txtKeteranganSkor);
        txtSkorTidur = findViewById(R.id.txtSkorTidur);
        txtTanggalTidur = findViewById(R.id.txtTanggalTidur);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(view -> {
            finish();
        });

        sharedPreference = new SharedPreference(getApplicationContext());
        try {
            updateView();
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateView() throws ParseException {
        //Update Tanggal
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sleep.getStart_time());
            String parseDate = new SimpleDateFormat("dd MMMM yyyy").format(date);
            txtTanggalTidur.setText(parseDate);
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        //Update Durasi
        int hour = sleep.getDuration() / 60;
        if (hour > 0)
            txtDurasiTidur.setText(hour + " Jam Durasi Tidur");
        else
            txtDurasiTidur.setText(sleep.getDuration() + " Menit Durasi Tidur");
        //Update Skor Tidur
        txtSkorTidur.setText(String.valueOf(sleep.getStatus()));
        int skor = Integer.parseInt(sleep.getStatus());
        if (skor >= 90) {
            txtKeteranganSkor.setText("Sangat Baik");
            txtKeteranganSkor.setTextColor(Color.parseColor("#19C118"));
        } else if (skor >= 80) {
            txtKeteranganSkor.setText("Baik");
            txtKeteranganSkor.setTextColor(Color.parseColor("#5C95C4"));
        } else if (skor >= 70) {
            txtKeteranganSkor.setText("Cukup");
            txtKeteranganSkor.setTextColor(Color.parseColor("#fbc531"));
        } else if (skor >= 60) {
            txtKeteranganSkor.setText("Kurang");
            txtKeteranganSkor.setTextColor(Color.parseColor("#FF5959"));
        } else if (skor < 60) {
            txtKeteranganSkor.setText("Sangat Kurang");
            txtKeteranganSkor.setTextColor(Color.parseColor("#FF5959"));
        }
        //Update catatan.
        txtCatatan.append("Berdasarkan dari durasi tidur dan rata - rata detak jantung anda" +
                " ketika tidur. Maka skor tidur anda adalah " + sleep.getStatus() + " dengan keterangan" +
                " " + txtKeteranganSkor.getText().toString() + ".");
        updateNoteHeartRate();
        updateNoteDuration();
    }

    private void updateNoteHeartRate() {
        if (sharedPreference.getUser().getGender().equals("Laki - Laki")) {
            if (sleep.getAverage_heart_rate() > 80)
                txtCatatan.append(" Hal ini dipengaruhi karena rata - rata detak jantung anda tinggi" +
                        " ketika tidur. Apabila anda tidur dengan detak jantung yang tinggi, itu berarti" +
                        " tidur anda kurang nyenyak karena anda sedang banyak pikiran dan bermimpi" +
                        " buruk. Ketika anda akan tidur, sebaiknya rileks kan pikiran anda. Usahakan" +
                        " jang terlalu memikirkan apapun secara berlebihan ketika anda hendak tidur," +
                        " itu agar pikiran anda santai dan anda bisa tidur dengan tenang.");
            else if (sleep.getAverage_heart_rate() < 50)
                txtCatatan.append(" Hal ini dipengaruhi karena rata - rata detak jantung anda rendah" +
                        " ketika tidur. Hal ini terjadi karena anda mengalami kecapean yang berlebihan" +
                        " sebelum anda tidur. Direkomendasikan sebelum anda tidur, istirahatkan tubuh" +
                        " terlebih dahulu. Jangan langsung tidur ketika anda mengalami kecapean berlebihan.");
            else
                txtCatatan.append(" Rata - rata detak jantung anda berada di angka normal ketika anda" +
                        " tidur. Ini menandakan bahwa anda tidur dengan nyenyak.");
        } else {
            if (sleep.getAverage_heart_rate() > 82)
                txtCatatan.append(" Hal ini dipengaruhi karena rata - rata detak jantung anda tinggi" +
                        " ketika tidur. Apabila anda tidur dengan detak jantung yang tinggi, itu berarti" +
                        " tidur anda kurang nyenyak karena anda sedang banyak pikiran dan bermimpi" +
                        " buruk. Ketika anda akan tidur, sebaiknya rileks kan pikiran anda. Usahakan" +
                        " jang terlalu memikirkan apapun secara berlebihan ketika anda hendak tidur," +
                        " itu agar pikiran anda santai dan anda bisa tidur dengan tenang.");
            else if (sleep.getAverage_heart_rate() < 53)
                txtCatatan.append(" Hal ini dipengaruhi karena rata - rata detak jantung anda rendah" +
                        " ketika tidur. Hal ini terjadi karena anda mengalami kecapean yang berlebihan" +
                        " sebelum anda tidur. Direkomendasikan sebelum anda tidur, istirahatkan tubuh" +
                        " terlebih dahulu. Jangan langsung tidur ketika anda mengalami kecapean berlebihan.");
            else
                txtCatatan.append(" Rata - rata detak jantung anda berada di angka normal ketika anda" +
                        " tidur. Ini menandakan bahwa anda tidur dengan nyenyak.");
        }
    }

    private void updateNoteDuration() throws ParseException {
        int age = getCurrentAge(getTodayDate(), sharedPreference.getUser().getDateofBirth());
        int hour = sleep.getDuration() / 60;
        if (hour == 0)
            hour = 1;
        //
        if (age >= 3 && age <= 5) {
            if (hour > 13)
                txtCatatan.append(" Durasi tidur anda termasuk berlebihan untuk usia anda. Sebaiknya" +
                        " anda jangan tidur berlebihan karena dapat menyebabkan pikiran anda terganggu." +
                        " Durasi tidur yang normal untuk usia anda adalah 10 sampai 13 jam");
            else if (hour >= 7 && hour <= 9)
                txtCatatan.append(" Durasi tidur anda termasuk kurang untuk usia anda. Sebaiknya, anda" +
                        " tidur lebih awal lagi supaya mendapat durasi tidur yang cukup");
            else if (hour < 7)
                txtCatatan.append(" Durasi tidur anda termasuk sangat kurang untuk usia anda. Sebaiknya, anda" +
                        " tidur lebih awal lagi supaya mendapat durasi tidur yang cukup");
            //
        } else if (age >= 14 && age <= 17) {
            if (hour > 10)
                txtCatatan.append(" Durasi tidur anda termasuk berlebihan untuk usia anda. Sebaiknya" +
                        " anda jangan tidur berlebihan karena dapat menyebabkan pikiran anda terganggu." +
                        " Durasi tidur yang normal untuk usia anda adalah 8 sampai 10 jam");
            else if (hour >= 5 && hour <= 7)
                txtCatatan.append(" Durasi tidur anda termasuk kurang untuk usia anda. Sebaiknya, anda" +
                        " tidur lebih awal lagi supaya mendapat durasi tidur yang cukup");
            else if (hour < 5)
                txtCatatan.append(" Durasi tidur anda termasuk sangat kurang untuk usia anda. Sebaiknya, anda" +
                        " tidur lebih awal lagi supaya mendapat durasi tidur yang cukup");
            //
        } else if (age >= 18 && age <= 25) {
            if (hour > 9)
                txtCatatan.append(" Durasi tidur anda termasuk berlebihan untuk usia anda. Sebaiknya" +
                        " anda jangan tidur berlebihan karena dapat menyebabkan pikiran anda terganggu." +
                        " Durasi tidur yang normal untuk usia anda adalah 7 sampai 9 jam");
            else if (hour >= 4 && hour <= 6)
                txtCatatan.append(" Durasi tidur anda termasuk kurang untuk usia anda. Sebaiknya, anda" +
                        " tidur lebih awal lagi supaya mendapat durasi tidur yang cukup");
            else if (hour < 4)
                txtCatatan.append(" Durasi tidur anda termasuk sangat kurang untuk usia anda. Sebaiknya, anda" +
                        " tidur lebih awal lagi supaya mendapat durasi tidur yang cukup");
            //
        } else if (age >= 26 && age <= 64) {
            if (hour > 9)
                txtCatatan.append(" Durasi tidur anda termasuk berlebihan untuk usia anda. Sebaiknya" +
                        " anda jangan tidur berlebihan karena dapat menyebabkan pikiran anda terganggu." +
                        " Durasi tidur yang normal untuk usia anda adalah 7 sampai 9 jam");
            else if (hour >= 4 && hour <= 6)
                txtCatatan.append(" Durasi tidur anda termasuk kurang untuk usia anda. Sebaiknya, anda" +
                        " tidur lebih awal lagi supaya mendapat durasi tidur yang cukup");
            else if (hour < 4)
                txtCatatan.append(" Durasi tidur anda termasuk sangat kurang untuk usia anda. Sebaiknya, anda" +
                        " tidur lebih awal lagi supaya mendapat durasi tidur yang cukup");
            //
        } else if (age >= 65) {
            if (hour > 8)
                txtCatatan.append(" Durasi tidur anda termasuk berlebihan untuk usia anda. Sebaiknya" +
                        " anda jangan tidur berlebihan karena dapat menyebabkan pikiran anda terganggu." +
                        " Durasi tidur yang normal untuk usia anda adalah 7 sampai 8 jam");
            else if (hour >= 4 && hour <= 6)
                txtCatatan.append(" Durasi tidur anda termasuk kurang untuk usia anda. Sebaiknya, anda" +
                        " tidur lebih awal lagi supaya mendapat durasi tidur yang cukup");
            else if (hour < 4)
                txtCatatan.append(" Durasi tidur anda termasuk sangat kurang untuk usia anda. Sebaiknya, anda" +
                        " tidur lebih awal lagi supaya mendapat durasi tidur yang cukup");
            //
        }
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
}