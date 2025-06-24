import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.Thread;

/**
 * Thread class responsible for handling a single client's communication.
 * 
 * Reads messages from the client and broadcasts them (text or image) to all others.
*/

class ControllerThread extends Thread{
    private List<Socket> sockets;
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private List<Message> history;

    /**
     * Constructor initializes data stream and stores client socket and shared socket list.
     * 
     * @param s    Socket connected to a specific client
     * @param sck  List of all active client sockets
    */

    public ControllerThread(Socket s, List<Socket> sockets, List<Message> history) {
        this.socket = s;
        this.sockets = sockets;
        this.history = history;

        try {
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Continuously listens for text or image data from the connected client.
     * On receiving data, broadcasts it to all clients.
    */

    @Override
    public void run() {
        try {
            sendHistory();
            while (true) {
                String header = dataIn.readUTF();

                if ("txt".equals(header)) {
                    String msg = dataIn.readUTF();
                    String sender = null;
                    if(msg.contains(":")){
                        sender = msg.split(":", 2)[0]; 
                    }
             
                    Message message = new Message(sender, msg);

                    synchronized (history) {
                        history.add(message);
                    }

                    broadcastText(msg);
                    log("[Text] " + msg);
                } 
                else if ("img".equals(header)) {
                    String sender = dataIn.readUTF();
                    int size = dataIn.readInt();
                    byte[] imageData = new byte[size];
                    dataIn.readFully(imageData);
                    Message imageMessage = new Message(sender, imageData, size);

                    synchronized (history) {
                        history.add(imageMessage);
                    }

                    broadcastImage(sender, imageData, size);
                    log("[Image] from " + sender + ", size = " + size);
                }
            }
        } 
        
        catch (IOException e) {
            log("Client disconnected: " + socket.getRemoteSocketAddress());
            synchronized (sockets) {
                sockets.remove(socket);
            }
            closeResources();
        }
    }

    /**
     * Sends a text message to all connected clients.
     * 
     * @param msg the message to broadcast
    */

    private void broadcastText(String msg) {
        for (Socket client : sockets) {
            try {
                DataOutputStream clientOut = new DataOutputStream(client.getOutputStream());
                clientOut.writeUTF("txt");
                clientOut.writeUTF(msg);
                clientOut.flush();
            } catch (IOException e) {
                sockets.remove(client);
            }
        }
    }

    /**
     * Sends an image message to all connected clients.
     * 
     * @param sender     name of the client sending the image
     * @param imageData  byte array of image data
     * @param size       size of the image data
    */

    private void broadcastImage(String sender, byte[] imageData, int size) {
        for (Socket client : sockets) {
            try {
                DataOutputStream clientOut = new DataOutputStream(client.getOutputStream());
                clientOut.writeUTF("img");
                clientOut.writeUTF(sender);
                clientOut.writeInt(size);
                clientOut.write(imageData);
                clientOut.flush();
            } catch (IOException e) {
                sockets.remove(client);
            }
        }
    }

    /**
     * Orders the message queries by timestamp with
     * writing appropriately regarding their type
     * @throws IOException
    */

    public void sendHistory() throws IOException {
        List<Message> ordered;
        synchronized (history) {
            ordered = new ArrayList<>(history);
        }
        ordered.sort(Comparator.comparingLong(m -> m.timestamp));

        for (Message message : ordered) {
            try{
                if (message.type == Type.TEXT) {
                    dataOut.writeUTF("txt");
                    dataOut.writeUTF(message.textContent);
                } 
                else if (message.type == Type.IMAGE) {
                    dataOut.writeUTF("img");
                    dataOut.writeUTF(message.sender);
                    dataOut.writeInt(message.imageSize);
                    dataOut.write(message.imageData);
                }
            }
            catch (IOException e) {
                log("Error sending history to client: " + e.getMessage());
                throw e;
            }
        }
        dataOut.flush();
    }

    /**
     * Closing streams and socket for preventing data issues.
    */

    private void closeResources() {
        try {
            if (dataIn != null) dataIn.close();
            if (dataOut != null) dataOut.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            log("Error closing resources: " + e.getMessage());
        }
    }

    /**
     * Logging for light-weight data consistency
     * @param msg
    */

    private synchronized void log(String msg) {
        System.out.println("[Server] " + msg);
        System.out.flush();
    }
}