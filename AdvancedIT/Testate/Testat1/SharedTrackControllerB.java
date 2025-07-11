package Aufgabe11.Testat1;

import java.util.concurrent.Semaphore;

public class SharedTrackControllerB {
    private boolean sharedTrackFree = true;
    private boolean[] waiting;
    private Semaphore[] privsem;
    private Semaphore mutex = new Semaphore(1, true); // wegen gemeinsamen Verwaltungsdaten (sharedTrack, waiting[])
    private static final int NO_ID = -1;

    public SharedTrackControllerB(int threadCount) {
        waiting = new boolean[threadCount];
        privsem = new Semaphore[threadCount];
        for (int i=0; i<threadCount; i++) {
            waiting[i] = false;
            privsem[i] = new Semaphore(0, true);
        }
    }

    public void enterSharedTrack(int threadId) throws InterruptedException{
        mutex.acquire(); //Verhindert die gleichzeitige Bearbeitung der Verwaltungsdaten durch beide Threads
        if(sharedTrackFree) { //Gemeinsamen Track reservieren
            sharedTrackFree = false;
            privsem[threadId].release(); // nachher nicht warten!
        } else {
            waiting[threadId] = true;
        }
        mutex.release(); // Gibt die Bearbeitung der Verwaltungsdaten wieder frei
        privsem[threadId].acquire();

        System.out.println("Lok " + threadId + ": Ich befahre jetzt den gemeinsamen Track!");
    }

    public void exitSharedTrack(int threadId) throws InterruptedException{
        System.out.println("Lok " + threadId + ": Ich verlasse jetzt den gemeinsamen Track!");
        mutex.acquire(); //Verhindert die gleichzeitige Bearbeitung der Verwaltungsdaten durch beide Threads
        sharedTrackFree = true;
        int selectedThread = 0;
        // gebe der anderen Lok wieder die Strecke frei
        if(threadId == 0) {
            selectedThread = 1;
        }

        waiting[selectedThread] = false;
        sharedTrackFree = false;
        privsem[selectedThread].release();

        mutex.release(); // Gibt die Bearbeitung der Verwaltungsdaten wieder frei
    }

    public static void main(String[] args) {
        SharedTrackControllerB track = new SharedTrackControllerB(2);
        new Thread(new LokB0(track)).start();
        new Thread(new LokB1(track)).start();
    }
}


class LokB0 implements Runnable {
    SharedTrackControllerB track;
    public LokB0(SharedTrackControllerB track) {
        this.track = track;
    }

    public void run() {
        while(true) {
            try{
                track.enterSharedTrack(0);
                Thread.sleep(300);
                track.exitSharedTrack(0);
                Thread.sleep(500);

            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}

class LokB1 implements Runnable {
    SharedTrackControllerB track;
    public LokB1(SharedTrackControllerB track) {
        this.track = track;
    }

    public void run() {
        while(true) {
            try{
                track.enterSharedTrack(1);
                Thread.sleep(300);
                track.exitSharedTrack(1);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}