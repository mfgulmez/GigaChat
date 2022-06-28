import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.Thread;
public class Server{
	
	private InetAddress iA;
    private ServerSocket sS;
    private int port=9090;
    
public Server() {
	System.out.println("trying to create Server");
	try {
		iA=InetAddress.getLocalHost();
		System.out.println(iA);
		sS=new ServerSocket(port,30,iA);
		sS.setSoTimeout(100000000);
		System.out.println("--Server is created--");
	
	} catch (UnknownHostException e) {
	} catch (IOException e) {
	}	
	
	
}
public void startServer() {
	List<Socket> sockets=new ArrayList<Socket>();
	while(true) {
		try {
				    Socket s=sS.accept();
					sockets.add(s);
				    System.out.println("--new client is connected--");
					ReadandSendThread rT=new ReadandSendThread(s,sockets);
					rT.start();
		} catch (IOException e) {
		}
	}
}
public static void main(String[] args) {
	Server server=new Server();
	server.startServer();
}
}
class ReadandSendThread extends Thread{
	List<Socket>sockets;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	ReadandSendThread(Socket s,List<Socket>sck){
		socket=s;
	    sockets=sck;
	}
	public void run() {
		try {
			if(socket.isConnected()) {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String msg="";
				while(msg!="over") {
					msg=in.readLine();
					for(int i=0;i<sockets.size();i++) {
						out = new PrintWriter(sockets.get(i).getOutputStream(), true);
						out.println(msg);
					}
					System.out.println(msg);
					
					
			}}}
		 catch (IOException e) {
			 System.out.println(e);
		}
	}
}