package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

  private static final int THREAD_POOL_SIZE = 20;
  public static void main(String[] args){
    ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 6379;
    try {
      serverSocket = new ServerSocket(port);
      serverSocket.setReuseAddress(true);
      while (true){
        clientSocket = serverSocket.accept();
        System.out.println("Connexion acceptée de " + clientSocket.getInetAddress());
        executorService.submit(new ClientHandler(clientSocket));
      }

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
