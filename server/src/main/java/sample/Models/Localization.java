package sample.Models;

import com.gigaspaces.annotation.pojo.SpaceClass;

import java.io.Serializable;

@SpaceClass
public class Localization implements Serializable {
    private Float latitude;
    private Float longitude;

    public Localization(){
    }


    public Localization(Float latitude, Float longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }
}
