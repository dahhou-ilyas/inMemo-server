package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

  private static final int THREAD_POOL_SIZE = 10; // Définir la taille du pool de threads
  private static final int PORT = 6379; // Port d'écoute

  private static final Object lock = new Object();
  public static void main(String[] args) {
    ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    System.out.println("Logs from your program will appear here!");

    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      serverSocket.setReuseAddress(true);
      while (true) {
        try {
          Socket clientSocket;
          synchronized (lock) {
            clientSocket = serverSocket.accept();
            System.out.println("Connexion acceptée de " + clientSocket.getInetAddress());
          }
          executorService.submit(new ClientHandler(clientSocket));
        } catch (IOException e) {
          System.out.println("Erreur lors de l'acceptation de la connexion : " + e.getMessage());
        }
      }
    } catch (IOException e) {
      System.out.println("Erreur lors de la création du ServerSocket : " + e.getMessage());
    } finally {
      executorService.shutdown(); // Ferme le pool de threads proprement
    }
  }
}
