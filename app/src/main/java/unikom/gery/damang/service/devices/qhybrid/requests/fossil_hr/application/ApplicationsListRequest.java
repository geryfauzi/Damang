package unikom.gery.damang.service.devices.qhybrid.requests.fossil_hr.application;

import org.json.JSONArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import unikom.gery.damang.impl.GBDevice;
import unikom.gery.damang.model.GenericItem;
import unikom.gery.damang.service.devices.qhybrid.adapter.fossil_hr.FossilHRWatchAdapter;
import unikom.gery.damang.service.devices.qhybrid.requests.fossil.file.FileLookupAndGetRequest;
import unikom.gery.damang.service.devices.qhybrid.file.FileHandle;

public class ApplicationsListRequest extends FileLookupAndGetRequest{
    public ApplicationsListRequest(FossilHRWatchAdapter adapter) {
        super(FileHandle.APP_CODE, adapter);
    }

    public void handleFileData(byte[] fileData){
        ArrayList<ApplicationInformation> applicationInfos = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.wrap(fileData);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(12);
        while(buffer.remaining() > 4){
            short packetLength = buffer.getShort();
            buffer.get();
            int nameLength = buffer.get() - 1; // cutting off null byte
            byte[] nameBuffer = new byte[nameLength];
            buffer.get(nameBuffer);
            String name = new String(nameBuffer);
            buffer.get(); // null byte
            byte handle = buffer.get();
            int hash = buffer.getInt();
            String version = String.format(
                    "%d.%d.%d.%d",
                    buffer.get(), buffer.get(), buffer.get(), buffer.get()
                    );
            applicationInfos.add(new ApplicationInformation(
                    name,
                    version,
                    hash,
                    handle
            ));
        }
        ((FossilHRWatchAdapter) getAdapter()).setInstalledApplications(applicationInfos);
        GBDevice device = getAdapter().getDeviceSupport().getDevice();
        JSONArray array = new JSONArray();
        for(ApplicationInformation info : applicationInfos){
            array.put(info.getAppName());
        }
        device.addDeviceInfo(new GenericItem("INSTALLED_APPS", array.toString()));
        device.sendDeviceUpdateIntent(getAdapter().getContext());
    }
    public void handleFileLookupError(FILE_LOOKUP_ERROR error){

    }
}
