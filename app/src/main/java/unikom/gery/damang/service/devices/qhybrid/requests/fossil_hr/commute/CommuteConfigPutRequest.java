/*  Copyright (C) 2019-2021 Andreas Shimokawa, Daniel Dakhno

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
package unikom.gery.damang.service.devices.qhybrid.requests.fossil_hr.commute;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unikom.gery.damang.service.devices.qhybrid.adapter.fossil_hr.FossilHRWatchAdapter;
import unikom.gery.damang.service.devices.qhybrid.requests.fossil_hr.json.JsonPutRequest;
import unikom.gery.damang.util.GB;

public class CommuteConfigPutRequest extends JsonPutRequest {
    public CommuteConfigPutRequest(String[] menuItems, FossilHRWatchAdapter adapter) {
        super(createObject(menuItems), adapter);
    }

    private static JSONObject createObject(String[] menuItems) {
        try {
            return new JSONObject()
                    .put("push", new JSONObject()
                            .put("set", new JSONObject()
                                    .put("commuteApp._.config.destinations", new JSONArray(menuItems))
                            )
                    );
        } catch (JSONException e) {
            GB.toast("error creating json", Toast.LENGTH_LONG, GB.ERROR, e);
        }

        return null;
    }
}
