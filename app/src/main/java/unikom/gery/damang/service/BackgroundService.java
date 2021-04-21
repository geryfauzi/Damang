package unikom.gery.damang.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import unikom.gery.damang.GBApplication;
import unikom.gery.damang.devices.DeviceManager;
import unikom.gery.damang.impl.GBDevice;
import unikom.gery.damang.model.ActivitySample;
import unikom.gery.damang.model.DeviceService;
import unikom.gery.damang.util.SharedPreference;

public class BackgroundService extends Service {
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (Objects.requireNonNull(action)) {
                case DeviceService.ACTION_REALTIME_SAMPLES:
                    handleRealtimeSample(intent.getSerializableExtra(DeviceService.EXTRA_REALTIME_SAMPLE));
                    break;
            }
        }
    };
    private GBDevice gbDevice;
    private DeviceManager deviceManager;
    private List<GBDevice> deviceList;
    private SharedPreference sharedPreference;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(DeviceService.ACTION_REALTIME_SAMPLES);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filterLocal);
        Toast.makeText(this, "Uji Coba 30 detik", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    private void getHeartRate() {
        deviceManager = ((GBApplication) getApplication()).getDeviceManager();
        deviceList = deviceManager.getDevices();
        if (deviceList.size() > 0) {
            gbDevice = deviceList.get(0);
            if (gbDevice.isConnected()) {
                GBApplication.deviceService().onHeartRateTest();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Log.d("onCreate()", "After service created");
    }


    private void handleRealtimeSample(Serializable extra) {
        if (extra instanceof ActivitySample) {
            ActivitySample sample = (ActivitySample) extra;
            Toast.makeText(getApplicationContext(), sample.getHeartRate(), Toast.LENGTH_SHORT).show();
        }
    }
}
