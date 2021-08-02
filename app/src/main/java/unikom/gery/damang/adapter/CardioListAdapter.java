package unikom.gery.damang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import unikom.gery.damang.R;
import unikom.gery.damang.model.Cardio;

public class CardioListAdapter extends RecyclerView.Adapter<CardioListAdapter.ViewHolder> {
    private ArrayList<Cardio> cardios;
    private Context context;

    public CardioListAdapter(Context context, ArrayList<Cardio> list) {
        this.context = context;
        this.cardios = list;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_cardio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(cardios.get(position).getAnimation()).into(holder.imgCardio);
        holder.txtCardioName.setText(cardios.get(position).getName());
        holder.txtCardioCount.setText("x" + cardios.get(position).getCount());
    }

    @Override
    public int getItemCount() {
        return cardios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCardio;
        TextView txtCardioName, txtCardioCount;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imgCardio = itemView.findViewById(R.id.imgCardio);
            txtCardioName = itemView.findViewById(R.id.txtCardioName);
            txtCardioCount = itemView.findViewById(R.id.txtCardioCount);
        }
    }
}
