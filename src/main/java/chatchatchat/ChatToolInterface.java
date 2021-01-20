package chatchatchat;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

//this is a controller for the chattool.fxml
public class ChatToolInterface implements Initializable {


    public Server server;
    public Client chatUser;
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
        try {
            fieldToTypeHostName.setText(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        fieldToTypePortNumber.setText("1234");
        outputTextArea.setText("Firstly, type the hostname and the port number."+System.lineSeparator()+
                "Secondly, click on StartServer."+System.lineSeparator()+"Thirdly, type your username and " +
                "click on Connect."
                +System.lineSeparator()+"Afterwards, you can chat.");
        inputTextField.setText("Please type your message here after connecting to the server.");
        btnDisconnect.setDisable(true); //set disable at the beginning of the program
        btnSendMsg.setDisable(true); //set disable at the beginning of the program
        outputTextArea.setEditable(false);
        inputTextField.setDisable(true); //set disable at the beginning of the program
        btnSendMsg.setDefaultButton(true); //activate send button by pushing enter enter button on the keyboard
    }
    // gets called when "start server" is clicked
    public void startingServer(ActionEvent actionEvent) throws IOException {
        String port = fieldToTypePortNumber.getText();

        //create Object of class server
        server = new Server(port);

        // starts server if not already started in Server.java
        if (server.isServerAlreadyStarted()) {
            outputTextArea.appendText(System.lineSeparator()+System.lineSeparator()+"SERVER IS ALREADY ON."
                    +System.lineSeparator()+ "Please click connect button to log in."
                    +System.lineSeparator());
            btnStartServer.setDisable(true);

            //if server has been already started, inform the user about this
        } else {
            btnStartServer.setDisable(true); // disable button in order to not start the server
            outputTextArea.appendText("" + System.lineSeparator() + "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + System.lineSeparator() +
                    "" + "SERVER IS READY. PLEASE CLICK CONNECTION BUTTON TO LOG IN");
        }
    }

    //the method is called when the button connect is clicked - creates user, connects to the server
    public void connectAcceptRequest(ActionEvent actionEvent) throws IOException {

        //checks whether the required fields are empty - if yes - informs the user and requires to fill in all the fields
        if (fieldToTypeUsername.getText().equals("") || fieldToTypePortNumber.getText().equals("")
                ||fieldToTypeHostName.getText().equals("")) {
            outputTextArea.appendText(System.lineSeparator()+
                    "Please fill in the required fields: username, ip address and port number."
                    +System.lineSeparator());

        //checks whether the server is already started. if not, informs the user that the server has been not started yet
        } else if (!Server.isServerOn(fieldToTypePortNumber.getText())) {
            outputTextArea.appendText(System.lineSeparator()+System.lineSeparator()+"The server is off. Please wait for " +
                    "the server to be started or start the " +
                    "server by yourself.");

        //create client and connect to server at the same time, then start a thread to read incoming messages
        } else {
            chatUser = new Client(fieldToTypeUsername.getText(), fieldToTypeHostName.getText(),
                    fieldToTypePortNumber.getText());

                //clear the output and input text fields
                outputTextArea.clear();
                inputTextField.clear();

                System.out.println("Started reading");

                //readMessage thread - needed to read in parallel the whole time
                // system outs used for test purposes only
                Thread readMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Entered run");

                        while (true) {
                            if (checkConnection()) {
                                break;
                            }
                            System.out.println("Entered while loop");
                            String msg = "Test";
                            try {
                                System.out.println("Entered try");
                                //Read the message sent to this client
                                msg = chatUser.receiveMsg(); // read from input stream
                                StringTokenizer st = new StringTokenizer(msg, "#"); // split message in userName and message
                                String sender = st.nextToken(); // get first part = userName
                                String msgToSend = st.nextToken(); // get second part = message
                                System.out.println("Message that was read: " + msg);
                                showMessage(sender + ": " + msgToSend);
                                System.out.println("After invoking showMessage Method");
                            } catch (IOException exception) {
                                //exception.printStackTrace(); //commented to avoid exception message
                            }
                        }
                    }
                });
                // start the read thread
                readMessage.start();

                //disable or enable fields and buttons after connection to the server has been established
                btnSendMsg.setDisable(false);
                btnDisconnect.setDisable(false);
                btnConnect.setDisable(true);
                inputTextField.setDisable(false);
                fieldToTypeUsername.setDisable(true);
                fieldToTypeHostName.setDisable(true);
                fieldToTypePortNumber.setDisable(true);

        }
    }

    // gets called when clicking on button "send" or pushing the enter button on the keyboard
    public void sendMessage(ActionEvent actionEvent) throws IOException {
        // if no text typed in do nothing
        if (inputTextField.getText().equals("")) {

        } else { // add message to own chatTextBox and send message to server
            System.out.println(fieldToTypeUsername.getText()+"#"+inputTextField.getText());
            chatUser.sendMsg(fieldToTypeUsername.getText()+"#"+inputTextField.getText());
        }
        inputTextField.clear(); // empty textField after message was sent
    }

    //display the incoming message on the screen
    public void showMessage(String incomingMsg) {
        System.out.println("Message delivered");
        outputTextArea.appendText(incomingMsg+System.lineSeparator()); // add incoming messages to TextArea
    }

    // checks whether a connection has been established established by looking to the buttons (enabled = connection not established)
    public boolean checkConnection() {
        if (btnDisconnect.isDisabled()) {
            return true;
        } else {
            return false;
        }

    }


    // TO-DO in case of unhandled exception call this method (not implemented yet)
    public void stopConnectionIfFailure() throws IOException {
        chatUser.stopConnection();
        System.out.println("The connection was forcefully stopped.");
        outputTextArea.appendText("Connection has been closed."+System.lineSeparator()+
                "Server is ready."+System.lineSeparator());
        btnDisconnect.setDisable(true);

    }


    // called when button "disconnect" is clicked
    public void disconnectTheSocket(ActionEvent actionEvent) throws IOException {
        chatUser.stopConnection();

        outputTextArea.appendText("Connection has been closed."+System.lineSeparator()+
                "Server is ready."+System.lineSeparator()+"Please log in or start a new server."+System.lineSeparator());
        btnDisconnect.setDisable(true);
        btnConnect.setDisable(false);
        btnSendMsg.setDisable(true);
        fieldToTypeUsername.setDisable(false);
        fieldToTypeHostName.setDisable(false);
        fieldToTypePortNumber.setDisable(false);
        btnStartServer.setDisable(false);
        System.out.println("Connection was closed with disconnect button");

    }
}
