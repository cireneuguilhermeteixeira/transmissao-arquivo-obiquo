package sample.Models;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;
import java.util.ArrayList;

@SpaceClass
public class Enviroment implements Serializable, Comparable<Enviroment>{

    private String name;
    private ArrayList<Device> connectedDeviceList;

    @SpaceId(autoGenerate = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Device> getConnectedDeviceList() {
        return connectedDeviceList;
    }

    public void setConnectedDeviceList(ArrayList<Device> connectedDeviceList) {
        this.connectedDeviceList = connectedDeviceList;
    }


    public int compareTo(Enviroment c) {
        return this.name.compareTo(c.getName());
    }

}
