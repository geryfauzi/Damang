package unikom.gery.damang.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import unikom.gery.damang.fragment.LottieFiveFragment;
import unikom.gery.damang.fragment.LottieFourFragment;
import unikom.gery.damang.fragment.LottieOneFragment;
import unikom.gery.damang.fragment.LottieThreeFragment;
import unikom.gery.damang.fragment.LottieTwoFragment;

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
        } else if (position == 1) {
            LottieTwoFragment slide2 = new LottieTwoFragment();
            return slide2;
        } else if (position == 2) {
            LottieThreeFragment slide3 = new LottieThreeFragment();
            return slide3;
        } else if (position == 3) {
            LottieFourFragment slide4 = new LottieFourFragment();
            return slide4;
        } else if (position == 4) {
            LottieFiveFragment slide5 = new LottieFiveFragment();
            return slide5;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
