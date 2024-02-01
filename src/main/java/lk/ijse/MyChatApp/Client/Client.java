package lk.ijse.MyChatApp.Client;

import lk.ijse.MyChatApp.Controller.ChatFormController;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String userName;
    private String sender;

    public static ArrayList<Client> clients = new ArrayList<>();

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            userName = dataInputStream.readUTF();
            clients.add(this);

            listenMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String type = dataInputStream.readUTF();
                    if (type.equals("Message")) {
                        String message = dataInputStream.readUTF();
                        String[] name = message.split(" : ");
                        sender = name[0];
                        broadcastMessage(message);
                    } else if (type.equals("image")) {
                        receiveImage();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void receiveImage() {
        try {
            String size = dataInputStream.readUTF();
            String user_name = dataInputStream.readUTF();
           // System.out.println(user_name+":img send");
            int imageSize = Integer.parseInt(size);

            byte[] blob = new byte[imageSize];
            dataInputStream.readFully(blob);

            broadcastImage(blob,user_name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void broadcastImage(byte[] blob,String user_Name) {
        for (Client client : clients) {
            try {
                if (!client.userName.equals(sender)) {
                   client. dataOutputStream.writeUTF("image");

                    client. dataOutputStream.writeUTF(blob.length+"");
                    client. dataOutputStream.writeUTF(user_Name);
                    client.dataOutputStream.write(blob);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void broadcastMessage(String message) {
        for (Client client : clients) {
            try {
                if (!client.userName.equals(sender)) {
                    client.dataOutputStream.writeUTF("Message");
                    client.dataOutputStream.writeUTF(message);
                    client.dataOutputStream.flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
