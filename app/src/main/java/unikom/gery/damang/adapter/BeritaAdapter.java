package unikom.gery.damang.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import unikom.gery.damang.R;
import unikom.gery.damang.response.Article;

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.ViewHolder> {

    private ArrayList<Article> list;
    private Context context;

    public BeritaAdapter(ArrayList<Article> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_berita, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        if (list.get(position).getUrlToImage() != null) {
            Glide.with(holder.itemView.getContext()).load(list.get(position).getUrlToImage()).into(holder.imgBerita);
        }

        holder.txtTanggalBerita.setText(dateFormat(list.get(position).getPublishedAt()));
        holder.txtJudulBerita.setText(list.get(position).getTitle());
        holder.url = list.get(position).getUrl();
        holder.cvBerita.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).getUrl()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }

    private String dateFormat(String dateNews) {
        String isDate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy - HH:mm:ss", Locale.getDefault());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(dateNews);
            isDate = simpleDateFormat.format(date);
        } catch (ParseException error) {
            error.printStackTrace();
            Toast.makeText(context, "Terjadi kesalahan saat memeuat berita", Toast.LENGTH_SHORT).show();
        }
        return isDate;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cvBerita;
        ImageView imgBerita;
        TextView txtJudulBerita, txtTanggalBerita;
        String url;

        ViewHolder(View view) {
            super(view);
            cvBerita = view.findViewById(R.id.cvBerita);
            imgBerita = view.findViewById(R.id.imgThumbnail);
            txtJudulBerita = view.findViewById(R.id.txtJudul);
            txtTanggalBerita = view.findViewById(R.id.txtTanggalBerita);
        }
    }
}
