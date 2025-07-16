package AdvancedIT.Serverprogrammierung.TCP.OwnExample;

import java.net.*;
import java.io.*;

public class TCPServer {
    public final static int PORT = 7777;

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);   // ServerSocket auf dem angegebenen Port erstellen
            System.out.println("TCP-Server läuft auf Port " + PORT);

            while (true) {
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

                        String antwort = "Antwort: " + empfangen.toUpperCase();
                        out.println(antwort);   // Antwort an den Client senden
                        out.flush();    // Puffer leeren, um sicherzustellen, dass die Antwort gesendet wird
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (connection != null) connection.close(); // Verbindung zum Client schließen
                    } catch (IOException e) {}
                }
            }

        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
