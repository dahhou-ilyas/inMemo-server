package org.example;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

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
            List<String> request = new ArrayList<>();
            while (bufferedReader.ready()) {
                /*if ("PING".equals(request)) {
                    outputStream.write("+PONG\r\n".getBytes());
                }else if ("ECHO".equalsIgnoreCase(inputLine)) {
                    bufferedReader.readLine();
                    String message = bufferedReader.readLine();
                    outputStream.write(
                            String.format("$%d\r\n%s\r\n", message.length(), message)
                                    .getBytes());
                }*/
                request.add(bufferedReader.readLine());
            }

            if (!request.isEmpty()) {
                String response = processRequest(request);
                System.out.println(response);
                outputStream.write(response.getBytes());
                outputStream.flush();
            }

        }catch (SocketException e) {
            System.out.println("SocketException: Connection reset");

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

    private String processRequest(List<String> request) {
        // Assure que la requête est au format RESP
        if (request.isEmpty() || !request.get(0).startsWith("*")) {
            return "-ERR Invalid request\r\n";
        }

        int numArgs = Integer.parseInt(request.get(0).substring(1));
        if (numArgs < 1) {
            return "-ERR Invalid number of arguments\r\n";
        }

        List<String> arguments = new ArrayList<>();
        int index = 1;
        for (int i = 0; i < numArgs; i++) {
            if (index >= request.size()) {
                return "-ERR Missing argument length\r\n";
            }
            String lengthStr = request.get(index).substring(1);
            int length = Integer.parseInt(lengthStr);
            String argument;
            if (length == -1) {
                argument = null;
            } else if (index + 1 < request.size()) {
                argument = request.get(index + 1);
            } else {
                return "-ERR Missing argument data\r\n";
            }
            arguments.add(argument);
            index += 2; // Passer au prochain argument
        }

        String command = arguments.get(0); // En supposant que la première commande est "ECHO"
        String response = "";

        if("PING".equalsIgnoreCase(command)){
            return "+PONG\r\n";
        }

        if ("ECHO".equalsIgnoreCase(command)) {
            if (arguments.size() > 1) {
                String echoArgument = arguments.get(1);
                response = echoArgument != null ? echoArgument : "";
            }
        }

        return formatResponse(response);
    }

    private String formatResponse(String response) {
        if (response == null) {
            return "$-1\r\n"; // Format pour une réponse null
        } else if (response.isEmpty()) {
            return "$0\r\n\r\n"; // Format pour une chaîne vide
        } else {
            return String.format("$%d\r\n%s\r\n",response.length(), response);
        }
    }

    private boolean isCompleteResponse(String response) {
        // Vérifiez si la réponse est complète en fonction du protocole RESP
        // Vous pouvez ajouter des vérifications plus spécifiques ici
        return response.endsWith("\r\n");
    }
}
