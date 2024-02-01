package lk.ijse.MyChatApp.Controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.MyChatApp.Client.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoginFormController {
    public JFXTextField UserNameTextFeild;
    public JFXTextField PasswordTextFeild;
    public AnchorPane MainAnchore;
    private ServerSocket serverSocket;

    static String name;
    public void initialize(){

        try {
            serverSocket = new ServerSocket(5000);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    while (!serverSocket.isClosed()){

                        Socket socket = serverSocket.accept();
                        System.out.println("new user connected");
                        Client client = new Client(socket);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();

    }
    public void LoginButtonOnAction(ActionEvent actionEvent) throws IOException {
        name = UserNameTextFeild.getText();
        Parent rootNode = FXMLLoader.load(getClass().getResource("/View/Chat-form.fxml"));
        Scene scene = new Scene(rootNode);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
