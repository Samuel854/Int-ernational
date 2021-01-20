package chatchatchat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

//the class should have no subclasses
public final class Client {

    private static Thread readMessage;
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
        int x = Integer.parseInt(portNumber);
        this.socket = new Socket(ip,x);   // create new socket
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());

        System.out.println("User connected to Server");
    }

    //to get the name of the user
    public String getUsername() {
        return username;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public String getPortNumber() {
        return portNumber;
    }

    /*
    //calls this method if click Connect to connect the client to the server
    public void connectToServer() throws IOException {
        InetAddress ip = InetAddress.getByName(ipAddress);
        int x = Integer.parseInt(portNumber);
        socket = new Socket(ip,x);   // create new socket
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        System.out.println("User connected to Server");
    }

     */


    // read from inputStream and return (gets called in read Thread)
    //is a static method
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
            //delete the object from the list
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
