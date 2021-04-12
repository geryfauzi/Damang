package unikom.gery.damang.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getSupportActionBar().hide();
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