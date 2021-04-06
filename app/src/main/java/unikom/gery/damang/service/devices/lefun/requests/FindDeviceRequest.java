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
import unikom.gery.damang.devices.lefun.commands.FindDeviceCommand;
import unikom.gery.damang.service.btle.TransactionBuilder;
import unikom.gery.damang.service.devices.lefun.LefunDeviceSupport;
import unikom.gery.damang.service.devices.miband.operations.OperationStatus;

public class FindDeviceRequest extends Request {
    public FindDeviceRequest(LefunDeviceSupport support, TransactionBuilder builder) {
        super(support, builder);
    }

    @Override
    public byte[] createRequest() {
        FindDeviceCommand cmd = new FindDeviceCommand();
        return cmd.serialize();
    }

    @Override
    public int getCommandId() {
        return LefunConstants.CMD_FIND_DEVICE;
    }

    @Override
    public void handleResponse(byte[] data) {
        FindDeviceCommand cmd = new FindDeviceCommand();
        cmd.deserialize(data);

        if (!cmd.isSuccess())
            reportFailure("Could not initiate find device");

        operationStatus = OperationStatus.FINISHED;
    }
}
