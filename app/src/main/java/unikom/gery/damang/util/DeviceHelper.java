/*  Copyright (C) 2015-2021 0nse, 115ek, Andreas Böhler, Andreas Shimokawa,
    angelpup, Carsten Pfeiffer, Cre3per, DanialHanif, Daniel Dakhno, Daniele
    Gobbetti, Dmytro Bielik, Gordon Williams, Jean-François Greffier, João Paulo
    Barraca, José Rebelo, ksiwczynski, ladbsoft, Lesur Frederic, Manuel Ruß,
    maxirnilian, mkusnierz, odavo32nof, opavlov, pangwalla, Pavel Elagin,
    protomors, Quallenauge, Sami Alaoui, Sebastian Kranz, Sophanimus, Taavi
    Eomäe, tiparega, Vadim Kaushan, Yukai Li

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
package unikom.gery.damang.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import unikom.gery.damang.GBApplication;
import unikom.gery.damang.GBException;
import unikom.gery.damang.R;
import unikom.gery.damang.database.DBHandler;
import unikom.gery.damang.database.DBHelper;
import unikom.gery.damang.devices.DeviceCoordinator;
import unikom.gery.damang.devices.UnknownDeviceCoordinator;
import unikom.gery.damang.devices.banglejs.BangleJSCoordinator;
import unikom.gery.damang.devices.casio.gb6900.CasioGB6900DeviceCoordinator;
import unikom.gery.damang.devices.casio.gbx100.CasioGBX100DeviceCoordinator;
import unikom.gery.damang.devices.hplus.EXRIZUK8Coordinator;
import unikom.gery.damang.devices.hplus.HPlusCoordinator;
import unikom.gery.damang.devices.hplus.MakibesF68Coordinator;
import unikom.gery.damang.devices.hplus.Q8Coordinator;
import unikom.gery.damang.devices.hplus.SG2Coordinator;
import unikom.gery.damang.devices.huami.amazfitband5.AmazfitBand5Coordinator;
import unikom.gery.damang.devices.huami.amazfitbips.AmazfitBipSLiteCoordinator;
import unikom.gery.damang.devices.huami.amazfitgts2.AmazfitGTS2MiniCoordinator;
import unikom.gery.damang.devices.huami.amazfitgts2.AmazfitGTS2eCoordinator;
import unikom.gery.damang.devices.huami.amazfitneo.AmazfitNeoCoordinator;
import unikom.gery.damang.devices.huami.amazfitbip.AmazfitBipCoordinator;
import unikom.gery.damang.devices.huami.amazfitbip.AmazfitBipLiteCoordinator;
import unikom.gery.damang.devices.huami.amazfitbips.AmazfitBipSCoordinator;
import unikom.gery.damang.devices.huami.amazfitbipu.AmazfitBipUCoordinator;
import unikom.gery.damang.devices.huami.amazfitbipupro.AmazfitBipUProCoordinator;
import unikom.gery.damang.devices.huami.amazfitcor.AmazfitCorCoordinator;
import unikom.gery.damang.devices.huami.amazfitcor2.AmazfitCor2Coordinator;
import unikom.gery.damang.devices.huami.amazfitgtr.AmazfitGTRCoordinator;
import unikom.gery.damang.devices.huami.amazfitgtr.AmazfitGTRLiteCoordinator;
import unikom.gery.damang.devices.huami.amazfitgtr2.AmazfitGTR2Coordinator;
import unikom.gery.damang.devices.huami.amazfitx.AmazfitXCoordinator;
import unikom.gery.damang.devices.huami.zeppe.ZeppECoordinator;
import unikom.gery.damang.devices.huami.amazfitgtr2.AmazfitGTR2eCoordinator;
import unikom.gery.damang.devices.huami.amazfitgts.AmazfitGTSCoordinator;
import unikom.gery.damang.devices.huami.amazfitgts2.AmazfitGTS2Coordinator;
import unikom.gery.damang.devices.huami.amazfitvergel.AmazfitVergeLCoordinator;
import unikom.gery.damang.devices.huami.amazfittrex.AmazfitTRexCoordinator;
import unikom.gery.damang.devices.huami.miband2.MiBand2Coordinator;
import unikom.gery.damang.devices.huami.miband2.MiBand2HRXCoordinator;
import unikom.gery.damang.devices.huami.miband3.MiBand3Coordinator;
import unikom.gery.damang.devices.huami.miband4.MiBand4Coordinator;
import unikom.gery.damang.devices.huami.miband5.MiBand5Coordinator;
import unikom.gery.damang.devices.id115.ID115Coordinator;
import unikom.gery.damang.devices.itag.ITagCoordinator;
import unikom.gery.damang.devices.jyou.BFH16DeviceCoordinator;
import unikom.gery.damang.devices.jyou.TeclastH30.TeclastH30Coordinator;
import unikom.gery.damang.devices.jyou.y5.Y5Coordinator;
import unikom.gery.damang.devices.lefun.LefunDeviceCoordinator;
import unikom.gery.damang.devices.lenovo.watchxplus.WatchXPlusDeviceCoordinator;
import unikom.gery.damang.devices.liveview.LiveviewCoordinator;
import unikom.gery.damang.devices.makibeshr3.MakibesHR3Coordinator;
import unikom.gery.damang.devices.miband.MiBandConst;
import unikom.gery.damang.devices.miband.MiBandCoordinator;
import unikom.gery.damang.devices.mijia_lywsd02.MijiaLywsd02Coordinator;
import unikom.gery.damang.devices.miscale2.MiScale2DeviceCoordinator;
import unikom.gery.damang.devices.no1f1.No1F1Coordinator;
import unikom.gery.damang.devices.nut.NutCoordinator;
import unikom.gery.damang.devices.pebble.PebbleCoordinator;
import unikom.gery.damang.devices.pinetime.PineTimeJFCoordinator;
import unikom.gery.damang.devices.qhybrid.QHybridCoordinator;
import unikom.gery.damang.devices.roidmi.Roidmi1Coordinator;
import unikom.gery.damang.devices.roidmi.Roidmi3Coordinator;
import unikom.gery.damang.devices.sonyswr12.SonySWR12DeviceCoordinator;
import unikom.gery.damang.devices.tlw64.TLW64Coordinator;
import unikom.gery.damang.devices.um25.Coordinator.UM25Coordinator;
import unikom.gery.damang.devices.vibratissimo.VibratissimoCoordinator;
import unikom.gery.damang.devices.waspos.WaspOSCoordinator;
import unikom.gery.damang.devices.watch9.Watch9DeviceCoordinator;
import unikom.gery.damang.devices.xwatch.XWatchCoordinator;
import unikom.gery.damang.devices.zetime.ZeTimeCoordinator;
import unikom.gery.damang.entities.Device;
import unikom.gery.damang.entities.DeviceAttributes;
import unikom.gery.damang.impl.GBDevice;
import unikom.gery.damang.impl.GBDeviceCandidate;
import unikom.gery.damang.model.DeviceType;

public class DeviceHelper {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceHelper.class);

    private static final DeviceHelper instance = new DeviceHelper();
    // lazily created
    private List<DeviceCoordinator> coordinators;

    public static DeviceHelper getInstance() {
        return instance;
    }

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        for (DeviceCoordinator coordinator : getAllCoordinators()) {
            DeviceType deviceType = coordinator.getSupportedType(candidate);
            if (deviceType.isSupported()) {
                return deviceType;
            }
        }
        return DeviceType.UNKNOWN;
    }

    public boolean getSupportedType(GBDevice device) {
        for (DeviceCoordinator coordinator : getAllCoordinators()) {
            if (coordinator.supports(device)) {
                return true;
            }
        }
        return false;
    }

    public GBDevice findAvailableDevice(String deviceAddress, Context context) {
        Set<GBDevice> availableDevices = getAvailableDevices(context);
        for (GBDevice availableDevice : availableDevices) {
            if (deviceAddress.equals(availableDevice.getAddress())) {
                return availableDevice;
            }
        }
        return null;
    }

    /**
     * Returns the list of all available devices that are supported by Gadgetbridge.
     * Note that no state is known about the returned devices. Even if one of those
     * devices is connected, it will report the default not-connected state.
     *
     * Clients interested in the "live" devices being managed should use the class
     * DeviceManager.
     * @param context
     * @return
     */
    public Set<GBDevice> getAvailableDevices(Context context) {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            GB.toast(context, context.getString(R.string.bluetooth_is_not_supported_), Toast.LENGTH_SHORT, GB.WARN);
        } else if (!btAdapter.isEnabled()) {
            GB.toast(context, context.getString(R.string.bluetooth_is_disabled_), Toast.LENGTH_SHORT, GB.WARN);
        }

        Set<GBDevice> availableDevices = new LinkedHashSet<>(getDatabaseDevices());
        Prefs prefs = GBApplication.getPrefs();
        String miAddress = prefs.getString(MiBandConst.PREF_MIBAND_ADDRESS, "");
        if (miAddress.length() > 0) {
            GBDevice miDevice = new GBDevice(miAddress, "MI", null, DeviceType.MIBAND);
            availableDevices.add(miDevice);
        }

        String pebbleEmuAddr = prefs.getString("pebble_emu_addr", "");
        String pebbleEmuPort = prefs.getString("pebble_emu_port", "");
        if (pebbleEmuAddr.length() >= 7 && pebbleEmuPort.length() > 0) {
            GBDevice pebbleEmuDevice = new GBDevice(pebbleEmuAddr + ":" + pebbleEmuPort, "Pebble qemu", "", DeviceType.PEBBLE);
            availableDevices.add(pebbleEmuDevice);
        }
        return availableDevices;
    }

    public GBDevice toSupportedDevice(BluetoothDevice device) {
        GBDeviceCandidate candidate = new GBDeviceCandidate(device, GBDevice.RSSI_UNKNOWN, device.getUuids());
        return toSupportedDevice(candidate);
    }

    public GBDevice toSupportedDevice(GBDeviceCandidate candidate) {
        for (DeviceCoordinator coordinator : getAllCoordinators()) {
            if (coordinator.supports(candidate)) {
                return coordinator.createDevice(candidate);
            }
        }
        return null;
    }

    public DeviceCoordinator getCoordinator(GBDeviceCandidate device) {
        synchronized (this) {
            for (DeviceCoordinator coord : getAllCoordinators()) {
                if (coord.supports(device)) {
                    return coord;
                }
            }
        }
        return new UnknownDeviceCoordinator();
    }

    public DeviceCoordinator getCoordinator(GBDevice device) {
        synchronized (this) {
            for (DeviceCoordinator coord : getAllCoordinators()) {
                if (coord.supports(device)) {
                    return coord;
                }
            }
        }
        return new UnknownDeviceCoordinator();
    }

    public synchronized List<DeviceCoordinator> getAllCoordinators() {
        if (coordinators == null) {
            coordinators = createCoordinators();
        }
        return coordinators;
    }

    private List<DeviceCoordinator> createCoordinators() {
        List<DeviceCoordinator> result = new ArrayList<>();
        result.add(new MiScale2DeviceCoordinator());
        result.add(new AmazfitXCoordinator());
        result.add(new AmazfitBipCoordinator());
        result.add(new AmazfitBipLiteCoordinator());
        result.add(new AmazfitCorCoordinator());
        result.add(new AmazfitCor2Coordinator());
        result.add(new AmazfitGTRCoordinator());
        result.add(new AmazfitGTRLiteCoordinator());
        result.add(new AmazfitGTR2Coordinator());
        result.add(new ZeppECoordinator());
        result.add(new AmazfitGTR2eCoordinator());
        result.add(new AmazfitTRexCoordinator());
        result.add(new AmazfitGTSCoordinator());
        result.add(new AmazfitGTS2Coordinator());
        result.add(new AmazfitGTS2eCoordinator());
        result.add(new AmazfitGTS2MiniCoordinator());
        result.add(new AmazfitVergeLCoordinator());
        result.add(new AmazfitBipSCoordinator());
        result.add(new AmazfitBipSLiteCoordinator());
        result.add(new AmazfitBipUCoordinator());
        result.add(new AmazfitBipUProCoordinator());
        result.add(new AmazfitBand5Coordinator());
        result.add(new AmazfitNeoCoordinator());
        result.add(new MiBand3Coordinator());
        result.add(new MiBand4Coordinator());
        result.add(new MiBand5Coordinator());
        result.add(new MiBand2HRXCoordinator());
        result.add(new MiBand2Coordinator()); // Note: MiBand2 and all of the above  must come before MiBand because detection is hacky, atm
        result.add(new MiBandCoordinator());
        result.add(new PebbleCoordinator());
        result.add(new VibratissimoCoordinator());
        result.add(new LiveviewCoordinator());
        result.add(new HPlusCoordinator());
        result.add(new No1F1Coordinator());
        result.add(new MakibesF68Coordinator());
        result.add(new Q8Coordinator());
        result.add(new EXRIZUK8Coordinator());
        result.add(new TeclastH30Coordinator());
        result.add(new XWatchCoordinator());
        result.add(new QHybridCoordinator());
        result.add(new ZeTimeCoordinator());
        result.add(new ID115Coordinator());
        result.add(new Watch9DeviceCoordinator());
        result.add(new WatchXPlusDeviceCoordinator());
        result.add(new Roidmi1Coordinator());
        result.add(new Roidmi3Coordinator());
        result.add(new Y5Coordinator());
        result.add(new CasioGB6900DeviceCoordinator());
        result.add(new CasioGBX100DeviceCoordinator());
        result.add(new BFH16DeviceCoordinator());
        result.add(new MijiaLywsd02Coordinator());
        result.add(new ITagCoordinator());
        result.add(new NutCoordinator());
        result.add(new MakibesHR3Coordinator());
        result.add(new BangleJSCoordinator());
        result.add(new TLW64Coordinator());
        result.add(new PineTimeJFCoordinator());
        result.add(new SG2Coordinator());
        result.add(new LefunDeviceCoordinator());
        result.add(new SonySWR12DeviceCoordinator());
        result.add(new WaspOSCoordinator());
        result.add(new UM25Coordinator());

        return result;
    }

    private List<GBDevice> getDatabaseDevices() {
        List<GBDevice> result = new ArrayList<>();
        try (DBHandler lockHandler = GBApplication.acquireDB()) {
            List<Device> activeDevices = DBHelper.getActiveDevices(lockHandler.getDaoSession());
            for (Device dbDevice : activeDevices) {
                GBDevice gbDevice = toGBDevice(dbDevice);
                if (gbDevice != null && DeviceHelper.getInstance().getSupportedType(gbDevice)) {
                    result.add(gbDevice);
                }
            }
            return result;

        } catch (Exception e) {
            GB.toast(GBApplication.getContext().getString(R.string.error_retrieving_devices_database), Toast.LENGTH_SHORT, GB.ERROR, e);
            return Collections.emptyList();
        }
    }

    /**
     * Converts a known device from the database to a GBDevice.
     * Note: The device might not be supported anymore, so callers should verify that.
     * @param dbDevice
     * @return
     */
    public GBDevice toGBDevice(Device dbDevice) {
        DeviceType deviceType = DeviceType.fromKey(dbDevice.getType());
        GBDevice gbDevice = new GBDevice(dbDevice.getIdentifier(), dbDevice.getName(), dbDevice.getAlias(), deviceType);
        List<DeviceAttributes> deviceAttributesList = dbDevice.getDeviceAttributesList();
        if (deviceAttributesList.size() > 0) {
            gbDevice.setModel(dbDevice.getModel());
            DeviceAttributes attrs = deviceAttributesList.get(0);
            gbDevice.setFirmwareVersion(attrs.getFirmwareVersion1());
            gbDevice.setFirmwareVersion2(attrs.getFirmwareVersion2());
            gbDevice.setVolatileAddress(attrs.getVolatileIdentifier());
        }

        return gbDevice;
    }

    /**
     * Attempts to removing the bonding with the given device. Returns true
     * if bonding was supposedly successful and false if anything went wrong
     * @param device
     * @return
     */
    public boolean removeBond(GBDevice device) throws GBException {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            BluetoothDevice remoteDevice = defaultAdapter.getRemoteDevice(device.getAddress());
            if (remoteDevice != null) {
                try {
                    Method method = BluetoothDevice.class.getMethod("removeBond", (Class[]) null);
                    Object result = method.invoke(remoteDevice, (Object[]) null);
                    return Boolean.TRUE.equals(result);
                } catch (Exception e) {
                    throw new GBException("Error removing bond to device: " + device, e);
                }
            }
        }
        return false;
    }

}
