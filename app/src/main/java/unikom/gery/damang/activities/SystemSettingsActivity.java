package unikom.gery.damang.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import unikom.gery.damang.R;

public class SystemSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private ConstraintLayout btnAbout, btnBackupRestore;
    private ImageView btnBack;

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
        setContentView(R.layout.activity_system_settings);
        btnBackupRestore = findViewById(R.id.btnBackupRestore);
        btnAbout = findViewById(R.id.btnAbout);
        btnBack = findViewById(R.id.btnBack);
        btnAbout.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnBackupRestore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAbout: {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                break;
            }
            case R.id.btnBack: {
                finish();
                break;
            }
            case R.id.btnBackupRestore: {
                startActivity(new Intent(getApplicationContext(), BackupRestoreActivity.class));
                break;
            }
        }
    }
}