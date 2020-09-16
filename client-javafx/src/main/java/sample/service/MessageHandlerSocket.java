package sample.service;

import sample.Models.Device;
import sample.Models.MessageFileToSend;
import sample.Models.MessageSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MessageHandlerSocket {

    private Socket clientSocket;
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;
    private Boolean run  = true;

    public MessageHandlerSocket(Socket clientSocket){
        this.clientSocket = clientSocket;
        try {
            clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
            clientInput = new ObjectInputStream(clientSocket.getInputStream());

            listenSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void listenSocket(){
        new Thread(() -> {
            while (this.run){
                try{
                    MessageSocket msg = (MessageSocket)clientInput.readObject();
                    if(msg != null){
                        if(msg.getTipo().equalsIgnoreCase("get_status")){
                            System.out.println("get_status");

                        }else if(msg.getTipo().equalsIgnoreCase("create_device")){
                            System.out.println("create_device");

                        }else if(msg.getTipo().equalsIgnoreCase("change_position_device")){
                            System.out.println("change_position_device");

                        }else if(msg.getTipo().equalsIgnoreCase("receive_file")){
                            System.out.println("receive_file");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void sendMessageThroughSocket(MessageSocket msg){
        try {
            clientOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void createDevice(Device device){
        MessageSocket messageSocket = new MessageSocket();
        messageSocket.setTipo("create_device");
        messageSocket.setConteudo(device);
        sendMessageThroughSocket(messageSocket);
    }

    public void changePositionDevice(Device device){
        MessageSocket messageSocket = new MessageSocket();
        messageSocket.setTipo("change_position_device");
        messageSocket.setConteudo(device);
        sendMessageThroughSocket(messageSocket);
    }



    public void sendFile(Device from, Device to, byte[] content){
        MessageSocket messageSocket = new MessageSocket();
        messageSocket.setConteudo("send_file");
        MessageFileToSend messageFileToSend = new MessageFileToSend(from, to, content);
        messageSocket.setConteudo(messageFileToSend);
        sendMessageThroughSocket(messageSocket);
    }


    public void getStatus(){
        MessageSocket messageSocket = new MessageSocket();
        messageSocket.setTipo("get_status");
        sendMessageThroughSocket(messageSocket);
    }


    public void setRun(Boolean bool){
        this.run = bool;
    }
}
