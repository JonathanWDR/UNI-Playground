package AdvancedIT.Aufgaben.A15;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class UDPFileServer {
    private static final String MESSAGES_DIR = System.getProperty("user.home") + "/Desktop/Messages/";
    
    public static void main(String[] args) {
        final int PORT = 5999;
        byte[] buffer = new byte[1024]; // Puffer für empfangene Daten

        createMessagesDirectory(); // Nachrichtenverzeichnis erstellen, falls es nicht existiert

        try {
            DatagramSocket server = new DatagramSocket(PORT);

            while (true) {
                DatagramPacket empfangen = new DatagramPacket(buffer, buffer.length);
                server.receive(empfangen); // Empfangen der Nachricht

                // Neuen Thread für jede Anfrage starten
                new Thread(new RequestHandler(server, empfangen)).start();
                
                // Buffer für nächste Anfrage zurücksetzen
                buffer = new byte[1024];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Thread-Klasse für die Behandlung von Anfragen
    static class RequestHandler implements Runnable {
        private DatagramSocket server;
        private DatagramPacket empfangen;
        
        public RequestHandler(DatagramSocket server, DatagramPacket empfangen) {
            this.server = server;
            this.empfangen = empfangen;
        }
        
        @Override
        public void run() {
            try {
                String befehl = new String(empfangen.getData(), 0, empfangen.getLength());
                System.out.println("Thread " + Thread.currentThread().getName() + " - Empfangen: " + befehl);

                if (befehl.startsWith("WRITE ")) {
                    String nachricht = befehl.substring(6); // "WRITE " entfernen
                    String filename = nachricht.split(",")[0]; // Dateiname extrahieren
                    String zeilennummer = nachricht.split(",")[1]; // Zeilennummer extrahieren
                    String content = nachricht.split(",")[2]; // Inhalt extrahieren

                    String antwort = saveMessage(filename, zeilennummer, content);

                    System.out.println("Thread " + Thread.currentThread().getName() + " - Response von der speichern methode: " + antwort);

                    sendResponse(antwort);

                } else if (befehl.startsWith("READ ")) {
                    String nachricht = befehl.substring(5); // "READ " entfernen
                    String filename = nachricht.split(",")[0]; // Dateiname extrahieren
                    String zeilennummer = nachricht.split(",")[1]; // Zeilennummer extrahieren
                    String antwort = getMessage(filename, zeilennummer);

                    if (antwort == null) {
                        antwort = "Fehler beim Lesen der Nachricht";
                    }

                    sendResponse(antwort);
                }
            } catch (Exception e) {
                System.err.println("Fehler im Thread " + Thread.currentThread().getName() + ": " + e.getMessage());
                try {
                    sendResponse("Fehler beim Verarbeiten der Anfrage");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        private void sendResponse(String antwort) throws IOException {
            byte[] antwortBytes = antwort.getBytes();
            
            DatagramPacket senden = new DatagramPacket(
                    antwortBytes,
                    antwortBytes.length,
                    empfangen.getAddress(),
                    empfangen.getPort()
            );
            
            server.send(senden);
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
    
    private static String saveMessage(String filename, String zeilennummer, String content) {
        String dateipfad = MESSAGES_DIR + filename + ".txt";
        
        if (zeilennummer == null || zeilennummer.isEmpty()) {
            // Keine Zeilennummer angegeben -> ans Ende anhängen
            try (PrintWriter writer = new PrintWriter(new FileWriter(dateipfad, true))) {
                writer.println(content);
                System.out.println("Nachricht angehängt an Datei: " + filename);
                return "Nachricht gespeichert";
            } catch (IOException e) {
                System.err.println("Fehler beim Speichern der Nachricht: " + e.getMessage());
                return "Fehler beim Speichern der Nachricht";
            }
        } else {
            // Zeilennummer angegeben -> in bestimmte Zeile schreiben
            try {
                int targetLine = Integer.parseInt(zeilennummer);
                
                // Datei lesen falls sie existiert
                java.util.List<String> lines = new java.util.ArrayList<>();
                Path filePath = Paths.get(dateipfad);
                
                if (Files.exists(filePath)) {
                    lines = Files.readAllLines(filePath);
                }
                
                // Leere Zeilen hinzufügen falls nötig
                while (lines.size() < targetLine) {
                    lines.add("");
                }
                
                // Zeile an gewünschter Position setzen (1-basiert)
                lines.set(targetLine - 1, content);
                
                // Datei neu schreiben
                try (PrintWriter writer = new PrintWriter(new FileWriter(dateipfad))) {
                    for (String line : lines) {
                        writer.println(line);
                    }
                }
                
                System.out.println("Nachricht gespeichert in Datei: " + filename + ", Zeile: " + targetLine);
                return "Nachricht gespeichert";
                
            } catch (NumberFormatException e) {
                System.err.println("Ungültige Zeilennummer: " + zeilennummer);
                return "Fehler beim Speichern der Nachricht";
            } catch (IOException e) {
                System.err.println("Fehler beim Speichern der Nachricht: " + e.getMessage());
                return "Fehler beim Speichern der Nachricht";
            }
        }
    }
    
    private static String getMessage(String filename, String zeilennummer) {
        String dateipfad = MESSAGES_DIR + filename + ".txt";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(dateipfad))) {
            int targetLine = Integer.parseInt(zeilennummer); // String zu int konvertieren
            String inhalt = null;
            
            // Zeilen durchgehen bis zur gewünschten Zeilennummer
            for (int i = 1; i <= targetLine; i++) {
                inhalt = reader.readLine();
                if (inhalt == null) {
                    System.err.println("Zeilennummer " + targetLine + " existiert nicht in der Datei");
                    return null;
                }
            }
            
            System.out.println("Nachricht abgerufen mit Schlüssel: " + filename + ", Zeile: " + targetLine);
            return inhalt;
        } catch (IOException e) {
            System.err.println("Datei nicht gefunden für Schlüssel: " + filename);
            return null;
        } catch (NumberFormatException e) {
            System.err.println("Ungültige Zeilennummer: " + zeilennummer);
            return null;
        }
    }
}
