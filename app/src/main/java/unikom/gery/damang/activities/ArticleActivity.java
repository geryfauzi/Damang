package unikom.gery.damang.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import unikom.gery.damang.R;
import unikom.gery.damang.adapter.BeritaAdapter;
import unikom.gery.damang.api.WebService;
import unikom.gery.damang.response.Article;
import unikom.gery.damang.response.News;

public class ArticleActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rvBerita;
    private BeritaAdapter beritaAdapter;
    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Change statusbar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().hide();
        //
        setContentView(R.layout.activity_article);
        progressDialog = new ProgressDialog(ArticleActivity.this);
        progressDialog.setTitle("Sedang memuat...");
        progressDialog.setCancelable(false);
        rvBerita = findViewById(R.id.rvBerita);
        btnBack = findViewById(R.id.btnBack);
        rvBerita.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvBerita.setHasFixedSize(true);
        viewArticleData();

        btnBack.setOnClickListener(view -> {
            finish();
        });
    }

    private void viewArticleData() {
        progressDialog.show();
        WebService webService = new Retrofit.Builder().baseUrl("https://newsapi.org/v2/").addConverterFactory(GsonConverterFactory.create()).build().create(WebService.class);
        Call<News> response = webService.getArticleNewsData("id", "health", "02c679d51abf4f51b390841dab64b436");
        response.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    ArrayList<Article> articleArrayList = (ArrayList<Article>) response.body().getArticles();
                    beritaAdapter = new BeritaAdapter(articleArrayList, getApplicationContext());
                    rvBerita.setAdapter(beritaAdapter);
                    beritaAdapter.notifyDataSetChanged();
                } else {
                    Log.d("TAG", "Error : " + response.toString());
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan! Silahkan coba lagi!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat memuat berita", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }
}