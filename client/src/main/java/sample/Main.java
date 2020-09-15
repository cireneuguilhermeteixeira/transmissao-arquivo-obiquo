package sample;

import sample.service.MessageHandlerSocket;

import java.io.IOException;
import java.net.Socket;

public class Main {


    public static void main(String[] args) throws IOException {
        System.out.println("Hello Client");

//        spaceHandlerInstance.writeEnviroment("ambiente 1");
//        spaceHandlerInstance.writeEnviroment("ambiente 2");
//        Device device = new Device();
//        device.setAddress("aaaaaa");
//        device.setLocalization(new Localization("123","456"));
//        device.setName("dispositivo 1");
//        spaceHandlerInstance.writeConnectionSolicitation(device);
//        device.setLocalization(new Localization("456","789"));
//        spaceHandlerInstance.writeChangePosition(device);

        Socket clientSocket = new Socket("localhost", 5000);
        MessageHandlerSocket messageHandlerSocket = new MessageHandlerSocket(clientSocket);
        messageHandlerSocket.getStatus();
    }
}