package org.example;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private static final Map<String, String> store = new HashMap<>();

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



            String request;
            while (!clientSocket.isClosed() ) {
                request = bufferedReader.readLine();
                if (request == null) {
                    break;
                }

                request = request.trim().toLowerCase();
                if("set".equals(request.toLowerCase())){
                    String emptyLine = bufferedReader.readLine(); // Lire la ligne attendue (peut être vide)
                    if (emptyLine == null) {
                        outputStream.write("-Error wrong number of arguments for command\r\n".getBytes());
                        outputStream.flush();
                        break;
                    }
                    String key = bufferedReader.readLine();
                    if (key == null) {
                        outputStream.write("-Error wrong number of arguments for command\r\n".getBytes());
                        outputStream.flush();
                        break;
                    }
                    bufferedReader.readLine();
                    String value = bufferedReader.readLine();
                    if (value == null) {
                        outputStream.write("-Error wrong number of arguments for command\r\n".getBytes());
                        outputStream.flush();
                        break;
                    }
                    store.put(key, value);
                    outputStream.write("+OK\r\n".getBytes());
                    outputStream.flush();

                }else if("get".equals(request.toLowerCase())){
                    String emptyLine = bufferedReader.readLine();
                    if (emptyLine == null) {
                        outputStream.write("-Error wrong number of arguments for command\r\n".getBytes());
                        outputStream.flush();
                        break;
                    }
                    String key = bufferedReader.readLine();
                    if (key == null) {
                        outputStream.write("-Error wrong number of arguments for command\r\n".getBytes());
                        outputStream.flush();
                        break;
                    }
                    String value = store.get(key);
                    System.out.println(value);
                    if (value != null) {
                        outputStream.write(String.format("$%d\r\n%s\r\n", value.length(), value).getBytes());
                    } else {
                        outputStream.write("$-1\r\n".getBytes()); // Indique que la clé n'existe pas
                    }
                    outputStream.flush();
                }
                else if ("ping".equals(request)) {
                    outputStream.write("+PONG\r\n".getBytes());
                } else if ("ECHO".equalsIgnoreCase(request)) {
                    String lengthLine = bufferedReader.readLine();
                    if (lengthLine == null) {
                        outputStream.write("-ERR Missing argument length\r\n".getBytes());
                        continue;
                    }
                    int length = Integer.parseInt(lengthLine.substring(1));
                    String message = bufferedReader.readLine();
                    if (message == null || message.length() != length) {
                        outputStream.write("-ERR Invalid argument data\r\n".getBytes());
                        continue;
                    }
                    outputStream.write(String.format("$%d\r\n%s\r\n", message.length(), message).getBytes());
                }
                outputStream.flush(); // Assurez-vous que les données sont envoyées
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

    //-------------------------------------------------------
    private String processRequest(List<String> request) {
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
            index += 2;
        }

        String command = arguments.get(0);
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
}
//List<String> request=new ArrayList<>();
/*while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();

                if (line == null) {
                    // Connection closed by client
                    break;
                }
                request.add(line);
            }

            if (!request.isEmpty()) {
                String response = processRequest(request);
                System.out.println(response);
                outputStream.write(response.getBytes());
            }*/