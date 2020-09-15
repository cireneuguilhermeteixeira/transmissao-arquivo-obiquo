package sample;

import com.gigaspaces.client.WriteModifiers;
import com.j_spaces.core.client.EntryAlreadyInSpaceException;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.CannotFindSpaceException;
import org.openspaces.core.space.SpaceProxyConfigurer;
import org.openspaces.events.notify.SimpleNotifyEventListenerContainer;
import org.openspaces.events.polling.SimplePollingEventListenerContainer;
import sample.Models.*;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class SpaceHandler {
    private static SpaceHandler instance;
    private GigaSpace applicationSpace;



    private SpaceHandler(){
        try {
            applicationSpace = new GigaSpaceConfigurer(new SpaceProxyConfigurer("mySpace"))
                    .gigaSpace();
        } catch (CannotFindSpaceException e){
            System.out.println("Nao foi possivel encontrar o espaco mySpace."+
                    "\nCertifique-se de que o espaco foi criado e tente novamente.");

            System.exit(-1);
        }
    }

    public static SpaceHandler getInstance() {
        if(instance == null){
            instance = new SpaceHandler();
        }
        return instance;
    }




    public void writeConnectionSolicitation(Device device){
        ConnectionSolicitation solicitation = new ConnectionSolicitation(device);
        applicationSpace.write(solicitation);
    }

    public void writeChangePosition(Device device){
        applicationSpace.write(device);
    }

    private void onConnectionSolicitationResponseReceived(ConnectionSolicitationResponse response){
       System.out.println("response: "+response.getUserName());
    }


    public void writeCloseConnectionSolicitation(String userName, String currentInRoomName){
        CloseConnectionSolicitation solicitation = new CloseConnectionSolicitation(userName, currentInRoomName);
        applicationSpace.write(solicitation);
    }


    public void writeEnviroment(String enviromentName) throws EntryAlreadyInSpaceException {
        System.out.println("Escrevendo sala de nome " + enviromentName + " no espaco");

        Enviroment enviroment = new Enviroment();
        enviroment.setName(enviromentName);
        enviroment.setConnectedDeviceList(new ArrayList<>());

        try {
            applicationSpace.write(enviroment, WriteModifiers.WRITE_ONLY);
            System.out.println("Ambiente criado: " + enviroment);
        }
        catch (Exception e){
            throw new EntryAlreadyInSpaceException("Erro", "Ja existe um ambiente com o nome " + enviromentName + " no espaco");
        }
    }


    public ArrayList<Enviroment> readEnviromentRegisteredList(){
        EnviromentRegister register = applicationSpace.read(new EnviromentRegister());
        return register.getRegisteredEnviromentList();
    }





    public Enviroment readEnviroment(String enviromentName){
        Enviroment template = new Enviroment();
        template.setName(enviromentName);

        return applicationSpace.read(template);
    }




}
