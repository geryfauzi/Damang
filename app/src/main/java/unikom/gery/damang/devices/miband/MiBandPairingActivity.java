/*  Copyright (C) 2015-2021 Andreas Shimokawa, Carsten Pfeiffer, Daniele
    Gobbetti, Taavi Eomäe

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package unikom.gery.damang.devices.miband;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import unikom.gery.damang.GBApplication;
import unikom.gery.damang.R;
import unikom.gery.damang.activities.AbstractGBActivity;
import unikom.gery.damang.activities.DiscoveryActivity;
import unikom.gery.damang.activities.HomeActivity;
import unikom.gery.damang.devices.DeviceCoordinator;
import unikom.gery.damang.impl.GBDevice;
import unikom.gery.damang.impl.GBDeviceCandidate;
import unikom.gery.damang.util.AndroidUtils;
import unikom.gery.damang.util.BondingInterface;
import unikom.gery.damang.util.BondingUtil;
import unikom.gery.damang.util.DeviceHelper;
import unikom.gery.damang.util.GB;
import unikom.gery.damang.util.Prefs;

import static unikom.gery.damang.util.BondingUtil.STATE_DEVICE_CANDIDATE;

public class MiBandPairingActivity extends AbstractGBActivity implements BondingInterface {
    private static final Logger LOG = LoggerFactory.getLogger(MiBandPairingActivity.class);

    private static final int REQ_CODE_USER_SETTINGS = 52;

    private final BroadcastReceiver pairingReceiver = BondingUtil.getPairingReceiver(this);
    private final BroadcastReceiver bondingReceiver = BondingUtil.getBondingReceiver(this);
    private TextView message;
    private boolean isPairing;
    private GBDeviceCandidate deviceCandidate;
    private ImageView btnBack;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide Action Bar
        this.getSupportActionBar().hide();
        //Change statusbar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_mi_band_pairing);

        btnBack = findViewById(R.id.btnBack);
        message = findViewById(R.id.miband_pair_message);
        this.deviceCandidate = getIntent().getParcelableExtra(DeviceCoordinator.EXTRA_DEVICE_CANDIDATE);
        if (deviceCandidate == null && savedInstanceState != null) {
            this.deviceCandidate = savedInstanceState.getParcelable(STATE_DEVICE_CANDIDATE);
        }

        if (deviceCandidate == null) {
            Toast.makeText(this, getString(R.string.message_cannot_pair_no_mac), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, DiscoveryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            return;
        }

        DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(deviceCandidate);
        GBDevice device = DeviceHelper.getInstance().toSupportedDevice(deviceCandidate);

        if (coordinator.getSupportedDeviceSpecificSettings(device) != null) { // FIXME: this will no longer be sane in the future
            SharedPreferences sharedPrefs = GBApplication.getDeviceSpecificSharedPrefs(device.getAddress());
            String authKey = sharedPrefs.getString("authkey", null);
            if (authKey == null || authKey.isEmpty()) {
                SharedPreferences.Editor editor = sharedPrefs.edit();

                String randomAuthkey = RandomStringUtils.random(16, true, true);
                editor.putString("authkey", randomAuthkey);
                editor.apply();
            }
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // already valid user info available, use that and pair
        startPairing();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_DEVICE_CANDIDATE, deviceCandidate);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        deviceCandidate = savedInstanceState.getParcelable(STATE_DEVICE_CANDIDATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // start pairing immediately when we return from the user settings
        if (requestCode == REQ_CODE_USER_SETTINGS) {
            if (!MiBandCoordinator.hasValidUserInfo()) {
                GB.toast(this, getString(R.string.miband_pairing_using_dummy_userdata), Toast.LENGTH_LONG, GB.WARN);
            }
            startPairing();
        }

        BondingUtil.handleActivityResult(this, requestCode, resultCode, data);
    }


    private void startPairing() {
        isPairing = true;
        message.setText(getString(R.string.pairing, deviceCandidate));

        if (!BondingUtil.shouldUseBonding()) {
            BondingUtil.attemptToFirstConnect(getCurrentTarget());
            return;
        }

        BondingUtil.tryBondThenComplete(this, deviceCandidate);
    }


    private void stopPairing() {
        isPairing = false;
        BondingUtil.stopBluetoothBonding(deviceCandidate.getDevice());
    }

    @Override
    public void onBondingComplete(boolean success) {
        LOG.debug("pairingFinished: " + success);
        if (!isPairing) {
            // already gone?
            return;
        } else {
            isPairing = false;
        }

        if (success) {
            //
            String macAddress = deviceCandidate.getMacAddress();
            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(macAddress);
            if (device != null && device.getBondState() == BluetoothDevice.BOND_NONE) {
                Prefs prefs = GBApplication.getPrefs();
                prefs.getPreferences().edit().putString(MiBandConst.PREF_MIBAND_ADDRESS, macAddress).apply();
            }
            Intent intent = new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public BluetoothDevice getCurrentTarget() {
        return this.deviceCandidate.getDevice();
    }

    @Override
    protected void onResume() {
        registerBroadcastReceivers();
        super.onResume();
    }

    @Override
    protected void onStart() {
        registerBroadcastReceivers();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        unregisterBroadcastReceivers();
        if (isPairing) {
            stopPairing();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        unregisterBroadcastReceivers();
        if (isPairing) {
            stopPairing();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        // WARN: Do not stop pairing or unregister receivers pause!
        // Bonding process can pause the activity and you might miss broadcasts
        super.onPause();
    }

    public void unregisterBroadcastReceivers() {
        AndroidUtils.safeUnregisterBroadcastReceiver(LocalBroadcastManager.getInstance(this), pairingReceiver);
        AndroidUtils.safeUnregisterBroadcastReceiver(this, bondingReceiver);
    }

    public void registerBroadcastReceivers() {
        LocalBroadcastManager.getInstance(this).registerReceiver(pairingReceiver, new IntentFilter(GBDevice.ACTION_DEVICE_CHANGED));
        registerReceiver(bondingReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    public Context getContext() {
        return this;
    }
}
