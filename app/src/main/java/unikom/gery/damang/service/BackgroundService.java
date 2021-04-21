package unikom.gery.damang.service;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.Serializable;

import unikom.gery.damang.GBApplication;
import unikom.gery.damang.model.ActivitySample;

public class BackgroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GBApplication.deviceService().onHeartRateTest();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Log.d("onCreate()", "After service created");
    }


    private void handleRealtimeSample(Serializable extra) {
        if (extra instanceof ActivitySample) {
            ActivitySample sample = (ActivitySample) extra;
            Toast.makeText(getApplicationContext(), sample.getHeartRate(), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), "Not found :/", Toast.LENGTH_SHORT).show();
    }
}
