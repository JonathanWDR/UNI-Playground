package AdvancedIT.Testate.Testat3;

public class WorkerThread implements Runnable {
    RingBuffer requestQueue;
    FileManager fileManager;

    public WorkerThread(RingBuffer requestQueue) {
        this.requestQueue = requestQueue;
        this.fileManager = new FileManager();
    }
    
    public WorkerThread(RingBuffer requestQueue, FileManager fileManager) {
        this.requestQueue = requestQueue;
        this.fileManager = fileManager;
    }

    @Override
    public void run() {
        Request request;
        while (true) {
            request = requestQueue.remove();
            if (request != null) {
                try {
                    processRequest(request);
                } catch (InterruptedException e) {
                    System.err.println("Fehler beim Verarbeiten der Anfrage: " + e.getMessage());
                }
            }
        }
    }

    private void processRequest(Request request) throws InterruptedException {
        System.out.println("Verarbeite Nachricht: " + request.message);

        String command = request.getCommand();
        String filename = request.getFilename();
        String lineNumberStr = request.getLineNumber();
        String response = "";
        
        try {
            int lineNumber = Integer.parseInt(lineNumberStr);
            
            if ("READ".equals(command)) {
                String content = fileManager.readLine(filename, lineNumber);
                if (content != null) {
                    response = "SUCCESS: " + content;
                    System.out.println("Gelesener Inhalt aus " + filename + " Zeile " + lineNumber + ": " + content);
                } else {
                    response = "ERROR: Kein Inhalt gefunden in " + filename + " Zeile " + lineNumber;
                    System.out.println("Kein Inhalt gefunden in " + filename + " Zeile " + lineNumber);
                }
                
            } else if ("WRITE".equals(command)) {
                String message = request.getData();
                String result = fileManager.saveMessage(filename, lineNumber, message);
                if (result != null) {
                    response = "SUCCESS: Nachricht gespeichert in " + filename + " Zeile " + lineNumber;
                    System.out.println("Nachricht erfolgreich gespeichert in " + filename);
                } else {
                    response = "ERROR: Fehler beim Speichern in " + filename;
                    System.out.println("Fehler beim Speichern der Nachricht in " + filename);
                }
                
            } else {
                response = "ERROR: Unbekannter Befehl: " + command;
                System.err.println("Unbekannter Befehl: " + command);
            }
            
        } catch (NumberFormatException e) {
            response = "ERROR: Ungültige Zeilennummer: " + lineNumberStr;
            System.err.println("Ungültige Zeilennummer: " + lineNumberStr);
        }
        
        // Antwort an Client senden
        sendResponse(request, response);
    }
    
    private void sendResponse(Request request, String response) {
        try {
            byte[] responseBytes = response.getBytes();
            java.net.DatagramPacket responsePacket = new java.net.DatagramPacket(
                responseBytes,
                responseBytes.length,
                request.address,
                request.port
            );
            request.socket.send(responsePacket);
            System.out.println("Antwort gesendet: " + response);
        } catch (Exception e) {
            System.err.println("Fehler beim Senden der Antwort: " + e.getMessage());
        }
    }
}
