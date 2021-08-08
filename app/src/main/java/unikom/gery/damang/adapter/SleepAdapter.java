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
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import unikom.gery.damang.R;
import unikom.gery.damang.activities.DetailSleepActivity;
import unikom.gery.damang.sqlite.table.Sleep;

public class SleepAdapter extends RecyclerView.Adapter<SleepAdapter.ViewHolder> {

    private ArrayList<Sleep> list;
    private Context context;

    public SleepAdapter(ArrayList<Sleep> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_sleep, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        int hour = list.get(position).getDuration() / 60;
        if (hour > 0)
            holder.txtDurasiTidur.setText(hour + " Jam Durasi Tidur");
        else
            holder.txtDurasiTidur.setText(list.get(position).getDuration() + " Menit Durasi Tidur");

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(list.get(position).getStart_time());
            String parseDate = new SimpleDateFormat("dd MMMM yyyy").format(date);
            holder.txtTanggalTidur.setText(parseDate);
        } catch (ParseException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }

        holder.txtSkorTidur.setText(String.valueOf(list.get(position).getStatus()));
        int skor = Integer.parseInt(list.get(position).getStatus());
        if (skor >= 90) {
            holder.txtKeteranganSkor.setText("Sangat Baik");
            holder.txtKeteranganSkor.setTextColor(Color.parseColor("#19C118"));
        } else if (skor >= 80) {
            holder.txtKeteranganSkor.setText("Baik");
            holder.txtKeteranganSkor.setTextColor(Color.parseColor("#5C95C4"));
        } else if (skor >= 70) {
            holder.txtKeteranganSkor.setText("Cukup");
            holder.txtKeteranganSkor.setTextColor(Color.parseColor("#fbc531"));
        } else if (skor >= 60) {
            holder.txtKeteranganSkor.setText("Kurang");
            holder.txtKeteranganSkor.setTextColor(Color.parseColor("#FF5959"));
        } else if (skor < 60) {
            holder.txtKeteranganSkor.setText("Sangat Kurang");
            holder.txtKeteranganSkor.setTextColor(Color.parseColor("#FF5959"));
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailSleepActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("id", list.get(position).getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTanggalTidur, txtDurasiTidur, txtSkorTidur, txtKeteranganSkor;

        ViewHolder(View view) {
            super(view);
            txtDurasiTidur = view.findViewById(R.id.txtDurasiTidur);
            txtKeteranganSkor = view.findViewById(R.id.txtKeteranganSkor);
            txtTanggalTidur = view.findViewById(R.id.txtTanggalTidur);
            txtSkorTidur = view.findViewById(R.id.txtSkorTidur);
        }
    }
}
