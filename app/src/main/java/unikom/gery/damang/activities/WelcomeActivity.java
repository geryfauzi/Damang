package unikom.gery.damang.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;
import unikom.gery.damang.R;
import unikom.gery.damang.adapter.WelcomeAdapter;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager viewPager;
    private WelcomeAdapter welcomeAdapter;
    private CircleIndicator circleIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_welcome);
        viewPager = findViewById(R.id.pager);
        circleIndicator = findViewById(R.id.dots);
        welcomeAdapter = new WelcomeAdapter(getSupportFragmentManager());
        viewPager.setAdapter(welcomeAdapter);
        circleIndicator.setViewPager(viewPager);
        welcomeAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    @Override
    public void onClick(View view) {

    }
}