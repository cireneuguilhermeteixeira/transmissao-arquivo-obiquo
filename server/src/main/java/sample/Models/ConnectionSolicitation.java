package sample.Models;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

@SpaceClass
public class ConnectionSolicitation {
    private Device device;

    public ConnectionSolicitation(){}

    public ConnectionSolicitation(Device device){
        this.device = device;
    }

    @SpaceId(autoGenerate = false)
    public Device getDevice() {
        return device;
    }


    public void setDevice(Device device) {
        this.device = device;
    }
}
