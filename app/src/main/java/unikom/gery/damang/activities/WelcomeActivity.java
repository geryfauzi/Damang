package unikom.gery.damang.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import me.relex.circleindicator.CircleIndicator;
import unikom.gery.damang.R;
import unikom.gery.damang.util.SharedPreference;
import unikom.gery.damang.adapter.WelcomeAdapter;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private WelcomeAdapter welcomeAdapter;
    private CircleIndicator circleIndicator;
    private SharedPreference sharedPreference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide Action Bar
        this.getSupportActionBar().hide();
        //Change statusbar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#000000"));
        //
        setContentView(R.layout.activity_welcome);
        sharedPreference = new SharedPreference(this);
        //Checking if the it's the first time user open the app on his current device
        if (!sharedPreference.isFirstTime()) {
            //Moving to Main Activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        //Walkthrought Initiation
        viewPager = findViewById(R.id.pager);
        circleIndicator = findViewById(R.id.dots);
        welcomeAdapter = new WelcomeAdapter(getSupportFragmentManager());
        //Setting the adapter
        viewPager.setAdapter(welcomeAdapter);
        circleIndicator.setViewPager(viewPager);
        welcomeAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
    }


    @Override
    public void onClick(View view) {

    }
}