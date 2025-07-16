package AdvancedIT.EigeneProjekte.PersistenterServer;
import java.net.*;
import java.io.*;

public class TCPMessageClient {
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
                System.out.print("Befehl eingeben (S für Save, G für Get, exit zum Beenden): ");
                String befehl = userIn.readLine();     // Benutzereingabe lesen
                if (befehl == null || befehl.equalsIgnoreCase("exit")) break;

                if (befehl.equalsIgnoreCase("S")) {
                    // Save Operation
                    System.out.print("Text zum Speichern eingeben: ");
                    String text = userIn.readLine();
                    
                    networkOut.println("SAVE " + text); // Text an den Server senden
                    networkOut.flush();     // Puffer leeren
                    
                    String response = networkIn.readLine(); // Antwort vom Server empfangen
                    // "KEY " am Anfang entfernen
                    String key = response.startsWith("KEY ") ? response.substring(4) : response;
                    System.out.println("Gespeichert mit Key: " + key);
                    
                } else if (befehl.equalsIgnoreCase("G")) {
                    // Get Operation
                    System.out.print("Key eingeben: ");
                    String key = userIn.readLine();
                    
                    networkOut.println("GET " + key); // GET-Befehl mit Key an Server senden
                    networkOut.flush();     // Puffer leeren
                    
                    String response = networkIn.readLine(); // Antwort vom Server empfangen
                    if (response.startsWith("OK ")) {
                        String inhalt = response.substring(3); // "OK " entfernen
                        System.out.println("Inhalt: " + inhalt);
                    } else if (response.equals("FAILED")) {
                        System.out.println("FAILED - Datei nicht gefunden oder Fehler beim Abrufen");
                    } else {
                        System.out.println("Unbekannte Antwort vom Server: " + response);
                    }
                    
                } else {
                    System.out.println("Ungültiger Befehl. Verwenden Sie S für Save oder G für Get.");
                }
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
