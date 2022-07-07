import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;

public class Client{
private String addr;
private Socket s;
//Declare the client socket and IP address of the server that client will connect to.
private JFrame frame=new JFrame();
private JLabel heading=new JLabel();
private JTextPane messageArea=new JTextPane();
private JTextField messageInput=new JTextField();
private JPanel inputPanel=new JPanel();
private JButton imageButton=new JButton("Send Image");
private JButton emojiButton = new JButton("😀");
Icon errorIcon=UIManager.getIcon("OptionPane.errorIcon");
StyledDocument doc=messageArea.getStyledDocument();
Style style=messageArea.addStyle("", null);
//Declare the components and style of texts.
private String name;
private BufferedReader in;
private PrintWriter out;
//Declare reader and writer for text stream.
public Client() {
	    name=JOptionPane.showInputDialog(null,"Enter your name:","GigaChat",JOptionPane.PLAIN_MESSAGE);
		addr=JOptionPane.showInputDialog(null,"Enter the IP address:","GigaChat",JOptionPane.PLAIN_MESSAGE);
			try {
				InetAddress iA=InetAddress.getByName(addr);
				s = new Socket (iA,9090);
				//The client socket is connected to the server socket.
			out = new PrintWriter(s.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out.println("--"+name+" has joined the chat--");
			if(s!=null) {
				actionEvents();
				keyEvents();
				CreateGui();
				read();
				//Read text stream coming from server and create GUI if socket is not empty.
			}}
			catch (UnknownHostException e) {
			  System.out.println(e);
		   } 
			catch (IOException e) {
			System.out.println(e);
			JOptionPane.showOptionDialog(null, "Cannot connect to the Server!","GigaChat",JOptionPane.PLAIN_MESSAGE, 1, errorIcon,null,0);	
			frame.dispose();
		   }
		} 
	

private void CreateGui() {
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(600,700);
	frame.setTitle("GigaChat");
	Icon messangerIcon=new ImageIcon("C:\\Users\\Fatih\\eclipse-workspace\\GigaChat\\src\\messangerIcon.png");
	heading.setBounds(0, 0, 800, 200);
	heading.setBackground(new Color(132, 66, 245));
	heading.setForeground(new Color(66, 245, 215));
	heading.setIcon(messangerIcon);
	heading.setOpaque(true);
	heading.setText("GigaChat");
	heading.setIconTextGap(45);
	heading.setHorizontalAlignment(JLabel.LEFT);
	heading.setFont(new Font("Helvetica", Font.BOLD, 50));
	messageArea.setEditable(false);
	messageArea.setFont(new Font("Helvetica", Font.PLAIN, 20));
	JScrollPane pane=new JScrollPane(messageArea);
	messageInput.addKeyListener(null);
	messageInput.setFont(new Font("Helvetica", Font.PLAIN,20));
	inputPanel.setLayout(new BorderLayout());
	inputPanel.add(messageInput,BorderLayout.CENTER);
	inputPanel.add(imageButton,BorderLayout.EAST);
	inputPanel.add(emojiButton,BorderLayout.WEST);
	frame.setLayout(new BorderLayout());
	frame.add(heading,BorderLayout.NORTH);
	frame.add(pane,BorderLayout.CENTER);
	frame.add(inputPanel,BorderLayout.SOUTH);
	frame.setVisible(true);
	//GUI components are initialized and located 
}

public void read() {
	try {
         while(s.isConnected()) {
			String msg=in.readLine();
			String name=new String();
			boolean welcome=true;
			int i=0;
			for(int j=0;j<msg.length();j++) {
				if(msg.charAt(j)==':') {
					welcome=false;
				}
			}
			try {
				if(welcome==true) {
					StyleConstants.setForeground(style,Color.magenta);
					doc.insertString(doc.getLength(), msg+"\n", style);
					//Style non-message stream
				}
				
				else {
					while(msg.charAt(i)!=':'&&i<msg.length()) {
						name=name+msg.charAt(i);
						i++;
					}
					//Separate the message stream from the text stream coming from the server.
					msg=msg.substring(i,msg.length());
				StyleConstants.setForeground(style,Color.blue);
				doc.insertString(doc.getLength(), name, style);
				StyleConstants.setForeground(style,Color.black);
				doc.insertString(doc.getLength(), msg+"\n", style);}}
			//Style message stream.
         catch (BadLocationException e) {
				e.printStackTrace();
		 } 
     }
         }
     catch (IOException e) {
			e.printStackTrace();
		}
}
public void keyEvents() {
	 messageInput.addKeyListener(new KeyListener() {
		 String contentToSend;
	    	@Override
	    	public void keyTyped(KeyEvent e) {
	    	}

	    	@Override
	    	public void keyPressed(KeyEvent e) {
	    	}

	    	@Override
	    	public void keyReleased(KeyEvent e) {
	    		
	    		if(e.getKeyCode()==10) {
	    	        contentToSend=messageInput.getText();
	    	        if(!contentToSend.isEmpty()) {
	    		    out.println(name+":"+contentToSend);
	    	    	out.flush();
	    			messageInput.setText("");
    	    		messageInput.requestFocus();
	    		}}
			//A thread for message to sent to server if enter key released.
	    	}});
}

	private JFrame selectframe;
	private ImageIcon icon;
	private JLabel pic;
	private JButton send;
	private JFrame emojiFrame;
	private JPanel p1,p2,p3;
	private JButton smile= new JButton("😀");
	private JButton sad= new JButton("😭");
	private JButton bruh= new JButton("😳");
	private JButton evil=new JButton("😈");
	private JButton angry=new JButton("😡");
	private JButton shit=new JButton("💩");
	private JButton sleepy=new JButton("😴");
	private JButton cool=new JButton("😎");
	private JButton nervous=new JButton("😬");
	//Emoji buttons are initialized.
void actionEvents() {
	imageButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JFileChooser filechooser = new JFileChooser();
			int response = filechooser.showOpenDialog(null); //select file to open
			if(response == JFileChooser.APPROVE_OPTION) {
				File file = new File(filechooser.getSelectedFile().getAbsoluteFile().toString(), addr);
				selectframe = new JFrame("image");
				selectframe.setSize(400,400);
				selectframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				StringBuffer str = new StringBuffer(file.toString());
				int strlength = str.length();	
				str.delete(strlength-14,strlength);
				String strr = str.toString();
				icon = new ImageIcon(strr);
				pic = new JLabel(icon);
				send = new JButton("Send");
				selectframe.add(pic, BorderLayout.CENTER);
				selectframe.add(send, BorderLayout.SOUTH);
				selectframe.setVisible(true);
				send.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent a) {
						try {
							OutputStream outputStream = s.getOutputStream();
							BufferedOutputStream bos = new BufferedOutputStream(outputStream);
							
							Image image = icon.getImage();
							
							BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
							Graphics graphics = bufferedImage.createGraphics();
							graphics.drawImage(image, 0, 0, null);
							graphics.dispose();
							ImageIO.write(bufferedImage, "png", bos);
							bos.close();
						} catch(IOException z) {
							z.printStackTrace();
						}
						
					}
					
				});
				
			}
			
		}
		//Thread for sending images to server (needs collaboration).
	});
	emojiButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent b) {
			emojiFrame = new JFrame("EMOJIS");
			emojiFrame.setSize(200,200);
			emojiFrame.setVisible(true);
			p1=new JPanel();
			p1.setFont(new Font("Helvetica", Font.BOLD, 20));
			p1.add(smile,BorderLayout.EAST);
			p1.add(sad,BorderLayout.CENTER);
			p1.add(bruh,BorderLayout.WEST);
			p2=new JPanel();
			p2.setFont(new Font("Helvetica", Font.BOLD, 20));
			p2.add(evil,BorderLayout.WEST);
			p2.add(angry,BorderLayout.CENTER);
			p2.add(shit,BorderLayout.EAST);
			p3=new JPanel();
			p3.add(sleepy,BorderLayout.WEST);
			p3.add(cool,BorderLayout.CENTER);
			p3.add(nervous,BorderLayout.EAST);
			p3.setFont(new Font("Helvetica", Font.BOLD, 20));
			emojiFrame.setLayout(new BorderLayout());
			emojiFrame.add(p1,BorderLayout.NORTH);
			emojiFrame.add(p3,BorderLayout.SOUTH);
			emojiFrame.add(p2,BorderLayout.CENTER);
			emojiFrame.pack();
			//Emoji buttons are located and packed in a frame.
		}});
	smile.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent c) {
			out.println(name+":😀");
		}
	});
	sad.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent c) {
			out.println(name+":😭");
		}
	});
	bruh.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent c) {
			out.println(name+":😳");
		}
	});
	evil.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent c) {
			out.println(name+":😈");
		}
	});
	angry.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent c) {
			out.println(name+":😡");
		}
	});
	shit.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent c) {
			out.println(name+":💩");
		}
	});
	sleepy.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent c) {
			out.println(name+":😴");
		}
	});
	cool.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent c) {
			out.println(name+":😎");
		}
	});
	nervous.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent c) {
			out.println(name+":😬");
		}
		//A thread for sending emojis to server when button is clicked.
	});
}

public static void main(String[] args) {
	
Client cT=new Client();

}
}
