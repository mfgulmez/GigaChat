import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Server class for GigaChat application.
 * 
 * Listens for incoming client connections and spawns a thread (ReadandSendThread)
 * to handle broadcasting text and image messages to all connected clients.
*/

public class Server{
    private ServerSocket sS;
    private int port=9090;
    private List<Socket> sockets;
    private List<Message> history = new ArrayList<>();

    /**
     * Private constructor to create and initialize the server socket.
    */

    private Server() {
        System.out.println("Attempt to create Server");
        try {
            InetAddress localIP = getLocalIPAddress();
            sS = new ServerSocket(port, 30, localIP);
            System.out.println("Server started at IP: " + localIP.getHostAddress());
            sS.setSoTimeout(100000000);
            System.out.println("--Server is created--");
            sockets = Collections.synchronizedList(new ArrayList<>());
        } 

        catch (UnknownHostException e) {
            System.out.println(e.getLocalizedMessage());
        } 

        catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }	
    }

    /**
     * Get the local IP address of socket by inspecting IP on 
     * connection to Google's public DNS server (8.8.8.8/100002)
     * @return
     * @throws UnknownHostException
     * @throws SocketException
    */
    
    private InetAddress getLocalIPAddress() throws UnknownHostException, SocketException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return InetAddress.getLocalHost().isLoopbackAddress() ? socket.getLocalAddress() : InetAddress.getLocalHost();
        }
    }

    /**
     * Starts listening for new client connections.
     * For each client, a new controller thread is started.
    */

    private void startServer() {
        while(true) {
            try {
                Socket s = sS.accept();
                sockets.add(s);
                System.out.println("--new client is connected--");
                ControllerThread rT = new ControllerThread(s, sockets, history);
                rT.start();  // Move sendHistory() into the thread
            } catch (IOException e) {
                System.err.println("Error accepting client: " + e.getMessage());
            }
        }
    }

    /**
     * Main method to run the server.
    */

    public static void main(String[] args) {
        Server server=new Server();
        server.startServer();
    }
}
