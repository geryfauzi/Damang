package unikom.gery.damang.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import unikom.gery.damang.R;
import unikom.gery.damang.activities.DetailConditionActivity;
import unikom.gery.damang.model.HeartRate;
import unikom.gery.damang.util.SharedPreference;

public class DailyConditionAdapter extends RecyclerView.Adapter<DailyConditionAdapter.ViewHolder> {

    private ArrayList<HeartRate> arrayList;
    private Context context;
    private ViewGroup viewGroup;

    public DailyConditionAdapter(ArrayList<HeartRate> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.viewGroup = parent;
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_daily_condition, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        SharedPreference sharedPreference = new SharedPreference(context);
        try {
            int age = getCurrentAge(getTodayDate(), sharedPreference.getUser().getDateofBirth());
            String status = getCurrentCondition(age, arrayList.get(position).getAverageHeartRate());
            if (status.equals("Kesehatan anda baik")) {
                holder.cvStatus.setCardBackgroundColor(Color.parseColor("#BDF5BC"));
                holder.txtStatus.setTextColor(Color.parseColor("#19C118"));
            } else if (status.equals("Kesehatan anda kurang baik")) {
                holder.cvStatus.setCardBackgroundColor(Color.parseColor("#fbc531"));
                holder.txtStatus.setTextColor(Color.parseColor("#fbc531"));
            } else {
                holder.cvStatus.setCardBackgroundColor(Color.parseColor("#FF6364"));
                holder.txtStatus.setTextColor(Color.parseColor("#FF5959"));
            }
            holder.txtStatus.setText(status);
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(arrayList.get(position).getDate());
            String parseDate = new SimpleDateFormat("dd MMMM yyyy").format(date);
            holder.txtTanggal.setText(parseDate);
        } catch (ParseException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
        holder.txtDetakJantung.setText(arrayList.get(position).getAverageHeartRate() + " bpm");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailConditionActivity.class);
                intent.putExtra("date", arrayList.get(position).getDate());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
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

    private String getCurrentCondition(int age, int heartRate) {
        String status = "";
        if (age < 2) {
            if (heartRate >= 80 && heartRate <= 160)
                status = "Kesehatan anda baik";
            else
                status = "Kesehatan anda kurang baik";
        } else if (age >= 2 && age <= 10) {
            if (heartRate >= 70 && heartRate <= 120)
                status = "Kesehatan anda baik";
            else
                status = "Kesehatan anda kurang baik";
        } else if (age >= 11) {
            if (heartRate >= 60 && heartRate <= 100)
                status = "Kesehatan anda baik";
            else if ((heartRate >= 54 && heartRate < 60) || (heartRate > 100 && heartRate <= 110))
                status = "Kesehatan anda kurang baik";
            else
                status = "Kesehatan anda tidak baik";
        }
        return status;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cvStatus, cardView;
        TextView txtTanggal, txtStatus, txtDetakJantung;

        ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cardviewCondition);
            cvStatus = view.findViewById(R.id.cvStatus);
            txtTanggal = view.findViewById(R.id.txtTanggal);
            txtStatus = view.findViewById(R.id.txtStatusKesehatan);
            txtDetakJantung = view.findViewById(R.id.txtDetakJantung);
        }
    }
}
