package sample.service;

import com.gigaspaces.client.TakeModifiers;
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
    private SimpleNotifyEventListenerContainer chatRoomInteractionListener;
    private SimplePollingEventListenerContainer directMessageListener;
    private SimplePollingEventListenerContainer typingStatusListener;
    private boolean runRoomMessageListener = false;


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

//    public void startChatRoomInteractionListener(ChatRoom room){
//        ChatRoomInteraction template = new ChatRoomInteraction();
//        template.setRoomName(room.getName());
//        template.setNotify(true);
//
//        chatRoomInteractionListener = new SimpleNotifyContainerConfigurer(applicationSpace)
//                .template(template)
//                .eventListenerAnnotation(new ChatRoomInteractionListener())
//                .notifyContainer();
//
//        chatRoomInteractionListener.start();
//
//        startMessageListeners(room, mainChatController.getCurrentClient());
//    }

//    private void startMessageListeners(Enviroment enviroment, Device device){
//        startDirectMessageListener(device);
//        startRoomMessageListener(enviroment);
//        startTypingStatusListener(device);
//    }

//    private void startDirectMessageListener(Device device){
//        ChatMessage template = new ChatMessage();
//        template.setReceiverName(device.getName());
//
//        directMessageListener = new SimplePollingContainerConfigurer(applicationSpace)
//                .template(template)
//                .eventListenerAnnotation(new DirectMessageListener())
//                .pollingContainer();
//
//        directMessageListener.start();
//    }

//    private void startTypingStatusListener(Device c){
//        UpdateTypingStatus template = new UpdateTypingStatus();
//        template.setReceiver(c.getName());
//
//        typingStatusListener =  new SimplePollingContainerConfigurer(applicationSpace)
//                .template(template)
//                .eventListenerAnnotation(new TypingStatusListener())
//                .pollingContainer();
//
//        typingStatusListener.start();
//    }

//    private void startRoomMessageListener(Enviroment room){
//        runRoomMessageListener = true;
//        new Thread(() -> {
//            ChatRoomMessage template = new ChatRoomMessage();
//            template.setRoomName(room.getName());
//            template.setForwardTo(mainChatController.getCurrentClient().getName());
//            while (runRoomMessageListener){
//                ChatRoomMessage msg = applicationSpace.take(template, JavaSpace.NO_WAIT, TakeModifiers.FIFO);
//
//                if(msg != null){
//                    onRoomMessageReceived(msg);
//                }
//            }
//        }).start();
//    }

    public void stopChatRoomInteractionListener(){
        chatRoomInteractionListener.stop();
        directMessageListener.stop();
        runRoomMessageListener = false;
        typingStatusListener.stop();
    }


    public synchronized void startConnectionSolicitationResponseListener(){
        System.out.println("Iniciando listener para observar resposta de solicitacao de conexao");

        new Thread(() -> {
            ConnectionSolicitationResponse template = new ConnectionSolicitationResponse();

            boolean responseReceived = false;
            while (!responseReceived) {
                template.setUserName("dasdas");

                ConnectionSolicitationResponse res = applicationSpace.take(template);
                if (res != null) {
                    onConnectionSolicitationResponseReceived(res);
                    responseReceived = true;
                }
            }
        }).start();
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

//    public void writeDirectMessage(String sender, String receiver, String text){
//        System.out.println("Enviando mensagem direta para " + receiver + ": " + text);
//        ChatMessage msg = new ChatMessage(sender, receiver, text);
//
//        applicationSpace.write(msg, 5 * 60000); // mensagem fica no espaco por 5 minutos
//    }

//    public void writeRoomChatMessage(String room, String sender, List<String> forwardToList, String text){
//        System.out.println("Enviando mensagem para a sala " + room + ": " + text);
//        for(String forwardTo: forwardToList){
//            ChatRoomMessage msg = new ChatRoomMessage(room, sender, forwardTo, text);
//            applicationSpace.write(msg, 5 * 60000); // mensagem fica no espaco por 5 minutos
//        }
//    }



//    private void onRoomMessageReceived(ChatRoomMessage msg){
//        if(!msg.getSenderName().equals(mainChatController.getCurrentClient().getName())) {
//            Platform.runLater(() -> {
//                mainChatController.onRoomChatMessageReceived(msg.getSenderName(), msg.getText());
//            });
//        }
//    }

//    public void onUpdateTypingStatusReceived(UpdateTypingStatus statusMsg){
//        System.out.println("Mensagem de atualizacao de status de digitacao recebida do cliente " +
//                statusMsg.getSender() + "\nNovo status: " + statusMsg.getTypingStatus());
//
//        Platform.runLater(() -> {
//            mainChatController.getChatController().showTypingStatus(statusMsg.getSender(), statusMsg.getTypingStatus());
//        });
//    }

//    public void writeTypingStatus(String receiver, TypingStatus status){
//        UpdateTypingStatus typingStatus = new UpdateTypingStatus(status, mainChatController.getCurrentClient().getName(),
//                receiver);
//
//        applicationSpace.write(typingStatus);
//    }
}
