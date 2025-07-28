package AdvancedIT.Testate.Testat3;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {
        final String SERVER = "localhost";
        final int PORT = 5999;

        while(true) {
            try {
                DatagramSocket clientSocket = new DatagramSocket(); // Das DatagramSocket wird hier erstellt
                Scanner scanner = new Scanner(System.in); // Scanner fürmo Benutzereingaben im Terminal

                System.out.println("Geben Sie Befehle ein im Format:");
                System.out.println(" READ <filename>,<lineNo>");
                System.out.println(" WRITE <filename>,<lineNo>,<data>");
                System.out.println(" Oder '.' zum Beenden ");
                String message = scanner.nextLine(); // Benutzereingabe lesen

                if (message.equals(".")) {
                    System.out.println("Verbindung beendet.");
                    break;
                }

                byte[] sendData = message.getBytes(); // Nachricht in Bytes umwandeln
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(SERVER), PORT); // DatagramPacket zum Senden der Nachricht erstellen
                clientSocket.send(sendPacket); // Nachricht senden

                byte[] receiveData = new byte[1024]; // Puffer für die Antwort des Servers
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); // DatagramPacket zum Empfangen der Antwort erstellen
                clientSocket.receive(receivePacket); // Antwort empfangen

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength()); // Antwort in String umwandeln
                System.out.println("Antwort vom Server: " + response);

                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
