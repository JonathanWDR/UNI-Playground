package Aufgabe11.Testat1;

import java.util.concurrent.Semaphore;

public class SharedTrackControllerAWithQueue {
    private int size;
    private String[] buffer; // Ringpuffer wie bei der klassischen Erzeuger-Verbraucher-Lösung
    private int nextfree = 0;
    private int nextfull = 0;
    private Semaphore full = new Semaphore(0, true);
    private Semaphore empty;

    public SharedTrackControllerAWithQueue(int size){
        this.size = size;
        buffer = new String[size];
        empty = new Semaphore(size, true);
    }
    public void enterLok0Q(String payload){
        try{
            empty.acquire();
            buffer[nextfree] = payload;
            nextfree = (nextfree+1) % size;
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public void exitLok0(){
        full.release();
    }

    public String enterLok1Q(){
        String data="";
        try{
            full.acquire();
            data = buffer[nextfull];
            nextfull = (nextfull+1) % size;
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        return data;
    }

    public void exitLok1(){
        empty.release();
    }

    public static void main(String[] args) {
        SharedTrackControllerAWithQueue track = new SharedTrackControllerAWithQueue(1); // Der Ringpuffer wird mit der Größe 1 initialisiert,
        // so befahren Lok0 und Lok1 also Erzeuger und Verbraucher abwechselnd den geteilten Streckenabschnitt
        new Thread( new Lok0Q(track) ).start();
        new Thread( new Lok1Q(track) ).start();
    }
}

class Lok0Q implements Runnable {
    SharedTrackControllerAWithQueue track;
    public Lok0Q(SharedTrackControllerAWithQueue track){
        this.track = track;
    }

    public void run() {
        int i = 0;
        while(true) {
            try {
                System.out.println("Lok0 arriving to railroad switch");
                track.enterLok0Q("Wagenladung" + i); //Lok0Q befährt den geteilten Streckenabschnitt und legt eine Wagenladung ab (Erzeuger)
                System.out.println("Lok0 driving in the shared railtrack and deploys payload: Wagenladung" + i);
                Thread.sleep(300);
                track.exitLok0();
                System.out.println("Lok0 driving in own track");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e);
            }

            i++;
        }
    }
}

class Lok1Q implements Runnable {
    SharedTrackControllerAWithQueue track;
    public Lok1Q(SharedTrackControllerAWithQueue track){
        this.track = track;
    }

    public void run() {
        String payload;
        while (true) {
            try{
                System.out.println("-----------Lok1 arriving to railroad switch");
                payload = track.enterLok1Q(); // Lok1 befährt den geteilten Streckenabschnitt und nimmt die Wagenladung auf (Verbraucher)
                System.out.println("-----------Lok1 driving in the shared railtrack and picking up payload: " + payload);
                Thread.sleep(300);
                track.exitLok1();
                System.out.println("-----------Lok1 driving in own track");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}