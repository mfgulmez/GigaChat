/**
 * Message class for GigaChat application.
 * 
 * Represents a chat message which can be either a text or an image.
 * Stores metadata including sender name, timestamp, and content.
 * Depending on the message type, only relevant fields (textContent or imageData/imageSize) are populated.
*/

class Message {
    final Type type;
    final String sender;
    final long timestamp;

    final String textContent;

    final byte[] imageData;
    final int imageSize;

    /**
     * Private constructor to create and initialize message with type text
    */

    Message(String sender, String content) {
        this.type = Type.TEXT;
        this.sender = sender;
        this.textContent = content;
        this.timestamp = System.currentTimeMillis();
        this.imageData = null;
        this.imageSize = 0;
    }

    /**
     * Private constructor to create and initialize message with type image
    */

    Message(String sender, byte[] imageData, int size) {
        this.type = Type.IMAGE;
        this.sender = sender;
        this.imageData = imageData;
        this.imageSize = size;
        this.timestamp = System.currentTimeMillis();
        this.textContent = null;
    }
}