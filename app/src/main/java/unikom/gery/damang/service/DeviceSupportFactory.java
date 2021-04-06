/*  Copyright (C) 2015-2021 0nse, 115ek, Andreas Böhler, Andreas Shimokawa,
    angelpup, Carsten Pfeiffer, Cre3per, criogenic, DanialHanif, Daniel Dakhno,
    Daniele Gobbetti, Dmytro Bielik, Gordon Williams, Jean-François Greffier,
    João Paulo Barraca, José Rebelo, ladbsoft, Manuel Ruß, maxirnilian,
    mkusnierz, odavo32nof, opavlov, pangwalla, Pavel Elagin, protomors,
    Quallenauge, Sami Alaoui, Sebastian Kranz, Sergey Trofimov, Sophanimus,
    Taavi Eomäe, tiparega, Vadim Kaushan, Yukai Li

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
package unikom.gery.damang.service;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.util.EnumSet;

import unikom.gery.damang.GBException;
import unikom.gery.damang.R;
import unikom.gery.damang.impl.GBDevice;
import unikom.gery.damang.model.DeviceType;
import unikom.gery.damang.service.devices.banglejs.BangleJSDeviceSupport;
import unikom.gery.damang.service.devices.casio.CasioGB6900DeviceSupport;
import unikom.gery.damang.service.devices.casio.CasioGBX100DeviceSupport;
import unikom.gery.damang.service.devices.hplus.HPlusSupport;
import unikom.gery.damang.service.devices.huami.HuamiSupport;
import unikom.gery.damang.service.devices.huami.amazfitband5.AmazfitBand5Support;
import unikom.gery.damang.service.devices.huami.amazfitbip.AmazfitBipLiteSupport;
import unikom.gery.damang.service.devices.huami.amazfitbip.AmazfitBipSupport;
import unikom.gery.damang.service.devices.huami.amazfitbips.AmazfitBipSLiteSupport;
import unikom.gery.damang.service.devices.huami.amazfitbips.AmazfitBipSSupport;
import unikom.gery.damang.service.devices.huami.amazfitbipu.AmazfitBipUSupport;
import unikom.gery.damang.service.devices.huami.amazfitbipupro.AmazfitBipUProSupport;
import unikom.gery.damang.service.devices.huami.amazfitcor.AmazfitCorSupport;
import unikom.gery.damang.service.devices.huami.amazfitcor2.AmazfitCor2Support;
import unikom.gery.damang.service.devices.huami.amazfitgtr.AmazfitGTRLiteSupport;
import unikom.gery.damang.service.devices.huami.amazfitgtr.AmazfitGTRSupport;
import unikom.gery.damang.service.devices.huami.amazfitgtr2.AmazfitGTR2Support;
import unikom.gery.damang.service.devices.huami.amazfitgtr2.AmazfitGTR2eSupport;
import unikom.gery.damang.service.devices.huami.amazfitgts.AmazfitGTSSupport;
import unikom.gery.damang.service.devices.huami.amazfitgts2.AmazfitGTS2MiniSupport;
import unikom.gery.damang.service.devices.huami.amazfitgts2.AmazfitGTS2Support;
import unikom.gery.damang.service.devices.huami.amazfitgts2.AmazfitGTS2eSupport;
import unikom.gery.damang.service.devices.huami.amazfitneo.AmazfitNeoSupport;
import unikom.gery.damang.service.devices.huami.amazfittrex.AmazfitTRexSupport;
import unikom.gery.damang.service.devices.huami.amazfitvergel.AmazfitVergeLSupport;
import unikom.gery.damang.service.devices.huami.amazfitx.AmazfitXSupport;
import unikom.gery.damang.service.devices.huami.miband3.MiBand3Support;
import unikom.gery.damang.service.devices.huami.miband4.MiBand4Support;
import unikom.gery.damang.service.devices.huami.miband5.MiBand5Support;
import unikom.gery.damang.service.devices.huami.zeppe.ZeppESupport;
import unikom.gery.damang.service.devices.id115.ID115Support;
import unikom.gery.damang.service.devices.itag.ITagSupport;
import unikom.gery.damang.service.devices.jyou.BFH16DeviceSupport;
import unikom.gery.damang.service.devices.jyou.TeclastH30.TeclastH30Support;
import unikom.gery.damang.service.devices.jyou.y5.Y5Support;
import unikom.gery.damang.service.devices.lefun.LefunDeviceSupport;
import unikom.gery.damang.service.devices.lenovo.watchxplus.WatchXPlusDeviceSupport;
import unikom.gery.damang.service.devices.liveview.LiveviewSupport;
import unikom.gery.damang.service.devices.makibeshr3.MakibesHR3DeviceSupport;
import unikom.gery.damang.service.devices.miband.MiBandSupport;
import unikom.gery.damang.service.devices.mijia_lywsd02.MijiaLywsd02Support;
import unikom.gery.damang.service.devices.miscale2.MiScale2DeviceSupport;
import unikom.gery.damang.service.devices.no1f1.No1F1Support;
import unikom.gery.damang.service.devices.nut.NutSupport;
import unikom.gery.damang.service.devices.pebble.PebbleSupport;
import unikom.gery.damang.service.devices.pinetime.PineTimeJFSupport;
import unikom.gery.damang.service.devices.qhybrid.QHybridSupport;
import unikom.gery.damang.service.devices.roidmi.RoidmiSupport;
import unikom.gery.damang.service.devices.sonyswr12.SonySWR12DeviceSupport;
import unikom.gery.damang.service.devices.tlw64.TLW64Support;
import unikom.gery.damang.service.devices.um25.Support.UM25Support;
import unikom.gery.damang.service.devices.vibratissimo.VibratissimoSupport;
import unikom.gery.damang.service.devices.waspos.WaspOSDeviceSupport;
import unikom.gery.damang.service.devices.watch9.Watch9DeviceSupport;
import unikom.gery.damang.service.devices.xwatch.XWatchSupport;
import unikom.gery.damang.service.devices.zetime.ZeTimeDeviceSupport;
import unikom.gery.damang.util.GB;

public class DeviceSupportFactory {
    private final BluetoothAdapter mBtAdapter;
    private final Context mContext;

    DeviceSupportFactory(Context context) {
        mContext = context;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public synchronized DeviceSupport createDeviceSupport(GBDevice device) throws GBException {
        DeviceSupport deviceSupport;
        String deviceAddress = device.getAddress();
        int indexFirstColon = deviceAddress.indexOf(":");
        if (indexFirstColon > 0) {
            if (indexFirstColon == deviceAddress.lastIndexOf(":")) { // only one colon
                deviceSupport = createTCPDeviceSupport(device);
            } else {
                // multiple colons -- bt?
                deviceSupport = createBTDeviceSupport(device);
            }
        } else {
            // no colon at all, maybe a class name?
            deviceSupport = createClassNameDeviceSupport(device);
        }

        if (deviceSupport != null) {
            return deviceSupport;
        }

        // no device found, check transport availability and warn
        checkBtAvailability();
        return null;
    }

    private DeviceSupport createClassNameDeviceSupport(GBDevice device) throws GBException {
        String className = device.getAddress();
        try {
            Class<?> deviceSupportClass = Class.forName(className);
            Constructor<?> constructor = deviceSupportClass.getConstructor();
            DeviceSupport support = (DeviceSupport) constructor.newInstance();
            // has to create the device itself
            support.setContext(device, null, mContext);
            return support;
        } catch (ClassNotFoundException e) {
            return null; // not a class, or not known at least
        } catch (Exception e) {
            throw new GBException("Error creating DeviceSupport instance for " + className, e);
        }
    }

    private void checkBtAvailability() {
        if (mBtAdapter == null) {
            GB.toast(mContext.getString(R.string.bluetooth_is_not_supported_), Toast.LENGTH_SHORT, GB.WARN);
        } else if (!mBtAdapter.isEnabled()) {
            GB.toast(mContext.getString(R.string.bluetooth_is_disabled_), Toast.LENGTH_SHORT, GB.WARN);
        }
    }

    private DeviceSupport createBTDeviceSupport(GBDevice gbDevice) throws GBException {
        if (mBtAdapter != null && mBtAdapter.isEnabled()) {
            DeviceSupport deviceSupport = null;

            try {
                switch (gbDevice.getType()) {
                    case PEBBLE:
                        deviceSupport = new ServiceDeviceSupport(new PebbleSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case MIBAND:
                        deviceSupport = new ServiceDeviceSupport(new MiBandSupport(), EnumSet.of(ServiceDeviceSupport.Flags.THROTTLING, ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case MIBAND2:
                        deviceSupport = new ServiceDeviceSupport(new HuamiSupport(), EnumSet.of(ServiceDeviceSupport.Flags.THROTTLING, ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case MIBAND3:
                        deviceSupport = new ServiceDeviceSupport(new MiBand3Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case MIBAND4:
                        deviceSupport = new ServiceDeviceSupport(new MiBand4Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case MIBAND5:
                        deviceSupport = new ServiceDeviceSupport(new MiBand5Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITBIP:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitBipSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITBIP_LITE:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitBipLiteSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITBIPS:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitBipSSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITBIPS_LITE:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitBipSLiteSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITBIPU:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitBipUSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITBIPUPRO:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitBipUProSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITGTR:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitGTRSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITGTR_LITE:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitGTRLiteSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITGTR2:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitGTR2Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case ZEPP_E:
                        deviceSupport = new ServiceDeviceSupport(new ZeppESupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITGTR2E:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitGTR2eSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITTREX:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitTRexSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITGTS:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitGTSSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITVERGEL:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitVergeLSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITGTS2:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitGTS2Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITGTS2_MINI:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitGTS2MiniSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITGTS2E:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitGTS2eSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITCOR:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitCorSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITCOR2:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitCor2Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITBAND5:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitBand5Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITX:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitXSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case AMAZFITNEO:
                        deviceSupport = new ServiceDeviceSupport(new AmazfitNeoSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case VIBRATISSIMO:
                        deviceSupport = new ServiceDeviceSupport(new VibratissimoSupport(), EnumSet.of(ServiceDeviceSupport.Flags.THROTTLING, ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case LIVEVIEW:
                        deviceSupport = new ServiceDeviceSupport(new LiveviewSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case HPLUS:
                        deviceSupport = new ServiceDeviceSupport(new HPlusSupport(DeviceType.HPLUS), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case MAKIBESF68:
                        deviceSupport = new ServiceDeviceSupport(new HPlusSupport(DeviceType.MAKIBESF68), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case EXRIZUK8:
                        deviceSupport = new ServiceDeviceSupport(new HPlusSupport(DeviceType.EXRIZUK8), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case Q8:
                        deviceSupport = new ServiceDeviceSupport(new HPlusSupport(DeviceType.Q8), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case NO1F1:
                        deviceSupport = new ServiceDeviceSupport(new No1F1Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case TECLASTH30:
                        deviceSupport = new ServiceDeviceSupport(new TeclastH30Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case XWATCH:
                        deviceSupport = new ServiceDeviceSupport(new XWatchSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case FOSSILQHYBRID:
                        deviceSupport = new ServiceDeviceSupport(new QHybridSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case ZETIME:
                        deviceSupport = new ServiceDeviceSupport(new ZeTimeDeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case ID115:
                        deviceSupport = new ServiceDeviceSupport(new ID115Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case WATCH9:
                        deviceSupport = new ServiceDeviceSupport(new Watch9DeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.THROTTLING, ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case WATCHXPLUS:
                        deviceSupport = new ServiceDeviceSupport(new WatchXPlusDeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.THROTTLING, ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case ROIDMI:
                        deviceSupport = new ServiceDeviceSupport(new RoidmiSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case ROIDMI3:
                        deviceSupport = new ServiceDeviceSupport(new RoidmiSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case Y5:
                        deviceSupport = new ServiceDeviceSupport(new Y5Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case CASIOGB6900:
                        deviceSupport = new ServiceDeviceSupport(new CasioGB6900DeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case CASIOGBX100:
                        deviceSupport = new ServiceDeviceSupport(new CasioGBX100DeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case MISCALE2:
                        deviceSupport = new ServiceDeviceSupport(new MiScale2DeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case BFH16:
                        deviceSupport = new ServiceDeviceSupport(new BFH16DeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case MIJIA_LYWSD02:
                        deviceSupport = new ServiceDeviceSupport(new MijiaLywsd02Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case MAKIBESHR3:
                        deviceSupport = new ServiceDeviceSupport(new MakibesHR3DeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case ITAG:
                        deviceSupport = new ServiceDeviceSupport(new ITagSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case NUTMINI:
                        deviceSupport = new ServiceDeviceSupport(new NutSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case BANGLEJS:
                        deviceSupport = new ServiceDeviceSupport(new BangleJSDeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case TLW64:
                        deviceSupport = new ServiceDeviceSupport(new TLW64Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case PINETIME_JF:
                        deviceSupport = new ServiceDeviceSupport(new PineTimeJFSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case SG2:
                        deviceSupport = new ServiceDeviceSupport(new HPlusSupport(DeviceType.SG2), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case LEFUN:
                        deviceSupport = new ServiceDeviceSupport(new LefunDeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case SONY_SWR12:
                        deviceSupport = new ServiceDeviceSupport(new SonySWR12DeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case WASPOS:
                        deviceSupport = new ServiceDeviceSupport(new WaspOSDeviceSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                    case UM25:
                        deviceSupport = new ServiceDeviceSupport(new UM25Support(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
                        break;
                }
                if (deviceSupport != null) {
                    deviceSupport.setContext(gbDevice, mBtAdapter, mContext);
                    return deviceSupport;
                }
            } catch (Exception e) {
                throw new GBException(mContext.getString(R.string.cannot_connect_bt_address_invalid_), e);
            }
        }
        return null;
    }

    private DeviceSupport createTCPDeviceSupport(GBDevice gbDevice) throws GBException {
        try {
            DeviceSupport deviceSupport = new ServiceDeviceSupport(new PebbleSupport(), EnumSet.of(ServiceDeviceSupport.Flags.BUSY_CHECKING));
            deviceSupport.setContext(gbDevice, mBtAdapter, mContext);
            return deviceSupport;
        } catch (Exception e) {
            throw new GBException("cannot connect to " + gbDevice, e); // FIXME: localize
        }
    }

}
