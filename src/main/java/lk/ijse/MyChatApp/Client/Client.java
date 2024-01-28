package lk.ijse.MyChatApp.Client;

import lk.ijse.MyChatApp.Controller.ChatFormController;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        try {
            boolean isStart = true;
            Socket socket = new Socket("localhost", 44000);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            while (isStart) {
                System.out.print("Enter Your Message: ");
                String message = scanner.nextLine();
                dataOutputStream.writeUTF(message);

                if (message.equals("exit")) {
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    dataInputStream.close();
                    socket.close();
                    isStart = false;
                }

                String newmsg = dataInputStream.readUTF();
                System.out.println("server: "+ newmsg);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
