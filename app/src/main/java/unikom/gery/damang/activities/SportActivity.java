package unikom.gery.damang.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import unikom.gery.damang.R;

public class SportActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnBack;
    private TextView btnViewAll;
    private ConstraintLayout cvNoData;

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
        setContentView(R.layout.activity_sport);

        btnBack = findViewById(R.id.btnBack);
        btnViewAll = findViewById(R.id.btnViewAll);
        cvNoData = findViewById(R.id.cvNoData);

        btnBack.setOnClickListener(this);
        btnViewAll.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (view == btnBack)
            finish();
    }
}