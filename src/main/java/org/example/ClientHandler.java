package org.example;

import java.io.*;
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

            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));

            //lire les nomber des line envoyé par commande et a chque ligne envoyé un réponse
            String inputLine;
            while ((inputLine=bufferedReader.readLine())!=null){
                outputStream.write(("+PONG\r\n").getBytes());
                outputStream.flush();
            }

            outputStream.write("+PONG\r\n".getBytes());
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
