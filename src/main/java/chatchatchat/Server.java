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

    //basic constructor for Server object
    public Server(String portNumber) throws IOException {
        this.portNumber = portNumber;
    }

    //basic getter, not used yet
    public String getPortNumber() {
        return this.portNumber;
    }

    //starts the server when button start server was clicked
    public void startServer() throws IOException {

        serverSocket = new ServerSocket(Integer.parseInt(portNumber));

        //sytem outs are used only for testing purposes only
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

                        //create ClientHandler, which receives messages and sends them to all clients
                        ClientHandler clientHandlerObject = new ClientHandler(socket);
                        Thread threadObject = new Thread(clientHandlerObject);
                        threadObject.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        acceptClientRequests.start();
    }


    //checks if the server is started (only when the start server button was clicked)
    // => returns true if the server has been already started
    //if the server is not started, starts the server and returns false
    public boolean isServerAlreadyStarted() {
        try {
            startServer();
            return false;
        } catch (IOException e) {
            //e.printStackTrace();
            return true;
        }
    }

    //checks whether the server is started, when the button connect was clicked. If the server is off, return false
    public static boolean isServerOn(String portNumber) {
        try {
            ServerSocket ss = new ServerSocket(Integer.parseInt(portNumber));
            ss.close();
            return false;
        } catch (IOException exception) {
            return true;
        }
    }

/*
    public boolean checkIfServerIsStillActive () {
        if (serverSocket.isClosed()) {
            return false;
        } else {
            return true;
        }
    }

 */
}



// necessary to handle client requests
