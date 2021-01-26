package chatchatchat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

public class ClientHandler implements Runnable {
    protected Socket socket;
    protected DataInputStream dataInputStream;
    protected DataOutputStream dataOutputStream;

    public ClientHandler (Socket socket) throws IOException {
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    // in order to save client connections
    public static Vector<ClientHandler> handlers = new Vector<ClientHandler>();

    // start adding new client connection objects to the Vector list
    @Override
    public void run() {
        try {

            handlers.addElement(this);

            //read and call the broadcast method to further the messages
            while (true) {
                String msg = dataInputStream.readUTF();
                broadcast(msg);
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
            Enumeration<ClientHandler> e = handlers.elements();
            while (e.hasMoreElements()) { // if there are clients in the list -> send message
                ClientHandler c = e.nextElement();
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