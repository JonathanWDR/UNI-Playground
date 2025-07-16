package AdvancedIT.Aufgaben.A15;

import java.net.*;
import java.util.Scanner;

public class UDPFileClient {
    public static void main(String[]args) {
        final String SERVER = "localhost";
        final int PORT = 5999;

        while(true) {
            try {
                DatagramSocket clientSocket = new DatagramSocket();
                Scanner scanner = new Scanner(System.in);
                System.out.print("Geben Sie eine Nachricht ein (oder '.' zum Beenden): ");
                String message = scanner.nextLine();

                if (message.equals(".")) {
                    System.out.println("Verbindung beendet.");
                    break;
                }
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(SERVER), PORT);
                clientSocket.send(sendPacket);

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
