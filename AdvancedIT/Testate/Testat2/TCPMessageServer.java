package AdvancedIT.Testate.Testat2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class TCPMessageServer {
    public static final int PORT = 7777;
    private static final String MESSAGES_DIR = System.getProperty("user.home") + "/Desktop/Messages/";

    public static void main(String[] args) {
        createMessagesDirectory(); // Messages-Verzeichnis erstellen falls es nicht existiert
        
        try {
            ServerSocket server = new ServerSocket(PORT);   // ServerSocket auf dem angegebenen Port erstellen
            System.out.println("TCPMessageServer läuft auf Port " + PORT);

            Socket connection = null;   // Socket für die Verbindung zum Client
            PrintWriter out = null; // PrintWriter zum Senden von Daten an den Client
            BufferedReader in = null;   // BufferedReader zum Empfangen von Daten vom Client

                try {
                    connection = server.accept(); // Auf eingehende Verbindungen warten
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));    // BufferedReader zum Empfangen von Daten vom Client
                    out = new PrintWriter(connection.getOutputStream());    // PrintWriter zum Senden von Daten an den Client

                    String empfangen;
                    while ((empfangen = in.readLine()) != null) {   // Daten vom Client lesen
                        System.out.println("Client sendet: " + empfangen);

                        if (empfangen.startsWith("SAVE ")) {
                            // Save Operation
                            String nachricht = empfangen.substring(5); // "SAVE " entfernen
                            String schluessel = saveMessage(nachricht);
                            out.println("KEY " + schluessel);   // Schlüssel zurück an Client senden
                            out.flush();
                            
                        } else if (empfangen.startsWith("GET ")) {
                            // Get Operation
                            String schluessel = empfangen.substring(4); // "GET " entfernen
                            String inhalt = getMessage(schluessel);
                            if (inhalt != null) {
                                out.println("OK " + inhalt);   // Inhalt an Client senden
                            } else {
                                out.println("FAILED");   // Datei nicht gefunden
                            }
                            out.flush();
                            
                        } else {
                            out.println("FAILED");
                            out.flush();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (connection != null) connection.close(); // Verbindung zum Client schließen
                    } catch (IOException e) {}
                }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    private static void createMessagesDirectory() {
        try {
            Path messagesPath = Paths.get(MESSAGES_DIR);
            if (!Files.exists(messagesPath)) {
                Files.createDirectories(messagesPath);
                System.out.println("Messages-Verzeichnis erstellt: " + MESSAGES_DIR);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Erstellen des Messages-Verzeichnisses: " + e.getMessage());
        }
    }
    
    private static String saveMessage(String nachricht) {
        String schluessel = UUID.randomUUID().toString(); // Eindeutigen Schlüssel generieren
        String dateipfad = MESSAGES_DIR + schluessel + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(dateipfad))) {
            writer.println(nachricht);
            System.out.println("Nachricht gespeichert mit Schlüssel: " + schluessel);
            return schluessel;
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Nachricht: " + e.getMessage());
            return null;
        }
    }
    
    private static String getMessage(String schluessel) {
        String dateipfad = MESSAGES_DIR + schluessel + ".txt";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(dateipfad))) {
            String inhalt = reader.readLine();
            System.out.println("Nachricht abgerufen mit Schlüssel: " + schluessel);
            return inhalt;
        } catch (IOException e) {
            System.err.println("Datei nicht gefunden für Schlüssel: " + schluessel);
            return null;
        }
    }
}
