package sample.Models;


import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;

@SpaceClass
public class Device implements Serializable, Comparable<Device> {
    private String id;
    private String name;
    private String address;
    private Localization localization;
    private Enviroment myEnviroment;

    public Device(){
    }


    public Enviroment getMyEnviroment() {
        return myEnviroment;
    }

    public Localization getLocalization() {
        return localization;
    }

    public String getAddress() {
        return address;
    }

    @SpaceId(autoGenerate = true)
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setMyEnviroment(Enviroment myEnviroment) {
        this.myEnviroment = myEnviroment;
    }

    public int compareTo(Device o) {
        return localization.getLatitude().compareTo(o.getLocalization().getLatitude());
    }

}

