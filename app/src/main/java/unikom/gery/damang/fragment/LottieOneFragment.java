package unikom.gery.damang.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import unikom.gery.damang.R;
import unikom.gery.damang.activities.ControlCenterv2;
import unikom.gery.damang.activities.WelcomeActivity;

public class LottieOneFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((WelcomeActivity)getActivity()).updateStatusBarColor("#4FA5D2");
//        final Handler handler = new Handler();
//        Runnable r = new Runnable() {
//            public void run() {
//            startActivity(new Intent(view.getContext(), ControlCenterv2.class));
//            }
//        };
//        handler.postDelayed(r,5000);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lottie_slide1, container, false);
    }
}
