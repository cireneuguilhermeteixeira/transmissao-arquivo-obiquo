package sample.Models;

import com.gigaspaces.annotation.pojo.SpaceClass;

import java.io.Serializable;

@SpaceClass
public class Localization implements Serializable {
    private Double latitude;
    private Double longitude;

    public Localization(){
    }


    public Localization(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
