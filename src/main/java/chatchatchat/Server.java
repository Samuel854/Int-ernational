package chatchatchat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

public class Server {
    public ServerSocket serverSocket;
    private final String portNumber;

    public Server(String portNumber) throws IOException {
        this.portNumber = portNumber;
        /*
        int portNumberServer = Integer.parseInt(portNumber);
        this.serverSocket=new ServerSocket(portNumberServer);
        System.out.println(Inet4Address.getLocalHost().getHostAddress());
        System.out.println("Server Socket created.");

         */
    }
    public String getPortNumber() {
        return this.portNumber;
    }
    public void startServer() throws IOException {
        /* ServerSocket serverSocket;

        */

        //int portNumberServer = Integer.parseInt(portNumber);
        //if (isServerAlreadyStarted(portNumberServer))

        // new serverSocket with portNumber from textField
        serverSocket = new ServerSocket(Integer.parseInt(portNumber));

        System.out.println(Inet4Address.getLocalHost().getHostAddress());
        System.out.println("Server Socket created.");


        // thread in order to handle client requests
        Thread acceptClientRequests = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Socket socket = null;
                    try {
                        socket = serverSocket.accept(); // accept new incoming connections

                        System.out.println("New client request received: " + socket.getInetAddress());
                        //erstelle ClientHandler, der Nachrichten empfängt und an alle weiterleitet
                        ClientHandler c = new ClientHandler(socket);
                        Thread t = new Thread(c);
                        t.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        acceptClientRequests.start();
    }


    // checks if the server is started by looking to the buttons (enabled = server not started)
    public boolean isServerAlreadyStarted() {
        try {
            startServer();
            return false;
        } catch (IOException e) {
            //e.printStackTrace();
            return true;
        }
    }


    public boolean checkIfServerIsStillActive () {
        if (serverSocket.isClosed()) {
            return false;
        } else {
            return true;
        }
    }


}



// needed in order to handle client requests
class ClientHandler implements Runnable{
protected Socket socket;
protected DataInputStream dataInputStream;
protected DataOutputStream dataOutputStream;
protected String username;

    public ClientHandler (Socket socket) throws IOException {
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    // in order to save client connections
    public static Vector<ClientHandler> handlers = new Vector();

    // start adding new client connection objects to the Vector list
    @Override
    public void run() {
        try {

            handlers.addElement(this);
            while (true) {
                //try {
                String msg = dataInputStream.readUTF();
                broadcast(msg);

                //} catch (IOException exception) {
                //exception.printStackTrace();
                //  handlers.removeElement(this);
                //  }
            }
        } catch (IOException exception){
            System.out.println("Cannot read any more, the connection should be closed.");
        } finally { //if there is an exception remove the client object from the list and close socket connection
            handlers.removeElement(this);
            try {
                socket.close();
                dataInputStream.close();
                dataOutputStream.close();
                System.out.println("Connection was closed, removed from vector");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // send message to all connected clients in the list
    protected static void broadcast( String msg) {
        synchronized (handlers) {
            Enumeration e = handlers.elements();
            while (e.hasMoreElements()) { // if there are clients in the list -> send message
                ClientHandler c = (ClientHandler) e.nextElement();
                try {
                    synchronized (c.dataOutputStream) {
                        c.dataOutputStream.writeUTF(msg);
                    }
                    c.dataOutputStream.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

    }
}