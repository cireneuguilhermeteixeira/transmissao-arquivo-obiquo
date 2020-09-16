package sample.Socket;

import sample.Models.Device;
import sample.Models.Enviroment;
import sample.Models.MessageFileToSend;
import sample.Models.MessageSocket;
import sample.SpaceHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.SortedSet;

public class ClientSideServer implements Runnable{
    private Socket clientSocket;
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;
    private SocketServer server;
    SpaceHandler spaceHandlerInstance = SpaceHandler.getInstance();


    public ClientSideServer(SocketServer server, Socket clientSocket){
        this.server = server;
        this.clientSocket = clientSocket;
        try {

            clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
            clientInput = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public void run() {
        MessageSocket msg;
        boolean clientConnected = true;
        while(clientConnected) {
            try {
                msg = (MessageSocket) clientInput.readObject();
                if (msg != null) {
                    gerenciaMensagem(msg);
                }
            } catch (Exception e) {
                System.out.println("Cliente " + clientSocket.getInetAddress().getHostAddress() +
                        " desconectou, parando tarefa");
                clientConnected = false;
                server.pararLadoCliente(this);
            }
        }
    }

    private void gerenciaMensagem(MessageSocket msg)  {
        Device device;
        switch (msg.getTipo()){
            case "create_device":
                device = (Device) msg.getConteudo();
                spaceHandlerInstance.writeConnectionSolicitation(device);
                sendStatus();
                break;

            case "change_position_device":
                device = (Device) msg.getConteudo();
                spaceHandlerInstance.writeChangePosition(device);
                sendStatus();
                break;

            case "send_file":
                MessageFileToSend messageFile = (MessageFileToSend)msg.getConteudo();
                receiveFile(messageFile);
                break;

            case "get_status":
                System.out.println("Obtendo status.");
                sendStatus();
                break;
            default:
                break;
        }
    }



    private void sendStatus(){

        try {
            Thread.sleep(500);
            ArrayList<Enviroment> enviromentSortedSet = spaceHandlerInstance.readEnviromentRegisteredList();
            MessageSocket messageSocket = new MessageSocket();
            messageSocket.setTipo("get_status");
            messageSocket.setConteudo(enviromentSortedSet);
            sendMessageToSocketToAllClient(messageSocket);

        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    private void sendMessageToSocketToAllClient(MessageSocket messageSocket){
        server.mandarMsgPraTodosClientes(messageSocket);
    }


    private void receiveFile(MessageFileToSend messageFile){
        MessageSocket messageSocket = new MessageSocket();
        messageSocket.setTipo("receive_file");
        messageSocket.setConteudo(messageFile);
        sendMessageToSocket(messageSocket);
    }






    public void sendMessageToSocket(Object msg){
        try {
            clientOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}