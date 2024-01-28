package lk.ijse.MyChatApp.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        try {
            boolean isServerRunning = true;

            ServerSocket serverSocket = new ServerSocket(3000);
            System.out.println("Server Started");

            while (isServerRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted : " + clientSocket);

                // Create a new thread to handle the client
                Thread clientThread = new Thread(() -> handleClient(clientSocket, scanner));
                clientThread.start();
            }

            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleClient(Socket clientSocket, Scanner scanner) {
        try (
                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        ) {
            boolean isClientConnected = true;

            while (isClientConnected) {
                // Read data from the client
                String message = dataInputStream.readUTF();
                System.out.println("");
                System.out.println("Client " + clientSocket + ": " + message);

                // Get server input
                System.out.print("Enter server message: ");
                String serverMessage = scanner.nextLine();

                // Send the server message to the client
                dataOutputStream.writeUTF(serverMessage);

                // Check for exit condition
                if (message.equals("exit") || serverMessage.equals("exit")) {
                    isClientConnected = false;
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}