package AdvancedIT.Testate.Testat3;
import java.net.*;

public class Request {
    String message;
    InetAddress address;
    int port;
    DatagramSocket socket;

    public Request(String message, InetAddress address, int port, DatagramSocket socket) {
        this.message = message;
        this.address = address;
        this.port = port;
        this.socket = socket;
    }

    public String getCommand() {
        // Logik zum Extrahieren des Kommandos aus der Nachricht
        System.out.println("getCommand: " + message.split(" ", 2)[0]);
        return message.split(" ", 2)[0]; // Erstes Element vor dem Leerzeichen
    }

    public String getFilename() {
        // Erst den Command-Teil ("READ " oder "WRITE ") entfernen
        String afterCommand = message.substring(message.indexOf(' ') + 1);
        // Dann das erste Element vor dem Komma nehmen
        System.out.println("getFilename: " + afterCommand.split(",")[0]);
        return afterCommand.split(",")[0];
    }

    public String getLineNumber() {
        // Erst den Command-Teil ("READ " oder "WRITE ") entfernen
        String afterCommand = message.substring(message.indexOf(' ') + 1);
        // Dann das zweite Element nach dem ersten Komma nehmen
        System.out.println("getLineNumber: " + afterCommand.split(",")[1]);
        return afterCommand.split(",")[1];
    }

    public DatagramSocket getSocket() {
        return socket;
    }
    
    public String getData() {
        // Erst den Command-Teil ("WRITE ") entfernen
        String command = message.substring(message.indexOf(' ') + 1);
        // Dann bei Kommata splitten, nur die ersten 3 Teile
        String[] parts = command.split(",", 3);
        if (parts.length >= 3) {
            System.out.println("getData: " + parts[2]);
            return parts[2];
        }
        return "";
    }
}
