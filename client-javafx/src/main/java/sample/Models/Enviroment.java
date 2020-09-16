package sample.Models;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;
import java.util.ArrayList;

@SpaceClass
public class Enviroment implements Serializable, Comparable<Enviroment>{

    private String name;
    private Localization localization;
    private ArrayList<Device> connectedDeviceList;

    @SpaceId(autoGenerate = true)
    public String getName() {
        return name;
    }


    public Localization getLocalization() {
        return localization;
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

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }
}
