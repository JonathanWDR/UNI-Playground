package Aufgabe11.Testat1;

import java.util.concurrent.Semaphore;

public class SharedTrackControllerA {
    private Semaphore full = new Semaphore(0, true);
    private Semaphore empty = new Semaphore(1, true);


    public SharedTrackControllerA(){}

    public void enterLok0(){
        try{
            empty.acquire();
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public void exitLok0(){
        full.release(); // Erzeuger produziert die Freigabe
    }

    public void enterLok1(){
        try{
            full.acquire();
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public void exitLok1(){
        empty.release(); // Verbraucher konsumiert die Freigabe
    }

    public static void main(String[] args) {
        SharedTrackControllerA track = new SharedTrackControllerA();
        new Thread( new Lok0(track) ).start();
        new Thread( new Lok1(track) ).start();
    }
}

class Lok0 implements Runnable { //Lok0 stellt den Erzeuger dar
    SharedTrackControllerA track;
    public Lok0(SharedTrackControllerA track){
        this.track = track;
    }

    public void run() {
        while(true) {
            try{
                System.out.println("Lok0 arriving to railroad switch");
                track.enterLok0(); // Lok0 betritt den geteilten Streckenabschnitt
                // Lok0 befindet sich im geteilten Streckenabschnitt
                System.out.println("Lok0 driving in the shared railtrack");
                Thread.sleep(300);
                track.exitLok0(); // Lok0 verlässt den geteilten Streckenabschnitt
                System.out.println("Lok0 driving in own railtrack");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}

class Lok1 implements Runnable { // Lok1 stellt den Verbraucher dar
    SharedTrackControllerA track;
    public Lok1(SharedTrackControllerA track){
        this.track = track;
    }

    public void run() {
        while(true) {
            try {
                System.out.println("-----------Lok1 arriving to railroad switch");
                track.enterLok1(); // Lok1 betritt den geteilten Streckenabschnitt
                System.out.println("-----------Lok1 driving in the shared railtrack");
                Thread.sleep(300);
                track.exitLok1(); // Lok1 verlässt den geteilten Streckenabschnitt
                System.out.println("-----------Lok1 driving in own railtrack");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}