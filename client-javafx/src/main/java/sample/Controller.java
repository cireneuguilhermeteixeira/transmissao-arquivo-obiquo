package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import sample.Models.Device;
import sample.Models.Enviroment;
import sample.Models.Localization;
import sample.service.MessageHandlerSocket;


import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;

public class Controller {
    @FXML private AnchorPane anchorPane;
    @FXML private Label labelCreateDevice;
    @FXML private Label labelMoveDevice;
    @FXML private Label labelEnviroment;
    @FXML private Label descriptionDevice;
    private boolean isEnabledToCreateDevice = false;
    private boolean isEnabledToMoveDevice = false;
    private boolean isEnabledToSeeEnviroment = false;
    private MessageHandlerSocket messageHandlerSocket;
    private ArrayList<Enviroment> enviromentSortedSet;
    private ArrayList<Circle> circleArrayList = new ArrayList<>();
    private ArrayList<Device> deviceArrayList;
    private Localization localization;


    public ArrayList<Enviroment> getEnviromentSortedSet() {
        return enviromentSortedSet;
    }

    public void setEnviromentSortedSet(ArrayList<Enviroment> enviromentSortedSet) {
        this.enviromentSortedSet = enviromentSortedSet;
    }

    @FXML public void initialize() throws IOException {

        Socket clientSocket = new Socket("localhost", 5000);
        messageHandlerSocket = new MessageHandlerSocket(clientSocket, this);
        messageHandlerSocket.getStatus();


        anchorPane.addEventHandler(MOUSE_PRESSED, event -> {
            if (event.getButton().equals(PRIMARY)) {
                if(isEnabledToCreateDevice){
                    createDevice(event.getSceneX(), event.getSceneY());
                }
            }else{
                if(isEnabledToMoveDevice && localization !=null){
                    descriptionDevice.setText("");
                    Circle newCircle = new Circle(event.getSceneX(),event.getSceneY(),10);
                    changePositionDevice(localization, new Localization(newCircle.getCenterX(),newCircle.getCenterY()));
                }
            }
        });
    }


    private void changePositionDevice(Localization oldLocalization, Localization newLocalization){

        for (Device device: deviceArrayList) {
            if(device.getLocalization().getLatitude().equals(oldLocalization.getLatitude()) &&
                    device.getLocalization().getLongitude().equals(oldLocalization.getLongitude())){
                device.getLocalization().setLatitude(newLocalization.getLatitude());
                device.getLocalization().setLongitude(newLocalization.getLongitude());
                messageHandlerSocket.changePositionDevice(device);
                break;
            }
        }
    }


    private void createDevice(Double x, Double y){
        Device device = new Device();
        device.setLocalization(new Localization(x,y));
        messageHandlerSocket.createDevice(device);
    }







    public void enableCreateDevice(ActionEvent event){
        isEnabledToCreateDevice = true;
        isEnabledToMoveDevice = false;
        isEnabledToSeeEnviroment = false;
        labelCreateDevice.setText("Habilitado");
        labelMoveDevice.setText("");
        labelEnviroment.setText("");

        descriptionDevice.setText("");
        localization = null;

    }

    public void enableMoveDevice(ActionEvent event){
        isEnabledToCreateDevice = false;
        isEnabledToMoveDevice = true;
        isEnabledToSeeEnviroment = false;
        labelCreateDevice.setText("");
        labelMoveDevice.setText("Habilitado");
        labelEnviroment.setText("");

        descriptionDevice.setText("");
        localization = null;
    }

    public void enableSeeEnviroments(ActionEvent event){
        isEnabledToCreateDevice = false;
        isEnabledToMoveDevice = false;
        isEnabledToSeeEnviroment = true;
        labelCreateDevice.setText("");
        labelMoveDevice.setText("");
        labelEnviroment.setText("Habilitado");

        descriptionDevice.setText("");
        localization = null;
        toColorRed();

    }


    private void toColorRed(){
        for (Circle circle: circleArrayList) {
            circle.setFill(Paint.valueOf("red"));
        }
    }


    public void updateDevicesInSpace() throws InterruptedException {
        Platform.runLater(() -> {
            localization = null;
            anchorPane.getChildren().removeAll(circleArrayList);
        });
        Thread.sleep(500);

        circleArrayList = new ArrayList<>();
        deviceArrayList = new ArrayList<>();
        for (Enviroment enviroment: enviromentSortedSet) {
            deviceArrayList.addAll(enviroment.getConnectedDeviceList());
        }

        for (Device device: deviceArrayList){
            Circle circle = new Circle(device.getLocalization().getLatitude(), device.getLocalization().getLongitude(), 10);
            circle.setFill(Paint.valueOf("red"));
            addEventListenerInCircle(circle);
            circleArrayList.add(circle);
            Platform.runLater(() -> {
                anchorPane.getChildren().add(circle);
            });
        }
    }

    private  void addEventListenerInCircle(Circle circle){
        circle.addEventHandler(MOUSE_PRESSED, event -> {
            if (event.getButton().equals(PRIMARY)) {
                if(isEnabledToMoveDevice){
                    descriptionDevice.setText("Selecione a posição de destino com botão direito.");
                    circle.setFill(Paint.valueOf("green"));
                    circleArrayList.add(circle);
                    if(localization ==null){
                        localization = new Localization(circle.getCenterX(),circle.getCenterY());
                    }
                }else if(isEnabledToSeeEnviroment){
                    toColorAllEnviroment(circle);
                }
            }
        });
    }


    private void toColorAllEnviroment(Circle circle) {
        toColorRed();
        Enviroment enviroment = null;
        for (Device device : deviceArrayList) {
            if(device.getLocalization().getLatitude().equals(circle.getCenterX())
            && device.getLocalization().getLongitude().equals(circle.getCenterY())){
                enviroment =device.getMyEnviroment();
                descriptionDevice.setText("Nome: "+device.getName() +" Ambiente: "+device.getMyEnviroment().getName());
            }
        }
        if(enviroment !=null){
            for (Device dev: enviroment.getConnectedDeviceList()) {
                Optional<Circle> c = circleArrayList.stream().filter(circle1 -> dev.getLocalization().getLatitude().equals(circle1.getCenterX())
                        && dev.getLocalization().getLongitude().equals(circle1.getCenterY())).findFirst();
                if(c.isPresent()){
                    Circle circle1 = c.get();
                    circle1.setFill(Paint.valueOf("blue"));
                }

            }
        }

    }


}
