package chatchatchat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


//the class should have no subclasses - not really necessary to implement this feature
//each time the user connects to the server, an object is created
public final class Client {

    private final String username;
    private final String portNumber;
    private final String ipAddress;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Socket socket;

    //basic constructor
    public Client(String username, String ipAddress, String portNumber) throws IOException {
        this.username=username;
        this.portNumber = portNumber;
        this.ipAddress = ipAddress;

        InetAddress ip = InetAddress.getByName(ipAddress);
        this.socket = new Socket(ip, Integer.parseInt(portNumber));   // create new socket

        //create input and out streams
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());

        System.out.println("User connected to Server");

    }

    //getters are not used in our application - can be used when extending the app
    public String getUsername() {
        return username;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public String getPortNumber() {
        return portNumber;
    }

    // read from inputStream and return String (gets called in read Thread)
    public String receiveMsg() throws IOException {
        return dataInputStream.readUTF();
    }

    //send Message to socket
    public void sendMsg(String msg) throws IOException {
        dataOutputStream.writeUTF(msg); // write to outputStream
        dataOutputStream.flush(); // empty output stream
    }

    //disconnect from the server after the disconnect button was clicked
    public void stopConnection() {
        try {
            socket.close();
            dataInputStream.close();
            dataOutputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
