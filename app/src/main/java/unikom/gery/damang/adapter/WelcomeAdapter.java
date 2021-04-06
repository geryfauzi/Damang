package unikom.gery.damang.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import unikom.gery.damang.fragment.LottieOneFragment;

public class WelcomeAdapter extends FragmentStatePagerAdapter {

    public WelcomeAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            LottieOneFragment slide1 = new LottieOneFragment();
            return slide1;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
