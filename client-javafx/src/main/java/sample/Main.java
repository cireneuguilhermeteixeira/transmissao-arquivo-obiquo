package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.service.MessageHandlerSocket;

import java.net.Socket;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();


        Socket clientSocket = new Socket("localhost", 5000);
        MessageHandlerSocket messageHandlerSocket = new MessageHandlerSocket(clientSocket);
        messageHandlerSocket.getStatus();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
