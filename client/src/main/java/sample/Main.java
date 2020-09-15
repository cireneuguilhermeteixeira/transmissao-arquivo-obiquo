package sample;

import sample.service.MessageHandlerSocket;

import java.io.IOException;
import java.net.Socket;

public class Main {


    public static void main(String[] args) throws IOException {
        System.out.println("Hello Client");
        Socket clientSocket = new Socket("localhost", 5000);
        MessageHandlerSocket messageHandlerSocket = new MessageHandlerSocket(clientSocket);
        messageHandlerSocket.getStatus();
    }
}