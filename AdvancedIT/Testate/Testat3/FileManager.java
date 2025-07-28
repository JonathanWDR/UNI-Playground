package AdvancedIT.Testate.Testat3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {
    static String MESSAGES_DIR = System.getProperty("user.home") + "/Desktop/Messages/";
    
    // Monitor für Leser-Schreiber-Problem
    private static final Object monitor = new Object();
    private static boolean activeWriter = false;
    private static int readctr = 0;
    private static int waitingWriters = 0; // Für Schreiberpriorität

    public FileManager() {}

    public static void main(String[] args) {
        createMessagesDirectory();
    }
    

    // Monitor-Methoden für Leser-Schreiber-Problem mit Schreiberpriorität
    private static void startRead() throws InterruptedException {
        synchronized(monitor) {
            while(activeWriter || waitingWriters > 0) {
                monitor.wait();
            }
            readctr++;
        }
    }
    
    private static void endRead() {
        synchronized(monitor) {
            readctr--;
            if(readctr == 0) {
                monitor.notifyAll(); // Wartende Schreiber wecken
            }
        }
    }
    
    private static void startWrite() throws InterruptedException {
        synchronized(monitor) {
            waitingWriters++;
            while(readctr > 0 || activeWriter) {
                monitor.wait();
            }
            waitingWriters--;
            activeWriter = true;
        }
    }
    
    private static void endWrite() {
        synchronized(monitor) {
            activeWriter = false;
            monitor.notifyAll(); // Alle wartenden Threads wecken
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
    
    public String saveMessage(String filename, int lineNumber, String nachricht) throws InterruptedException {
        String dateipfad = MESSAGES_DIR + filename + ".txt";

        startWrite();
        
        try {
            // Erst die aktuelle Anzahl Zeilen ermitteln
            int currentLineCount = 0;
            Path filePath = Paths.get(dateipfad);
            
            if (Files.exists(filePath)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(dateipfad))) {
                    while (reader.readLine() != null) {
                        currentLineCount++;
                    }
                }
            }
            
            // Array-Größe bestimmen (mindestens so groß wie die gewünschte Zeile)
            int arraySize = Math.max(currentLineCount, lineNumber);
            String[] lines = new String[arraySize];
            
            // Alle existierenden Zeilen in Array laden
            if (Files.exists(filePath)) {
                try (BufferedReader reader = new BufferedReader(new FileReader(dateipfad))) {
                    String line;
                    int i = 0;
                    while ((line = reader.readLine()) != null && i < arraySize) {
                        lines[i] = line;
                        i++;
                    }
                }
            }
            
            // Leere Stellen mit leeren Strings füllen
            for (int i = 0; i < arraySize; i++) {
                if (lines[i] == null) {
                    lines[i] = "";
                }
            }
            
            // Gewünschte Zeile ersetzen (lineNumber ist 1-basiert, Array ist 0-basiert)
            lines[lineNumber - 1] = nachricht;
            
            // Datei komplett neu schreiben
            try (PrintWriter writer = new PrintWriter(new FileWriter(dateipfad, false))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
            
            System.out.println("Zeile " + lineNumber + " in Datei " + filename + " ersetzt mit: " + nachricht);
            return filename;
            
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Nachricht: " + e.getMessage());
            return null;
        } finally {
            endWrite();
        }
    }
    
    public String readLine(String filename, int lineNumber) throws InterruptedException{
        String dateipfad = MESSAGES_DIR + filename + ".txt";

        startRead();

        // k. A.
        try (BufferedReader reader = new BufferedReader(new FileReader(dateipfad))) {
            String line;
            int currentLine = 1;

            while((line = reader.readLine()) != null) {
                if (currentLine == lineNumber) {
                    return line; // Zeile gefunden
                }
                currentLine++;
            }

            // Zeile nicht gefunden
            System.out.println("Zeile " + lineNumber + " nicht gefunden in Datei: " + filename);
            return null;
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
            return null;
        } finally {
            endRead();
        }
    }
}