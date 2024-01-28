package lk.ijse.MyChatApp.Controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class LoginFormController {
    public JFXTextField UserNameTextFeild;
    public JFXTextField PasswordTextFeild;
    public AnchorPane MainAnchore;

    public void LoginButtonOnAction(ActionEvent actionEvent) throws IOException {
        Node node = (Node) FXMLLoader.load(getClass().getResource("/View/Chat-form.fxml"));
        MainAnchore.getChildren().clear();
        MainAnchore.getChildren().setAll(node);
    }
}
