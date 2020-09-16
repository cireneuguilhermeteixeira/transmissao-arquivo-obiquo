package sample;

import com.gigaspaces.client.ChangeSet;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.EmbeddedSpaceConfigurer;
import org.openspaces.events.notify.SimpleNotifyContainerConfigurer;
import org.openspaces.events.notify.SimpleNotifyEventListenerContainer;
import org.openspaces.events.polling.SimplePollingContainerConfigurer;
import org.openspaces.events.polling.SimplePollingEventListenerContainer;
import sample.Listeners.ChangePositionListener;
import sample.Listeners.CloseConnectionSolicitationListener;
import sample.Listeners.ConnectionSolicitationListener;
import sample.Listeners.EnviromentCreationListener;
import sample.Models.*;

import java.io.Serializable;
import java.util.*;

public class Server {
    private GigaSpace applicationSpace;
    private List<Device> deviceList;
    private EnviromentRegister enviromentRegister;
    private SimplePollingEventListenerContainer connectionSolicitationListener;
    private SimplePollingEventListenerContainer closeConnectionSolicitationListener;
    private SimpleNotifyEventListenerContainer enviromentCreatedListener;
    private SimplePollingEventListenerContainer changePositionListener;


    Server(){
        String spaceName = "mySpace";
        System.out.println("Iniciando aplicacao do servidor, criando espaco de nome " + spaceName);
        try {
            applicationSpace = new GigaSpaceConfigurer(new EmbeddedSpaceConfigurer(spaceName)).gigaSpace();
            System.out.println("Espaco " + spaceName + " criado com sucesso");
            deviceList = new ArrayList<>();
            enviromentRegister = new EnviromentRegister();
            enviromentRegister.setRegisteredEnviromentList(new ArrayList<>());
            applicationSpace.write(enviromentRegister);
        } catch (Exception e){
            System.out.println("Falha ao criar espaco " + spaceName + ".\n");
            e.printStackTrace();
            System.exit(-1);
        }
    }


    void run(){
        registerConnectionSolicitationListener();
        registerEnviromentCreatedListener();
        registerChangePositionListener();
        registerCloseConnectionSolicitationListener();

        connectionSolicitationListener.start();
        enviromentCreatedListener.start();
        changePositionListener.start();
        closeConnectionSolicitationListener.start();
    }


    private void registerConnectionSolicitationListener(){
        connectionSolicitationListener = new SimplePollingContainerConfigurer(applicationSpace)
                .template(new ConnectionSolicitation())
                .eventListenerAnnotation(new ConnectionSolicitationListener(this))
                .pollingContainer();
    }


    private void registerCloseConnectionSolicitationListener(){
        closeConnectionSolicitationListener = new SimplePollingContainerConfigurer(applicationSpace)
                .template(new CloseConnectionSolicitation())
                .eventListenerAnnotation(new CloseConnectionSolicitationListener(this))
                .pollingContainer();
    }

    private void registerChangePositionListener(){
        changePositionListener = new SimplePollingContainerConfigurer(applicationSpace)
                .template(new Device())
                .eventListenerAnnotation(new ChangePositionListener(this))
                .pollingContainer();
    }


    private void registerEnviromentCreatedListener(){
        enviromentCreatedListener = new SimpleNotifyContainerConfigurer(applicationSpace)
                .template(new Enviroment())
                .eventListenerAnnotation(new EnviromentCreationListener(this))
                .notifyContainer();
    }



    private void sendConnectionSolicitationResponse(String userName, boolean isAccepted){
        System.out.println("Enviando resposta de solicitacao de conexao para dispositivo " + userName + "" +
                "\nResposta " + (isAccepted? "Aceito": "Negado"));

        ConnectionSolicitationResponse response = new ConnectionSolicitationResponse(userName, isAccepted);
        applicationSpace.write(response);
    }


    public void onChangePositionReceived(Device deviceWithNewPosition){

        Device deviceWithOldPosition = deviceList.stream().filter(device -> device.getName().equals(device.getName()))
                .findFirst().orElse(null);

        assert deviceWithOldPosition != null;
        if(deviceWithNewPosition.getLocalization().getLatitude().equals(deviceWithOldPosition.getLocalization().getLatitude()) &&
          deviceWithNewPosition.getLocalization().getLatitude().equals(deviceWithOldPosition.getLocalization().getLatitude())){
            System.out.println("Dispositivo não se moveu");
        }else{
            System.out.println("Dispositivo "+deviceWithNewPosition.getName()+" está se movendo.");
            handleDeviceInEnviroment( deviceWithNewPosition, false);
        }

    }


    public void onConnectionSolicitationReceived(ConnectionSolicitation solicitation){
        solicitation.getDevice().setName("dispositivo "+ (deviceList.size()+1));
        solicitation.getDevice().setAddress("dispositivo "+ (deviceList.size()+1));

        System.out.println("Solicitacao de conexao recebida." +
                "\n" + solicitation.getDevice().getName());

        System.out.println("Novo dispositivo " + solicitation.getDevice().getName() + " se registrando ao servidor");
        handleDeviceInEnviroment(solicitation.getDevice(), true);
        sendConnectionSolicitationResponse(solicitation.getDevice().getName(), true);
    }



    private void handleDeviceInEnviroment(Device device, Boolean isNewDevice){
        for (Device dev: deviceList){
            if(device.getName().equalsIgnoreCase(dev.getName())){
                deviceList.remove(dev);
                break;
            }
        }

        if(isNewDevice){
            //Criando novo dispositivo
            if(enviromentRegister.getRegisteredEnviromentList().size()==0){
                Enviroment enviroment = createDefaultEnviroment();
                enviroment.setLocalization(device.getLocalization());
                onEnviromentCreated(enviroment);
                device.setMyEnviroment(enviroment);
                registerDeviceInEnviroment(enviroment.getName(), device);
            }else{
                Device deviceMostClose = checkDeviceMostClose(device);
                if(!deviceMostClose.getName().equals(device.getName())){
                    device.setMyEnviroment(deviceMostClose.getMyEnviroment());
                    registerDeviceInEnviroment(deviceMostClose.getMyEnviroment().getName(), device);
                }else{
                    Enviroment enviroment = createDefaultEnviroment();
                    enviroment.setLocalization(device.getLocalization());
                    onEnviromentCreated(enviroment);
                    device.setMyEnviroment(enviroment);
                    registerDeviceInEnviroment(enviroment.getName(), device);
                }
            }
        }else{
            //Aterando posicao dispositivo
            Device deviceMostClose = checkDeviceMostClose(device);
            if(!deviceMostClose.getName().equals(device.getName())){
                removeDeviceFromEnviroment(device.getMyEnviroment().getName(),device);
                device.setMyEnviroment(deviceMostClose.getMyEnviroment());
                registerDeviceInEnviroment(deviceMostClose.getMyEnviroment().getName(), device);

            }else{
                Enviroment enviroment = createDefaultEnviroment();
                onEnviromentCreated(enviroment);
                device.setMyEnviroment(enviroment);
                registerDeviceInEnviroment(enviroment.getName(), device);
            }

        }
        deviceList.add(device);
    }


    private Device checkDeviceMostClose(Device device) {
        Double shortestLatitude;
        Double shortestLongitude;
        Double hipotenusa;
        Double hipotenusaAnterior = 9999999.9;


        Device deviceMostClose = device;
        for (Device dev : deviceList) {

            shortestLatitude = Math.abs(dev.getLocalization().getLatitude() - device.getLocalization().getLatitude());
            shortestLongitude = Math.abs(dev.getLocalization().getLongitude() - device.getLocalization().getLongitude());
            hipotenusa = Math.sqrt((shortestLatitude*shortestLatitude) + (shortestLongitude*shortestLongitude));


            if (!dev.getName().equalsIgnoreCase(device.getName()) && hipotenusa< hipotenusaAnterior) {
                hipotenusaAnterior = hipotenusa;
                if(hipotenusaAnterior< 200){
                    deviceMostClose = dev;
                }
            }
        }
        return deviceMostClose;
    }

    private Enviroment createDefaultEnviroment(){
        Enviroment enviroment = new Enviroment();
        int amountEnviroments =  enviromentRegister.getRegisteredEnviromentList().size();
        enviroment.setConnectedDeviceList(new ArrayList<>());
        enviroment.setName("ambiente "+(amountEnviroments+1));
        return enviroment;
    }


    private void updateEnviromentRegisterInSpace(){
        ArrayList<Enviroment> list = enviromentRegister.getRegisteredEnviromentList();
        applicationSpace.change(new EnviromentRegister(), new ChangeSet().set("registeredEnviromentList", (Serializable) list));
    }

    public void onEnviromentCreated(Enviroment enviroment){
        System.out.println("Nova sala criada.\nNome: " + enviroment.getName());
        enviromentRegister.getRegisteredEnviromentList().add(enviroment);
        applicationSpace.change(new EnviromentRegister(), new ChangeSet().addToCollection("registeredEnviromentList", enviroment));
        System.out.println("Sala " + enviroment.getName() + " adicionada ao registro de salas no espaco");
    }


    private void registerDeviceInEnviroment(String enviromentName, Device device){
        System.out.println("Registrando dispositivo " + device.getName() + " no ambiente " + enviromentName);

        Enviroment env = enviromentRegister.getRegisteredEnviromentList().stream()
                .filter(e -> e.getName().equals(enviromentName)).findFirst().orElse(null);

        assert env != null;
        if(env.getConnectedDeviceList() == null){
            env.setConnectedDeviceList(new ArrayList<>());
        }

        env.getConnectedDeviceList().add(device);

        Enviroment template = new Enviroment();
        template.setName(enviromentName);
        applicationSpace.change(template, new ChangeSet().addToCollection("connectedDeviceList", device));
        updateEnviromentRegisterInSpace();

    }


    private void removeDeviceFromEnviroment(String nomeAmbiente, Device device){
        System.out.println("Removendo dispositivo " + device.getName() + " do ambiente " + nomeAmbiente);

        Enviroment enviroment = enviromentRegister.getRegisteredEnviromentList().stream()
                .filter(r -> r.getName().equals(nomeAmbiente)).findFirst().orElse(null);

        assert enviroment != null;
        enviroment.getConnectedDeviceList().removeIf(d -> d.getName().equals(device.getName()));

        Enviroment template = new Enviroment();
        template.setName(nomeAmbiente);
        applicationSpace.change(template, new ChangeSet().removeFromCollection("connectedDeviceList", device));

        updateEnviromentRegisterInSpace();

    }


    public void onCloseConnectionSolicitationReceived(CloseConnectionSolicitation solicitation){
        System.out.println("Dispositivo de id " + solicitation.getDeviceId() + " se desconectou");

        Device device = deviceList.stream().
                filter(d -> d.getId().equals(solicitation.getDeviceId()))
                .findFirst()
                .orElse(null);

        assert device != null;
        if(solicitation.getCurrentInEnviromentName() != null){
            removeDeviceFromEnviroment(solicitation.getCurrentInEnviromentName(), device);
        }

        //client.setStatus(Status.OFFLINE);
    }
}
