package sample;

import sample.Socket.SocketServer;

public class Main {
    public static void main(String[] args) {
        //Inicia Server GigaSpace
        Server server = new Server();
        server.run();

        //Inicia Server Socket
        SocketServer socketServer = new SocketServer();
        socketServer.run();
    }
}