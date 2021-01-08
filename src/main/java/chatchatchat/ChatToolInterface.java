package chatchatchat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class ChatToolInterface implements Initializable {


    public Button btnSendMsg;
    public Button btnConnect;
    public Button btnStartServer;
    public Button btnDisconnect;
    public TextField inputTextField;
    public TextArea outputTextArea;
    public TextField fieldToTypePortNumber;
    public TextField fieldToTypeHostName;
    public TextField fieldToTypeUsername;

    public ChatToolInterface() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/chattool.fxml"));
        fxmlLoader.setController(this);
    }

    // set default values for TextFields
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fieldToTypeHostName.setText("127.0.0.1");
        fieldToTypePortNumber.setText("1234");
        outputTextArea.setText("Firstly, type the hostname and the port number."+System.lineSeparator()+
                "Secondly, click on StartServer."+System.lineSeparator()+"Thirdly, click on Connect."
                +System.lineSeparator()+"Afterwards, you can chat.");
        inputTextField.setText("Please type your message here after connecting to the server.");
        btnDisconnect.setDisable(true);

    }
    // gets called when "start server" is clicked
    public void startingServer(ActionEvent actionEvent) throws IOException {
        String username = fieldToTypeUsername.getText();
        String port = fieldToTypePortNumber.getText();

        if (Server.isServerAlreadyStarted(port)) { // starts server if not allready started in Server.java
            outputTextArea.appendText(System.lineSeparator()+System.lineSeparator()+"SERVER IS ALREADY ON."
                    +System.lineSeparator()+ "Please click connect button to log in."
                    +System.lineSeparator());
            btnStartServer.setDisable(true);
        } else {
            btnStartServer.setDisable(true); // disable button in order to not start the server
            outputTextArea.appendText("" + System.lineSeparator() + "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + "SERVER IS READY. PLEASE CLICK CONNECTION BUTTON TO LOG IN");
        }
    }

    public void connectAcceptRequest(ActionEvent actionEvent) throws IOException {
        String name = fieldToTypeHostName.getText();
        String port = fieldToTypePortNumber.getText();
        ChatClient.connectToServer(name,port); // calls the method to connect the client to the server
        outputTextArea.clear();
        inputTextField.clear();
        System.out.println("Started reading");
        //readMessage thread - needed to read in parallel the whole time
        // system outs used for test purposes only
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Entered run");
                // start the read thread
                while (true) {
                    if (checkConnection()){
                        break;
                    }
                    System.out.println("Entered while loop");
                    String msg = "Test";
                    try {
                        System.out.println("Entered try");
                        //Read the message sent to this client
                        msg = ChatClient.receiveMsg(); // read from input stream
                        StringTokenizer st = new StringTokenizer(msg,"#"); // split message in userName and message
                        String sender = st.nextToken(); // get first part = userName
                        String msgToSend = st.nextToken(); // get second part = message
                        System.out.println("Messege that was read: "+msg);
                        showMessage(sender+": "+msgToSend);
                        System.out.println("After invoking showMessage Method");
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    if (checkConnection()){
                        break;
                    }
                }

            }
        });
        readMessage.start();
        //disable felds and buttons after connected to the server
        btnDisconnect.setDisable(false);
        btnConnect.setDisable(true);
        fieldToTypeUsername.setDisable(true);
        fieldToTypeHostName.setDisable(true);
        fieldToTypePortNumber.setDisable(true);
    }

    // gets called when clicking on button "send"
    public void sendMessage(ActionEvent actionEvent) throws IOException {
        // if no text typed in do nothing
        if (inputTextField.getText().equals("")) {

        } else { // add message to own chatTextBox and send message to server
            System.out.println(fieldToTypeUsername.getText()+"#"+inputTextField.getText());
            ChatClient.sendMsg(fieldToTypeUsername.getText()+"#"+inputTextField.getText());

        }
        inputTextField.clear(); // empty textField after message was sent
    }
    public void showMessage(String incomingMsg) {
        System.out.println("Message delivered");
        outputTextArea.appendText(incomingMsg+System.lineSeparator()); // add incoming messages to TextArea

    }

    // checks if the connection is established by looking to the buttons (enabled = connection not established)
    public boolean checkConnection() {
        if (btnDisconnect.isDisabled()) {
            return true;
        } else {
            return false;
        }

    }


    // TO-DO in case of unhandled exception call this method (not implemented yet)
    public void stopConnectionIfFailure() throws IOException {
        ChatClient.s.close();
        ChatClient.i.close();
        ChatClient.o.close();
        System.out.println("The connection was forcefully stopped.");
        outputTextArea.appendText("Connection has been closed."+System.lineSeparator()+
                "Server is ready."+System.lineSeparator());
        btnDisconnect.setDisable(true);

    }


    // called when button "disconnect" is clicked
    public void disconnectTheSocket(ActionEvent actionEvent) throws IOException {
        ChatClient.s.close();
        ChatClient.i.close();
        ChatClient.o.close();

        outputTextArea.appendText("Connection has been closed."+System.lineSeparator()+
                "Server is ready."+System.lineSeparator()+"Please log in or start a new server."+System.lineSeparator());
        btnDisconnect.setDisable(true);
        btnConnect.setDisable(false);
        fieldToTypeUsername.setDisable(false);
        fieldToTypeHostName.setDisable(false);
        fieldToTypePortNumber.setDisable(false);
        System.out.println("Connection was closed with disconnect button");

    }
}
