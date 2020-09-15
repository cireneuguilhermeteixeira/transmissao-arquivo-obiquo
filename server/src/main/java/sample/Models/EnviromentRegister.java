package sample.Models;


import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.SortedSet;

@SpaceClass
public class EnviromentRegister {

    private String id;
    private ArrayList<Enviroment> registeredEnviromentList;


    public EnviromentRegister(){
    }

    public ArrayList<Enviroment> getRegisteredEnviromentList() {
        return registeredEnviromentList;
    }

    public void setRegisteredEnviromentList(ArrayList<Enviroment> registeredEnviromentList) {
        this.registeredEnviromentList = registeredEnviromentList;
    }

    @SpaceId(autoGenerate = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

