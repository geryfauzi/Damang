package unikom.gery.damang.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import unikom.gery.damang.model.ActivitySample;

public class DataReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("sample.data")) {
            if (intent.getSerializableExtra("extra") instanceof ActivitySample) {
                ActivitySample sample = (ActivitySample) intent.getSerializableExtra("extra");
                Toast.makeText(context, sample.getHeartRate(), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, "Error hiji", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(context, "Error dua", Toast.LENGTH_SHORT).show();
    }
}
