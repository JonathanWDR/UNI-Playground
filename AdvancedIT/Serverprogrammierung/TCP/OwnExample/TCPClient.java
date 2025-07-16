package AdvancedIT.Serverprogrammierung.TCP.OwnExample;

import java.net.*;
import java.io.*;

public class TCPClient {
    public static final int PORT = 7777;
    public static final String HOST = "localhost";

    public static void main(String[] args) {
        Socket s = null; // Socket für die Verbindung zum Server
        PrintWriter networkOut = null; // PrintWriter zum Senden von Daten an den Server
        BufferedReader networkIn = null; // BufferedReader zum Empfangen von Daten vom Server

        try {
            s = new Socket(HOST, PORT); // Verbindung zum Server herstellen
            System.out.println("Verbunden mit TCP-Server");

            networkOut = new PrintWriter(s.getOutputStream());  // PrintWriter zum Senden von Daten an den Server
            networkIn = new BufferedReader(new InputStreamReader(s.getInputStream()));  // BufferedReader zum Empfangen von Daten vom Server
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in)); // BufferedReader für Benutzereingaben im Terminal

            while (true) {
                System.out.print("Nachricht eingeben: ");
                String eingabe = userIn.readLine();     // Benutzereingabe lesen
                if (eingabe == null || eingabe.equalsIgnoreCase("exit")) break;

                networkOut.println(eingabe); // Nachricht an den Server senden
                networkOut.flush();     // Puffer leeren, um sicherzustellen, dass die Nachricht gesendet wird

                String antwort = networkIn.readLine(); // Antwort vom Server lesen
                System.out.println("Server-Antwort: " + antwort);
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                if (s != null) s.close();
            } catch (IOException e) {}
        }
    }
}
