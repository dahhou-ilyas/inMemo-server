package org.example;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class ClientHandler implements Runnable {
    class Node {
        private String value;
        private Long exp;

        Node(String value, Long exp) {
            this.value = value;
            this.exp = exp;
        }

        Node(String value) {
            this.value = value;
        }

    }

    private Socket clientSocket;
    private static final Map<String, Node> store = new HashMap<>();

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
            // !! il y a une errue dans le cas de deux commande simultané


            // khasni nchouf des exemples bach nfhem mzyan

            while (true) {
                int bytesAvailable = inputStream.available();
                if (bytesAvailable > 0) {
                    byte[] buffer = new byte[bytesAvailable];
                    int bytesRead = inputStream.read(buffer);

                    if (bytesRead > 0) {
                        String[] split = new String(buffer, 0, bytesRead).split("\r\n");
                        List<String> list = Arrays.asList(split);

                        ArrayList<String> arrayList = new ArrayList<>(list);
                        System.out.println(arrayList);
                        switch (split[2].toLowerCase()){
                            case "ping":
                                hundlePingCommande(split,outputStream);
                                break;
                            case "echo":
                                hundleEchoCommande(split,outputStream);
                                break;
                            case "get":
                                hundleGetCommande(split,outputStream);
                                break;
                            case "set":
                                hundleSetCommande(split,outputStream);
                                break;
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }

        } catch (SocketException e) {
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


    private void hundlePingCommande(String[] split,OutputStream outputStream) throws IOException {
        if (split.length > 3) {
            outputStream.write(("+" + split[4] + "\r\n").getBytes());
        } else {
            outputStream.write("+PONG\r\n".getBytes());
        }
        outputStream.flush();
    }

    private void hundleEchoCommande(String[] split,OutputStream outputStream) throws IOException{

        if(split.length<=3 || split.length>5){
            outputStream.write("-ERR wrong number of arguments\r\n".getBytes());
        }else {
            sendBulkString(outputStream,split[split.length-1]);
        }
        outputStream.flush();
    }

    private void hundleGetCommande(String[] split,OutputStream outputStream) throws IOException{
        if(split.length<=3 || split.length>5){
            outputStream.write("-ERR wrong number of arguments\r\n".getBytes());
        }else {
            Node val=store.get(split[split.length-1]);
            if (val !=null){
                System.out.println(val.exp);
                System.out.println(val.value);
                if (val.exp != null){
                    System.out.println(isExpired(val));
                    if(isExpired(val)){
                        outputStream.write("$-1\r\n".getBytes());
                    }else {
                        sendBulkString(outputStream,val.value);
                    }
                }else {
                    sendBulkString(outputStream,val.value);
                }
            }else {
                outputStream.write("$-1\r\n".getBytes());
            }

        }
        outputStream.flush();
    }

    private void hundleSetCommande(String[] split,OutputStream outputStream) throws IOException{
        try {
            if(split.length<7){
                outputStream.write("-ERR wrong number of arguments\r\n".getBytes());
            } else if (split.length > 7) {
                String px=split[8].toLowerCase().equals("px") ? "px":null;
                if(px==null){
                    outputStream.write("-ERR syntax error\r\n".getBytes());
                }else {
                    Long expiry = Long.parseLong(split[split.length-1]);
                    store.put(
                            split[4],
                            new Node(split[6],new Date().getTime() + expiry)
                    );
                    outputStream.write("+OK\r\n".getBytes());
                }
            }else {
                store.put(
                        split[4],
                        new Node(split[split.length-1])
                );
                outputStream.write("+OK\r\n".getBytes());
            }
        }catch (NumberFormatException e){
            outputStream.write("-ERR value is not an integer or out of range\r\n".getBytes());
        }finally {
            outputStream.flush();
        }

    }

    private boolean isExpired(Node node) {
        return new Date().getTime() > node.exp;
    }

    private void sendBulkString(OutputStream outputStream, String value) throws IOException {
        String response = "$" + value.length() + "\r\n" + value + "\r\n";
        outputStream.write(response.getBytes());
    }
}
