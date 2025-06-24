# GigaChat 💬

GigaChat is a real-time chat application built in Java using Swing for the GUI and Java Sockets for network communication. It allows users to exchange text messages, emojis, and images in a group chat setting with a visually appealing and responsive interface.

## Features

-  Multi-client support (via threads)
-  Text messaging with sender styling and alignment
-  Emoji panel with one-click emoji sending
-  Image sharing with image preview
-  Chat history with automatic replay on new connections
-  GUI built using `JFrame`, `JTextPane`, and custom layouts
-  Thread-safe broadcasting and message storage

## Project Structure

```plaintext
GigaChat/
├── Client.java            # GUI-based client application
├── Server.java            # Server handling multiple clients
├── ControllerThread.java  # Thread class for handling each client
├── Message.java           # Unified message class for text/image
├── Type.java              # Enum to represent message type
└── README.md              # Project documentation
