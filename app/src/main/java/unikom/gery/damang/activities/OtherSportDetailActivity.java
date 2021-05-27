package unikom.gery.damang.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import unikom.gery.damang.R;
import unikom.gery.damang.model.DetailHeartRate;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.sqlite.table.Sport;
import unikom.gery.damang.util.SharedPreference;

public class OtherSportDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtDurasi, txtTNSStatus, txtTNSTarget, txtTanggal, txtWaktuMulai, txtWaktuSelesai;
    private TextView txtRataRata, txtTerendah, txtTertinggi, txtKaloriTerbakar;
    private SharedPreference sharedPreference;
    private String id;
    private HeartRateHelper heartRateHelper;
    private ArrayList<DetailHeartRate> arrayList;
    private ImageView btnBack;
    private Sport sport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_other_sport_detail);
        //View Binding
        txtDurasi = findViewById(R.id.txtDurasi);
        txtTNSStatus = findViewById(R.id.txtStatusTNS);
        txtTNSTarget = findViewById(R.id.txtTargetTNS);
        txtTanggal = findViewById(R.id.txtTanggal);
        txtWaktuMulai = findViewById(R.id.txtWaktuMulai);
        txtWaktuSelesai = findViewById(R.id.txtWaktuSelesai);
        txtRataRata = findViewById(R.id.txtRataRata);
        txtTerendah = findViewById(R.id.txtTerendah);
        txtTertinggi = findViewById(R.id.txtTertinggi);
        txtKaloriTerbakar = findViewById(R.id.txtKaloriTerbakar);
        btnBack = findViewById(R.id.btnBack);

        id = getIntent().getStringExtra("id");
        sharedPreference = new SharedPreference(getApplicationContext());
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        arrayList = heartRateHelper.getSportDetailHeartRate(sharedPreference.getUser().getEmail(), id);
        sport = heartRateHelper.getOtherSportDetail(id);
        btnBack.setOnClickListener(this);
        try {
            updateView();
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateView() throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sport.getId());
        String parseDate = new SimpleDateFormat("dd MMMM yyyy").format(date);
        txtDurasi.setText(String.valueOf(sport.getDuration()));
        txtTNSStatus.setText(sport.getTns_status());
        txtTNSTarget.setText(String.valueOf(sport.getTns_target()));
        txtTanggal.setText(parseDate);
        txtWaktuMulai.setText(sport.getStart_time());
        txtWaktuSelesai.setText(sport.getEnd_time());
        txtRataRata.setText(String.valueOf(sport.getAverage_heart_rate()) + " bpm");
        txtTerendah.setText(String.valueOf(nilaiTerkecil(arrayList)) + " bpm");
        txtTertinggi.setText(String.valueOf(nilaiTertinggi(arrayList)) + " bpm");
        txtKaloriTerbakar.setText(String.valueOf(sport.getCalories_burned()));
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
    public void onClick(View view) {
        finish();
    }
}