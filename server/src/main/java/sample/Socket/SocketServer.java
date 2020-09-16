package sample.Socket;

import sample.Models.Device;
import sample.Models.MessageSocket;
import sample.Socket.ClientSideServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class SocketServer {

    private static final int SERVER_PORT = 5000;
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private HashMap<ClientSideServer, Future> clientHandlerTaskMap;

    private void initServer() {
        try {
            System.out.println("Servidor rodando na porta " + SERVER_PORT);
            serverSocket = new ServerSocket(SERVER_PORT);
            pool = Executors.newFixedThreadPool(10);
            clientHandlerTaskMap = new HashMap<>();

        } catch (IOException e) {
            System.out.println("Erro ao tentar iniciar server.");
            e.printStackTrace();
        }
    }

    public SocketServer(){};


    public void run(){
        initServer();
        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Aguardando conexoes");
                    Socket client = serverSocket.accept();
                    System.out.println("Nova conexao recebida: " + client.getInetAddress().getHostAddress());
                    ClientSideServer clientSideServer = new ClientSideServer(this, client);
                    Future task = pool.submit(clientSideServer);

                    clientHandlerTaskMap.put(clientSideServer, task);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    void mandarMsgPraTodosClientes(MessageSocket messageSocket){
        clientHandlerTaskMap.forEach((clientSideServer, future) -> {
            clientSideServer.sendMessageToSocket(messageSocket);
        });
    }

    void pararLadoCliente(ClientSideServer clientSideServer){
        System.out.println("Parando tarefa para o socket do dispositivo ");
        Future clientHandlerTask = clientHandlerTaskMap.get(clientSideServer);
        clientHandlerTask.cancel(true);
        System.out.println("Tarefa  finalizada com sucesso!");

    }
}
