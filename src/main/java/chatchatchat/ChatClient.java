package chatchatchat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient extends Application {

    private static Thread readMessage;
    public static DataInputStream i;
    public static DataOutputStream o;
    public static Socket s;


    //calls this method if click Connect
    public static void connectToServer(String name, String portNumber) throws IOException { // method gets hostname and Port
        InetAddress ip = InetAddress.getByName(name);
        int x = Integer.parseInt(portNumber);
        s = new Socket(ip,x);   // create new socket
        i = new DataInputStream(s.getInputStream());
        o = new DataOutputStream(s.getOutputStream());

        System.out.println("User connection to Server");
    }

    // read from inputStream and return (gets called in read Thread)
    public static String receiveMsg() throws IOException {
        return i.readUTF();
    }

    //send Message to socket
    public static void sendMsg(String msg) throws IOException {
        o.writeUTF(msg); // write to outputStream
        o.flush(); // empty output stream
    }




    // MAIN METHOD starts app
    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }


    // method to start and stop app and load Scene
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/chattool.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Tool");
        primaryStage.show();
        primaryStage.isResizable();

        //to stop the app if the window is closed
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
}
