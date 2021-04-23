package unikom.gery.damang.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import unikom.gery.damang.GBApplication;
import unikom.gery.damang.R;
import unikom.gery.damang.activities.devicesettings.DeviceSettingsActivity;
import unikom.gery.damang.devices.DeviceCoordinator;
import unikom.gery.damang.impl.GBDevice;
import unikom.gery.damang.model.BatteryState;
import unikom.gery.damang.util.DeviceHelper;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private final Context context;
    private List<GBDevice> deviceList;
    private ViewGroup parent;

    public DeviceAdapter(Context context, List<GBDevice> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final GBDevice device = deviceList.get(position);
        final DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(device);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (device.isInitialized() || device.isConnected())
                    Toast.makeText(context, "Tahan untuk memutuskan koneksi dengan perangkat", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(context, "Sedang menghubungkan dengan perangkat...", Toast.LENGTH_SHORT).show();
                    GBApplication.deviceService().connect(device);
                }
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (device.getState() != GBDevice.State.NOT_CONNECTED) {
                    Toast.makeText(context, "Memutuskan koneksi dengan perangkat...", Toast.LENGTH_SHORT).show();
                    GBApplication.deviceService().disconnect();
                }
                return true;
            }
        });

        holder.deviceName.setText(getUniqueDeviceName(device));

        if (device.isBusy())
            holder.deviceConnectionStatus.setText(device.getBusyTask());
        else
            holder.deviceConnectionStatus.setText(device.getStateString());

        //Battery Area
        short batteryLevel = device.getBatteryLevel();
        float batteryVoltage = device.getBatteryVoltage();
        BatteryState batteryState = device.getBatteryState();
        if (batteryLevel != GBDevice.BATTERY_UNKNOWN) {
            holder.deviceBatteryIcon.setVisibility(View.VISIBLE);
            holder.deviceBatteryStatus.setVisibility(View.VISIBLE);
            holder.deviceBatteryStatus.setText(device.getBatteryLevel() + "%");
            if (BatteryState.BATTERY_CHARGING.equals(batteryState) ||
                    BatteryState.BATTERY_CHARGING_FULL.equals(batteryState)) {
                holder.deviceBatteryIcon.setImageLevel(device.getBatteryLevel() + 100);
            } else {
                holder.deviceBatteryIcon.setImageLevel(device.getBatteryLevel());
            }
        } else if (BatteryState.NO_BATTERY.equals(batteryState) && batteryVoltage != GBDevice.BATTERY_UNKNOWN) {
            holder.deviceBatteryIcon.setVisibility(View.VISIBLE);
            holder.deviceBatteryStatus.setVisibility(View.VISIBLE);
            holder.deviceBatteryStatus.setText(String.format(Locale.getDefault(), "%.2f", batteryVoltage));
            holder.deviceBatteryIcon.setImageLevel(200);
        }
        //
        holder.deviceMoreOption.setVisibility(coordinator.getSupportedDeviceSpecificSettings(device) != null && !device.isBusy() ? View.VISIBLE : View.GONE);
        holder.deviceMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DeviceSettingsActivity.class);
                intent.putExtra(GBDevice.EXTRA_DEVICE, device);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    private String getUniqueDeviceName(GBDevice device) {
        String deviceName = device.getAliasOrName();

        if (!isUniqueDeviceName(device, deviceName)) {
            if (device.getModel() != null) {
                deviceName = deviceName + " " + device.getModel();
                if (!isUniqueDeviceName(device, deviceName)) {
                    deviceName = deviceName + " " + device.getShortAddress();
                }
            } else {
                deviceName = deviceName + " " + device.getShortAddress();
            }
        }
        return deviceName;
    }

    private boolean isUniqueDeviceName(GBDevice device, String deviceName) {
        for (int i = 0; i < deviceList.size(); i++) {
            GBDevice item = deviceList.get(i);
            if (item == device) {
                continue;
            }
            if (deviceName.equals(item.getName())) {
                return false;
            }
        }
        return true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView deviceName;
        ImageView deviceBatteryIcon;
        TextView deviceBatteryStatus;
        TextView deviceConnectionStatus;
        ImageView deviceMoreOption;

        ViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.cardViewDevice);
            deviceName = view.findViewById(R.id.textView14);
            deviceBatteryIcon = view.findViewById(R.id.imgDeviceBattery);
            deviceBatteryStatus = view.findViewById(R.id.txtDeviceBattery);
            deviceConnectionStatus = view.findViewById(R.id.txtDeviceConnection);
            deviceMoreOption = view.findViewById(R.id.imgDeviceMore);
        }

    }
}
