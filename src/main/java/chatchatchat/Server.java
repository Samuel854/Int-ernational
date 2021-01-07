package chatchatchat;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

public class Server {

    public static void startServer(String portNumber) throws IOException {
        ServerSocket ss;
        int x = Integer.parseInt(portNumber);
        ss = new ServerSocket(x); // new serverSocket with portNumber from textField
        System.out.println("Server Socket created.");

        // threads in order to handle client requests
        Thread ok = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Socket s = null;
                    try {
                        s = ss.accept(); // accept new incoming connections
                        System.out.println("New client request received: " + s.getInetAddress());
                        //erstelle ClientHandler, der Nachrichten empfängt und an alle weiterleitet
                        ClientHandler c = new ClientHandler(s);
                        Thread t = new Thread(c);
                        t.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        ok.start();
    }

    // checks if the server is started by looking to the buttons (enabled = server not started)
    public static boolean isServerAlreadyStarted(String portnumber) {
        try {
            startServer(portnumber);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }
}

// needed in order to handle client requests
class ClientHandler implements Runnable{
protected Socket s;
protected DataInputStream i;
protected DataOutputStream o;
protected String username;

    public ClientHandler (Socket s) throws IOException {
        this.s = s;
        this.i = new DataInputStream(s.getInputStream());
        this.o = new DataOutputStream(s.getOutputStream());
    }

    // in order to save client connections
    public static Vector<ClientHandler> handlers = new Vector();

    // start adding new client connection objects to the Vector list
    @Override
    public void run() {
        try {
            handlers.addElement(this);
            while (true) {
                String msg = i.readUTF();
                broadcast(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally { //if there is an exception remove the client object from the list and close socket connection
            handlers.removeElement(this);
            try {
                s.close();
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
                    synchronized (c.o) {
                        c.o.writeUTF(msg);
                    }
                    c.o.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

    }
}