package unikom.gery.damang.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import unikom.gery.damang.R;
import unikom.gery.damang.SharedPreference;
import unikom.gery.damang.activities.ControlCenterv2;

public class LottieFiveFragment extends Fragment implements View.OnClickListener {

    SharedPreference sharedPreference;
    private Button btnMulai;

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnMulai = view.findViewById(R.id.btnMulai);
        btnMulai.setOnClickListener(this);
        sharedPreference = new SharedPreference(view.getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lottie_slide5, container, false);
    }

    @Override
    public void onClick(View view) {
        if (view == btnMulai) {
            //Set Shared Prefence First Time to False
            sharedPreference.setFirstTime(false);
            //Moving to Main Activity
            Intent intent = new Intent(view.getContext(), ControlCenterv2.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
