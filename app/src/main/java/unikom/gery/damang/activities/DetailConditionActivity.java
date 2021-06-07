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

import java.util.ArrayList;

import unikom.gery.damang.R;
import unikom.gery.damang.model.DetailHeartRate;
import unikom.gery.damang.sqlite.dml.HeartRateHelper;
import unikom.gery.damang.util.SharedPreference;

public class DetailConditionActivity extends AppCompatActivity {

    private String date, parseDate;
    private SharedPreference sharedPreference;
    private HeartRateHelper heartRateHelper;
    private ArrayList<DetailHeartRate> arrayList;
    private TextView txtTerendah, txtRataRata, txtTertinggi;
    private LineChart lineChart;
    private ImageView btnBack;

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
        lineChart = findViewById(R.id.heartRateChart);
        btnBack = findViewById(R.id.btnBack);

        setToview();
        setBarChart(arrayList);
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