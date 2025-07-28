package AdvancedIT.Testate.Testat3;

public class RingBuffer {
    // Monitor-basierte E/V-Problem Implementierung

    private int size;
    private Request[] buffer;
    private int nextfree = 0;
    private int nextfull = 0;
    
    // Monitor für Producer-Consumer
    private final Object monitor = new Object();
    private int count = 0; // Anzahl Elemente im Puffer

    public RingBuffer(int size) {
        this.size = size;
        buffer = new Request[size];
    }

    public void put(Request data) {
        synchronized(monitor) {
            try {
                System.out.println("Producer arriving");
                
                // Warten bis Platz im Puffer ist
                while(count == size) {
                    System.out.println("Producer waiting - buffer full");
                    monitor.wait();
                }
                
                System.out.println("Producer active with " + data);
                buffer[nextfree] = data;
                nextfree = (nextfree + 1) % size;
                count++;
                
                // Consumer aufwecken, falls welche warten
                monitor.notifyAll();
                System.out.println("Producer gone");
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
    
    public Request remove() {
        synchronized(monitor) {
            try {
                System.out.println("------------------Consumer arriving");
                
                // Warten bis Daten im Puffer sind
                while(count == 0) {
                    System.out.println("------------------Consumer waiting - buffer empty");
                    monitor.wait();
                }
                
                System.out.println("------------------Consumer active");
                Request data = buffer[nextfull];
                buffer[nextfull] = null; // Referenz löschen
                nextfull = (nextfull + 1) % size;
                count--;
                
                // Producer aufwecken, falls welche warten
                monitor.notifyAll();
                System.out.println("------------------Consumer gone with " + data);
                
                return data;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
                return null;
            }
        }
    }
}
