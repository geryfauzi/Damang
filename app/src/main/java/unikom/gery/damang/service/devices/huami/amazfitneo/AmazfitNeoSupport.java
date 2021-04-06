/*  Copyright (C) 2020 Andreas Shimokawa

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
package unikom.gery.damang.service.devices.huami.amazfitneo;

import android.content.Context;
import android.net.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import unikom.gery.damang.R;
import unikom.gery.damang.devices.huami.HuamiFWHelper;
import unikom.gery.damang.devices.huami.amazfitneo.AmazfitNeoFWHelper;
import unikom.gery.damang.model.NotificationSpec;
import unikom.gery.damang.service.btle.TransactionBuilder;
import unikom.gery.damang.service.devices.huami.miband5.MiBand5Support;
import unikom.gery.damang.service.devices.huami.operations.UpdateFirmwareOperation;
import unikom.gery.damang.service.devices.huami.operations.UpdateFirmwareOperation2020;

//import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitneo.AmazfitBand5FWHelper;

public class AmazfitNeoSupport extends MiBand5Support {
    private static final Logger LOG = LoggerFactory.getLogger(AmazfitNeoSupport.class);

    @Override
    public void onNotification(NotificationSpec notificationSpec) {
        super.sendNotificationNew(notificationSpec, false);
    }

    @Override
    protected AmazfitNeoSupport setDisplayItems(TransactionBuilder builder) {
        setDisplayItemsNew(builder, false, false, R.array.pref_neo_display_items_default);
        return this;
    }

    @Override
    public HuamiFWHelper createFWHelper(Uri uri, Context context) throws IOException {
        return new AmazfitNeoFWHelper(uri, context);
    }

    @Override
    public UpdateFirmwareOperation createUpdateFirmwareOperation(Uri uri) {
        return new UpdateFirmwareOperation2020(uri, this);
    }
}
