package unikom.gery.damang.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

import unikom.gery.damang.R;
import unikom.gery.damang.adapter.CardioListAdapter;
import unikom.gery.damang.data.CardioList;
import unikom.gery.damang.model.Cardio;

public class CardioMenuActivity extends AppCompatActivity implements View.OnClickListener {

    String level;
    Button btnMulai;
    RecyclerView recyclerView;
    ImageView btnBack;
    CardioListAdapter cardioListAdapter;
    ArrayList<Cardio> arrayList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Change statusbar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //
        getSupportActionBar().hide();
        setContentView(R.layout.activity_cardio_menu);

        btnBack = findViewById(R.id.btnBack);
        level = getIntent().getStringExtra("level");
        btnMulai = findViewById(R.id.btnMulai);
        recyclerView = findViewById(R.id.rvCardio);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setNestedScrollingEnabled(false);
        setView(level);

        btnMulai.setOnClickListener(this);
        btnBack.setOnClickListener(this);

    }

    private void setView(String level) {
        arrayList.addAll(CardioList.getCardioList(level));
        cardioListAdapter = new CardioListAdapter(getApplicationContext(), arrayList);
        recyclerView.setAdapter(cardioListAdapter);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), SportActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == btnBack) {
            startActivity(new Intent(getApplicationContext(), SportActivity.class));
            finish();
        } else if (view == btnMulai) {
            Intent intent = new Intent(getApplicationContext(), CardioSportActivity.class);
            intent.putExtra("list", arrayList);
            startActivity(intent);
            finish();
        }
    }
}