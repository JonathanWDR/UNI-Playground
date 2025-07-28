package AdvancedIT.Testate.Testat3;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class TestClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5999;
    private static final int ANZAHL_THREADS = 6;
    private static final int BEFEHLE_PRO_THREAD = 8;
    private static final String DATEINAME = "testfile";

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch fertigLatch = new CountDownLatch(ANZAHL_THREADS);

        for (int i = 1; i <= ANZAHL_THREADS; i++) {
            new Thread(new AutomatedClient(fertigLatch, i)).start();
        }

        fertigLatch.await();
        System.out.println("Alle Tests wurden abgeschlossen.");
    }

    static class AutomatedClient implements Runnable {
        private final CountDownLatch latch;
        private final int nummer;
        private final Random zufall = new Random();

        public AutomatedClient(CountDownLatch latch, int nummer) {
            this.latch = latch;
            this.nummer = nummer;
        }

        @Override
        public void run() {
            try (DatagramSocket socket = new DatagramSocket()) {
                InetAddress serverAdresse = InetAddress.getByName(SERVER_HOST);

                for (int befehlNummer = 1; befehlNummer <= BEFEHLE_PRO_THREAD; befehlNummer++) {
                    boolean sollSchreiben = zufall.nextBoolean();
                    int zeilennummer = zufall.nextInt(3) + 1;

                    String befehlArt;
                    String nachricht;

                    if (sollSchreiben) {
                        String text = "Client " + nummer + " schreibt Zeile " + zeilennummer + " Befehl " + befehlNummer;
                        nachricht = "WRITE " + DATEINAME + "," + zeilennummer + "," + text;
                        befehlArt = "WRITE";
                    } else {
                        nachricht = "READ " + DATEINAME + "," + zeilennummer;
                        befehlArt = "READ";
                    }

                    // Einscheidung, Lese oder Schreiben    
                    synchronized (System.out) {
                        System.out.printf("[Client %d] will %s auf Zeile %d (Befehl %d)%n",
                                nummer, (befehlArt.equals("READ") ? "lesen " : "schreiben"), zeilennummer, befehlNummer);
                    }

                    // Nachricht an den Server senden
                    byte[] sendepuffer = nachricht.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket packet = new DatagramPacket(sendepuffer, sendepuffer.length, serverAdresse, SERVER_PORT);
                    socket.send(packet);

                    // Antwort empfangen
                    byte[] empfangspuffer = new byte[1024];
                    DatagramPacket antwort = new DatagramPacket(empfangspuffer, empfangspuffer.length);
                    socket.receive(antwort);

                    String antwortText = new String(antwort.getData(), 0, antwort.getLength(), StandardCharsets.UTF_8);

                    // Bestätigung der Durchführung
                    synchronized (System.out) {
                        System.out.printf("[Client %d] %s auf Zeile %d : Antwort: %s%n",
                                nummer,
                                (befehlArt.equals("READ") ? "liest   " : "schreibt"),
                                zeilennummer,
                                antwortText);
                    }

                    Thread.sleep(zufall.nextInt(100) + 30);
                }
            } catch (Exception e) {
                synchronized (System.err) {
                    System.err.printf("Fehler bei Client %d: %s%n", nummer, e.getMessage());
                }
            } finally {
                latch.countDown();
            }
        }
    }
}
