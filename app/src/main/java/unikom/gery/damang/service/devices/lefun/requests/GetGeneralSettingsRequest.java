/*  Copyright (C) 2020-2021 Yukai Li

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
package unikom.gery.damang.service.devices.lefun.requests;

import unikom.gery.damang.devices.lefun.LefunConstants;
import unikom.gery.damang.devices.lefun.commands.BaseCommand;
import unikom.gery.damang.devices.lefun.commands.SettingsCommand;
import unikom.gery.damang.service.btle.TransactionBuilder;
import unikom.gery.damang.service.devices.lefun.LefunDeviceSupport;
import unikom.gery.damang.service.devices.miband.operations.OperationStatus;

public class GetGeneralSettingsRequest extends Request {
    public GetGeneralSettingsRequest(LefunDeviceSupport support, TransactionBuilder builder) {
        super(support, builder);
    }

    @Override
    public byte[] createRequest() {
        SettingsCommand cmd = new SettingsCommand();

        cmd.setOp(BaseCommand.OP_GET);

        return cmd.serialize();
    }

    @Override
    public void handleResponse(byte[] data) {
        SettingsCommand cmd = new SettingsCommand();
        cmd.deserialize(data);
        if (cmd.getOp() == BaseCommand.OP_GET) {
            getSupport().receiveGeneralSettings(
                    (int) cmd.getAmPmIndicator() & 0xff,
                    (int) cmd.getMeasurementUnit() & 0xff
            );
        }

        operationStatus = OperationStatus.FINISHED;
    }

    @Override
    public int getCommandId() {
        return LefunConstants.CMD_SETTINGS;
    }
}
