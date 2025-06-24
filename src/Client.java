import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;

/**
 * A GUI-based chat client with support for sending and receiving text, emojis, and images.
 * 
 * Connects to a server via socket and allows rich message exchange using Java Swing.
*/

public class Client{
	private Socket s;

	private JFrame frame=new JFrame();
	private JFrame emojiFrame;

	private JLabel heading=new JLabel();
	private JTextPane messageArea=new JTextPane();
	private JTextField messageInput=new JTextField();
	private JPanel inputPanel=new JPanel();
	private JPanel p1,p2,p3;

	private JButton imageButton=new JButton("Send Image");
	private JButton emojiButton = new JButton("😀");
	private JButton smile= new JButton("😀");
	private JButton sad= new JButton("😭");
	private JButton bruh= new JButton("😳");
	private JButton evil=new JButton("😈");
	private JButton angry=new JButton("😡");
	private JButton shit=new JButton("💩");
	private JButton sleepy=new JButton("😴");
	private JButton cool=new JButton("😎");
	private JButton nervous=new JButton("😬");

	Icon errorIcon=UIManager.getIcon("OptionPane.errorIcon");
	StyledDocument doc=messageArea.getStyledDocument();
	Style style=messageArea.addStyle("", null);

	SimpleAttributeSet leftAlign = new SimpleAttributeSet();
	SimpleAttributeSet rightAlign = new SimpleAttributeSet();
	SimpleAttributeSet centerAlign = new SimpleAttributeSet();

	private String name;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;

	/**
	 * Sets initial page, initializes GUI and connects the client to the server.
	*/

	private void start() {
		name = JOptionPane.showInputDialog(null, "Enter your name:", "GigaChat", JOptionPane.PLAIN_MESSAGE);
		if (name == null || name.trim().isEmpty()) {
			JOptionPane.showOptionDialog(null, "You should enter a name", "GigaChat", JOptionPane.PLAIN_MESSAGE, 1, errorIcon, null, 0);	
			return;
		}

		String ipAddress = JOptionPane.showInputDialog(null, "Enter server IP address:", "GigaChat", JOptionPane.PLAIN_MESSAGE);
		if (ipAddress == null || ipAddress.trim().isEmpty()) {
			JOptionPane.showOptionDialog(null, "You should enter a valid IP address", "GigaChat", JOptionPane.PLAIN_MESSAGE, 1, errorIcon, null, 0);	
			return;
		}

		try {
			s = new Socket(ipAddress, 9090);
			dataOut = new DataOutputStream(s.getOutputStream());
			dataIn = new DataInputStream(s.getInputStream());
			dataOut.writeUTF("txt");
			dataOut.writeUTF("-- " + name + " has joined the chat --");

			if (s != null) {
				actionEvents();
				keyEvents();
				generateMainPage();
				read();
			}
		} catch (UnknownHostException e) {
			System.out.println(e);
			JOptionPane.showOptionDialog(null, "Unknown host: " + ipAddress, "GigaChat", JOptionPane.PLAIN_MESSAGE, 1, errorIcon, null, 0);	
			frame.dispose();
		} catch (IOException e) {
			System.out.println(e);
			JOptionPane.showOptionDialog(null, "Cannot connect to the Server at " + ipAddress, "GigaChat", JOptionPane.PLAIN_MESSAGE, 1, errorIcon, null, 0);	
			frame.dispose();
		}
	}


    /**
     * Builds and displays the main GUI frame.
    */

	private void generateMainPage() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,700);
		frame.setTitle("GigaChat");
		Icon messangerIcon=new ImageIcon("resources/app/messangerIcon.png");
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
	}

	/**
     * Continuously reads incoming messages from the server.
     * Handles both text and image messages.
    */

	private void read() {
		try {
			while(true) {
				String header = dataIn.readUTF();
				if("txt".equals(header)) {
					String msg = dataIn.readUTF();
					SwingUtilities.invokeLater(() -> processTextMessage(msg));
				} 
				else if("img".equals(header)) {
					String clientName = dataIn.readUTF();
					int size = dataIn.readInt();
					byte[] imageData = new byte[size];
					dataIn.readFully(imageData);
					SwingUtilities.invokeLater(() -> displayImage(clientName, imageData));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parsing the message to extract sender and content to 
	 * style different parts and different kinds of messages
	 * @param msg
	 */

	private void processTextMessage(String msg) {
		boolean welcome = true;
		int i = 0;
		if (msg.contains(":")) {
			welcome = false;
		}
		try {
			if (welcome) {
				Style welcomeStyle = doc.addStyle("welcome", null);
				StyleConstants.setForeground(welcomeStyle, Color.magenta);
				
				SimpleAttributeSet alignCenter = new SimpleAttributeSet();
				StyleConstants.setAlignment(alignCenter, StyleConstants.ALIGN_CENTER);

				doc.setParagraphAttributes(doc.getLength(), 1, alignCenter, false);
				doc.insertString(doc.getLength(), msg + "\n", welcomeStyle);
			} 
			
			else {
				String clientName = new String();
				while (msg.charAt(i) != ':' && i < msg.length()) {
					clientName = clientName + msg.charAt(i);
					i++;
				}

				msg = msg.substring(i, msg.length());
				Style nameStyle = doc.addStyle("name", null);
				StyleConstants.setForeground(nameStyle, Color.blue);
				
				Style msgStyle = doc.addStyle("message", null);
				StyleConstants.setForeground(msgStyle, Color.black);
				
				SimpleAttributeSet align = new SimpleAttributeSet();
				StyleConstants.setAlignment(
					align, 
					name.equals(clientName) ? 
						StyleConstants.ALIGN_RIGHT : 
						StyleConstants.ALIGN_LEFT
				);
				doc.setParagraphAttributes(doc.getLength(), 1, align, false);
				
				doc.insertString(doc.getLength(), clientName, nameStyle);
				doc.insertString(doc.getLength(), wrapText(msg, 30) + "\n", msgStyle);
			}
			messageArea.setCaretPosition(messageArea.getDocument().getLength());
			messageInput.requestFocusInWindow();
		} 
		
		catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
     * Displays received image in chat pane with optional client name.
    */
	
	private void displayImage(String clientName, byte[] imageData) {
		try {
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

			Style imageStyle = doc.addStyle("image", null);
			StyleConstants.setIcon(imageStyle, new ImageIcon(image));

			Style captionStyle = doc.addStyle("caption", null);
			StyleConstants.setForeground(captionStyle, Color.red);

			SimpleAttributeSet align = new SimpleAttributeSet();
			StyleConstants.setAlignment(
				align, 
				name.equals(clientName) ? 
					StyleConstants.ALIGN_RIGHT : 
					StyleConstants.ALIGN_LEFT
			);

			doc.setParagraphAttributes(doc.getLength(), 1, align, false);
			doc.insertString(doc.getLength(), "\n", imageStyle);
			doc.insertString(doc.getLength(), "-- Image from " + clientName + " --\n", captionStyle);
			doc.insertString(doc.getLength(), "\n", style);

			messageInput.setText("");
			messageInput.requestFocus();

			StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);
			doc.setParagraphAttributes(doc.getLength(), 1, rightAlign, false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Wrap message texts to be appeared in a fixed width size
	 * by adding new line afterwards
	 * @param text raw text to be reshaped
	 * @param maxLineLength sets the limit width size to be applied
	 * @return
	*/

	private String wrapText(String text, int maxLineLength) {
		StringBuilder result = new StringBuilder();
		int i = 0;
		while (i < text.length()) {
			int end = Math.min(i + maxLineLength, text.length());
			result.append(text, i, end).append("\n");
			i = end;
		}
		return result.toString();
	}

    /**
     * Handles sending text messages when Enter key is released.
    */

	private void keyEvents() {
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
						try {
							dataOut.writeUTF("txt");
							dataOut.writeUTF(name + ":" + contentToSend);
							dataOut.flush();
						}
						catch (IOException ex) {
							ex.printStackTrace();
						}
						messageInput.setText("");
						messageInput.requestFocus();
					}}
		
				}});
	}

    /**
     * Handles action listeners for buttons: image upload, emoji sending.
    */

	private void actionEvents() {
		imageButton.addActionListener(_ -> handleImageSelection());
		emojiButton.addActionListener(_ -> generateEmojiPanel());
        smile.addActionListener(_ -> sendEmoji("😀"));
        sad.addActionListener(_ -> sendEmoji("😭"));
        bruh.addActionListener(_ -> sendEmoji("😳"));
        evil.addActionListener(_ -> sendEmoji("😈"));
        angry.addActionListener(_ -> sendEmoji("😡"));
        shit.addActionListener(_ -> sendEmoji("💩"));
        sleepy.addActionListener(_ -> sendEmoji("😴"));
        cool.addActionListener(_ -> sendEmoji("😎"));
        nervous.addActionListener(_ -> sendEmoji("😬"));
	}

    /**
     * Builds and displays image selection panel that enables 
	 * user to browse images using file chooser.
    */

	private void handleImageSelection(){
		JFileChooser fileChooser = new JFileChooser();
		int response = fileChooser.showOpenDialog(null);
		if (response == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			JFrame selectFrame = new JFrame("Image Preview");
			selectFrame.setSize(400, 400);
			selectFrame.setLayout(new BorderLayout());
			selectFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 

			try {

				BufferedImage preprocessedImage = preprocessImage(file);
				JLabel pic = new JLabel(new ImageIcon(preprocessedImage));
				
				JButton send = new JButton("Send");
				selectFrame.add(pic, BorderLayout.CENTER);
				selectFrame.add(send, BorderLayout.SOUTH);
				selectFrame.setVisible(true);
				
				String fileName = file.getName();
				send.addActionListener(_ -> handleSendingImage(fileName, preprocessedImage, selectFrame));
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Error loading image: " + ex.getMessage());
			}
		}
	}
	
	/**
	 * Returns a scaled version of the input image that fits within a 360x360 square,
	 * maintaining the aspect ratio while integrating bicubic interpolation with maintaining quality
	 * to get a 
	 * more realistic and good quality result
	 * @param imageFile file of the original image to be resized
	 * @return the scaled Image that best fits within 360x360 while preserving aspect ratio
	*/

	private BufferedImage preprocessImage(File imageFile){
		BufferedImage originalImage;
		try {
			originalImage = ImageIO.read(imageFile);
			int width = originalImage.getWidth();
			int height = originalImage.getHeight();

			int scaledWidth = 480;
			int scaledHeight = 480;

			if (width > height) {
				scaledHeight = (int) (height * 480.0 / width);
			} else if (height > width) {
				scaledWidth = (int) (width * 480.0 / height);
			}

			BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = scaledImage.createGraphics();

			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2d.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
			g2d.dispose();

			return scaledImage;
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
     * Handling image to be sent by adding it to output stream with 
	 * a generic format (e.g. PNG)
    */
	
	private void handleSendingImage(String fileName, BufferedImage bufferedImage, JFrame selectFrame){
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        	ImageIO.write(bufferedImage, "png", baos);
        	byte[] imageData = baos.toByteArray();

			dataOut.writeUTF("img");
			dataOut.writeUTF(name);
			dataOut.writeInt(imageData.length);
			dataOut.write(imageData);
			dataOut.flush();
			
			JOptionPane.showMessageDialog(selectFrame, "Image sent successfully!");
			selectFrame.dispose();
		} 
			
		catch (IOException ex) {
			JOptionPane.showMessageDialog(selectFrame, "Error sending image: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

    /**
     * Builds and displays the emoji panel.
    */

    private void generateEmojiPanel(){
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
	}

    /**
     * Helper method to send emoji as text message.
    */

    private void sendEmoji(String emoji) {
        try {
            dataOut.writeUTF("txt");
            dataOut.writeUTF(name + ":" + emoji);
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
     * Launches the client application.
    */

	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}
}
