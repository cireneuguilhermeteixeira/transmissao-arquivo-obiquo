package sample.Models;

import com.gigaspaces.annotation.pojo.SpaceClass;

@SpaceClass
public class CloseConnectionSolicitation {
    private String deviceId;
    private String currentInEnviromentName;

    public CloseConnectionSolicitation() {
    }

    public CloseConnectionSolicitation(String currentInEnviromentName, String deviceId){
        this.currentInEnviromentName = currentInEnviromentName;
        this.deviceId = deviceId;
    }

    public String getCurrentInEnviromentName() {
        return currentInEnviromentName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setCurrentInEnviromentName(String currentInEnviromentName) {
        this.currentInEnviromentName = currentInEnviromentName;
    }


    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
