package unikom.gery.damang.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import unikom.gery.damang.R;
import unikom.gery.damang.activities.OtherSportDetailActivity;
import unikom.gery.damang.sqlite.table.Sport;

public class SportAdapter extends RecyclerView.Adapter<SportAdapter.ViewHolder> {
    private ArrayList<Sport> sportList;
    private Context context;

    public SportAdapter(ArrayList<Sport> arrayList, Context context) {
        this.context = context;
        this.sportList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_sport, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.id = sportList.get(position).getId();
        holder.txtDetakJantung.setText(String.valueOf(sportList.get(position).getAverage_heart_rate()));

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(sportList.get(position).getStart_time());
            String parseDate = new SimpleDateFormat("dd MMMM yyyy").format(date);
            holder.txtTanggal.setText(parseDate);
        } catch (ParseException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }

        if (sportList.get(position).getType().equals("Lainnya"))
            Glide.with(context).load(R.drawable.ic_sport_other).into(holder.imgSportType);
        else if (sportList.get(position).getType().equals("Jogging"))
            Glide.with(context).load(R.drawable.ic_sport_running).into(holder.imgSportType);
        else
            Glide.with(context).load(R.drawable.ic_sport_cardio).into(holder.imgSportType);

        holder.itemView.setOnClickListener(view -> {
            if (sportList.get(position).getType().equals("Jogging")) {
                //TODO Saat backend olahraga jogging dan frontend hasil olahraga jogging selesai
            } else {
                Intent intent = new Intent(context, OtherSportDetailActivity.class);
                intent.putExtra("id", sportList.get(position).getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sportList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewSport;
        ImageView imgSportType;
        TextView txtTanggal, txtDetakJantung;
        String id;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewSport = itemView.findViewById(R.id.cardViewSport);
            imgSportType = itemView.findViewById(R.id.imgSportType);
            txtTanggal = itemView.findViewById(R.id.txtTanggal);
            txtDetakJantung = itemView.findViewById(R.id.txtDetakJantung);
        }
    }
}
