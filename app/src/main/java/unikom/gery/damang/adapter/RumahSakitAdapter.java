package unikom.gery.damang.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

import unikom.gery.damang.R;
import unikom.gery.damang.response.Properties;

public class RumahSakitAdapter extends RecyclerView.Adapter<RumahSakitAdapter.ViewHolder> {

    private ArrayList<Properties> list;
    private Context context;

    public RumahSakitAdapter(ArrayList<Properties> list, Context context) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_hospital, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.latitude = list.get(position).getProperties().getLat();
        holder.longitude = list.get(position).getProperties().getLon();
        holder.txtNamaRumahSakit.setText(list.get(position).getProperties().getName());
        holder.txtAlamatRumahSakit.setText(list.get(position).getProperties().getAddress_line2());

        holder.itemView.setOnClickListener(view -> {
            String uri = String.format(Locale.getDefault(), "http://maps.google.com/maps?q=loc:%s,%s", holder.latitude, holder.longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (mapIntent.resolveActivity(holder.itemView.getContext().getPackageManager()) != null)
                holder.itemView.getContext().startActivity(mapIntent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNamaRumahSakit, txtAlamatRumahSakit;
        private String latitude, longitude;

        ViewHolder(View view) {
            super(view);
            txtNamaRumahSakit = view.findViewById(R.id.txtNamaRumahSakit);
            txtAlamatRumahSakit = view.findViewById(R.id.txtAlamatRumahSakit);
        }
    }
}
