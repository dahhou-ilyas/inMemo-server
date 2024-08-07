package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }
    @Override
    public void run() {
        try {
            InputStream inputStream=clientSocket.getInputStream();
            OutputStream outputStream=clientSocket.getOutputStream();

        }catch (IOException e) {
            System.err.println("Erreur de connexion avec le client : " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("connexion fermer");
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture de la socket du client : " + e.getMessage());
            }
        }
    }
}
