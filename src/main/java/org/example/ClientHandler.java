package org.example;


import org.example.types.DataType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

    private static final Object lock = new Object();
    private Socket clientSocket;
    private static final ConcurrentHashMap<String, Node> store = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService;
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void run() {
        try (
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
        ) {

            Parser parser=new Parser(inputStream);

            DataType dataType = parser.parseRequest();

            ProcessReqest processReqest=new ProcessReqest(dataType);

            DataType response = processReqest.process();

            outputStream.write(response.getFormattedValue().getBytes());

        } catch (SocketException e) {
            System.out.println("SocketException: Connection reset");
        } catch (IOException e) {
            System.err.println("Erreur de connexion avec le client : " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
            System.out.println("Client connection closed");
        }
    }


    private void handleCommand(String command, OutputStream outputStream) throws IOException {
        String[] split = command.split(" ");
        if (split.length > 1) {
            switch (split[1].toLowerCase()) {
                case "ping":
                    hundlePingCommande(split, outputStream);
                    break;
                case "echo":
                    hundleEchoCommande(split, outputStream);
                    break;
                case "get":
                    hundleGetCommande(split, outputStream);
                    break;
                case "set":
                    hundleSetCommande(split, outputStream);
                    break;
                default:
                    outputStream.write("-ERR unknown command\r\n".getBytes());
                    outputStream.flush();
            }
        } else {
            outputStream.write("-ERR invalid command\r\n".getBytes());
            outputStream.flush();
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
            clientSocket.close();
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

    private void skipNBytes(int n, InputStream inputStream) throws IOException {
        while (n != 0) {
            n -= inputStream.skip(n);
            System.out.println(n);
        }
    }

}

