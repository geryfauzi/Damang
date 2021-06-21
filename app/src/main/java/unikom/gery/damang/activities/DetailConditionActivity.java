package unikom.gery.damang.activities;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import unikom.gery.damang.R;
import unikom.gery.damang.model.DetailHeartRate;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.util.SharedPreference;

public class DetailConditionActivity extends AppCompatActivity {

    private String date, parseDate, averageStatus, currentStatus;
    private SharedPreference sharedPreference;
    private HeartRateHelper heartRateHelper;
    private ArrayList<DetailHeartRate> arrayList;
    private TextView txtTerendah, txtRataRata, txtTertinggi, txtHasilAnalisis;
    private LineChart lineChart;
    private ImageView btnBack;
    private int age, currentHeartRate, averageHeartRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_detail_condition);

        sharedPreference = new SharedPreference(getApplicationContext());
        date = getIntent().getStringExtra("date");
        parseDate = getIntent().getStringExtra("parseDate");
        heartRateHelper = HeartRateHelper.getInstance(getApplicationContext());
        arrayList = heartRateHelper.getDetailDailyCondition(sharedPreference.getUser().getEmail(), date);

        txtTerendah = findViewById(R.id.txtTerendah);
        txtRataRata = findViewById(R.id.txtRataRata);
        txtTertinggi = findViewById(R.id.txtTertinggi);
        txtHasilAnalisis = findViewById(R.id.txtHasilAnalisis);
        lineChart = findViewById(R.id.heartRateChart);
        btnBack = findViewById(R.id.btnBack);

        try {
            age = getCurrentAge(getTodayDate(), sharedPreference.getUser().getDateofBirth());
            currentHeartRate = arrayList.get(arrayList.size() - 1).getHeartRate();
            averageHeartRate = hitungRataRata(arrayList);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        setToview();
        setBarChart(arrayList);
        updateAnalyst();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setBarChart(ArrayList<DetailHeartRate> list) {
        ArrayList listData = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            listData.add(new BarEntry(i, list.get(i).getHeartRate()));
        }
        ArrayList listHour = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            listHour.add(list.get(i).getHour());
        }

        lineChart.setBackgroundColor(Color.parseColor("#FAFAFA"));
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        LineDataSet set1;
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(listData);
            set1.notifyDataSetChanged();
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(listData, "Data detak jantung pada " + parseDate);
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setValueTextSize(9f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return lineChart.getAxisLeft().getAxisMinimum();
                }
            });

            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
            set1.setFillDrawable(drawable);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            lineChart.setData(data);

            lineChart.animateX(1500);
            Legend l = lineChart.getLegend();
            l.setForm(Legend.LegendForm.LINE);
        }
    }

    private void updateAnalyst() {
        averageStatus = getCurrentHeartRateStatus(age, averageHeartRate);
        currentStatus = getCurrentHeartRateStatus(age, currentHeartRate);
        String status = getCurrentCondition(averageStatus, currentStatus);

        txtHasilAnalisis.append("Berdasarkan dari hasil analisis data detak jantung,");
        txtHasilAnalisis.append(status + ".");
        txtHasilAnalisis.append("Rata - rata detak jantung anda berada di angka " + averageStatus + ",");
        txtHasilAnalisis.append(" dan detak jantung anda yang terkini berada di angka " + currentStatus);
        txtHasilAnalisis.append(" untuk usia anda.");
        if (status.equals("Kesehatan anda kurang baik")) {
            txtHasilAnalisis.append(" Hasil kesehatan anda ini mungkin diakibatkan karena anda sedang" +
                    " panik, sedikit pusing, cemas, depresi atau sedang banyak pikiran. Damang menyarankan untuk" +
                    " tidak membiarkan kondisi seperti ini terlalu lama. Apabila" +
                    " kegiatan anda sekarang tidak terlalu penting, sebaiknya anda beristirahat" +
                    " dan santai dulu sejenak sampai detak jantung anda mulai normal kembali.");
        } else if (status.equals("Kesehatan anda tidak baik")) {
            txtHasilAnalisis.append("Hasil kesehatan anda ini mungkin diakibatkan karena anda sedang" +
                    " pusing berat, stress berat, demam, atau karena lelah berlebihan." +
                    " Damang sangat menyarankan anda untuk beristirahat, dan segera menemui dokter" +
                    " apabila tubuh anda merasa tidak nyaman.");
        }
    }

    private String getCurrentCondition(String average, String current) {
        String status = "";
        if (average.equals("Normal") && current.equals("Normal"))
            status = "Kesehatan anda baik";
        else if (average.equals("Normal") && current.equals("Tinggi"))
            status = "Kesehatan anda baik";
        else if (average.equals("Normal") && current.equals("Rendah"))
            status = "Kesehatan anda baik";
        else if (average.equals("Tinggi") && current.equals("Tinggi"))
            status = "Kesehatan anda tidak baik";
        else if (average.equals("Tinggi") && current.equals("Normal"))
            status = "Kesehatan anda kurang baik";
        else if (average.equals("Tinggi") && current.equals("Rendah"))
            status = "Kesehatan anda kurang baik";
        else if (average.equals("Rendah") && current.equals("Normal"))
            status = "Kesehatan anda kurang baik";
        else if (average.equals("Rendah") && current.equals("Rendah"))
            status = "Kesehatan anda tidak baik";
        else if (average.equals("Rendah") && current.equals("Tinggi"))
            status = "Kesehatan anda tidak baik";
        return status;
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

    private String getCurrentHeartRateStatus(int age, int heartRate) {
        String status = "";
        if (age < 2) {
            if (heartRate >= 80 && heartRate <= 160)
                status = "Normal";
            else if (heartRate > 160)
                status = "Tinggi";
            else
                status = "Rendah";
        } else if (age <= 10) {
            if (heartRate >= 70 && heartRate <= 120)
                status = "Normal";
            else if (heartRate > 120)
                status = "Tinggi";
            else
                status = "Rendah";
        } else {
            if (heartRate >= 54 && heartRate <= 110)
                status = "Normal";
            else if (heartRate > 110)
                status = "Tinggi";
            else
                status = "Rendah";
        }
        return status;
    }

    private void setToview() {
        txtTertinggi.setText(nilaiTertinggi(arrayList) + "");
        txtRataRata.setText(hitungRataRata(arrayList) + "");
        txtTerendah.setText(nilaiTerkecil(arrayList) + "");
    }

    private int hitungRataRata(ArrayList<DetailHeartRate> list) {
        int total = 0;
        for (int i = 0; i < list.size(); i++) {
            total = total + list.get(i).getHeartRate();
        }
        return total / list.size();
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
}