package AdvancedIT.Serverprogrammierung.UDP;

import java.net.*;

public class UDPServer {
    public static void main(String[] args) {
        final int PORT = 9876;
        byte[] buffer = new byte[1024]; // Puffer für empfangene Daten

        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT); // DatagramSocket auf dem angegebenen Port erstellen
            System.out.println("UDP-Server läuft auf Port " + PORT);

            while (true) {
                DatagramPacket empfangen = new DatagramPacket(buffer, buffer.length); // DatagramPacket zum Empfangen der Nachricht erstellen
                serverSocket.receive(empfangen);  // Empfangen der Nachricht

                String nachricht = new String(empfangen.getData(), 0, empfangen.getLength()); // Nachricht in String umwandeln
                System.out.println("Empfangen: " + nachricht);

                String antwort = "Antwort: " + nachricht.toUpperCase();
                byte[] antwortBytes = antwort.getBytes(); // Antwort in Bytes umwandeln

                DatagramPacket senden = new DatagramPacket(
                        antwortBytes,
                        antwortBytes.length,
                        empfangen.getAddress(),
                        empfangen.getPort()
                ); // DatagramPacket zum Senden der Antwort erstellen

                serverSocket.send(senden);  // Antwort senden
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
