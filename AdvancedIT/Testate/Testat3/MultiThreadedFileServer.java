package AdvancedIT.Testate.Testat3;
import java.net.*;

public class MultiThreadedFileServer {
    private static final int WORKER_COUNT = 4;
    private RingBuffer requestQueue;
    private WorkerThread[] workers;
    private FileManager fileManager;

    public MultiThreadedFileServer() {
        this.requestQueue = new RingBuffer(10); // Größe nach Bedarf anpassen
        this.workers = new WorkerThread[WORKER_COUNT];
        this.fileManager = new FileManager();
    }

    public void start() {
        final int PORT = 5999;
        byte[] buffer = new byte[1024];

        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            System.out.println("UDP-Server läuft auf Port " + PORT);

            // Worker-Threads starten
            for (int i = 0; i < WORKER_COUNT; i++) {
                workers[i] = new WorkerThread(requestQueue, fileManager);
                new Thread(workers[i]).start();
            }

            // Warten auf Anfragen
            while (true) {
                DatagramPacket empfangen = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(empfangen);

                String nachricht = new String(empfangen.getData(), 0, empfangen.getLength());
                System.out.println("Empfangen: " + nachricht);

                Request request = new Request(nachricht, empfangen.getAddress(), empfangen.getPort(), serverSocket);
                requestQueue.put(request);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MultiThreadedFileServer().start();
    }
}