package org.example;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                if (inputLine.toLowerCase().startsWith("ping")) {
                    outputStream.write(("+PONG\r\n").getBytes());
                    outputStream.flush();
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur de connexion avec le client : " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Connexion fermée.");
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture de la socket du client : " + e.getMessage());
            }
        }
    }
}
