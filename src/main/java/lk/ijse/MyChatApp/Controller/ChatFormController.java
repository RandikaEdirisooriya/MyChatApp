package lk.ijse.MyChatApp.Controller;

import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

public class ChatFormController {
    public Label datelbl;
    public Label timelbl;
    public AnchorPane MessageAnchore;
    public ScrollPane messageScrollPane;
    public JFXTextField MessageTextFeild;
    public AnchorPane mainAnchorPane;
    public VBox Vbox;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    Socket socket;

    private int y=662;
    private double totalHeight = 0; // Initialize total height to 0
    private double totalHeightRecive = 40; // Initialize total height to 0
    String name;

    public void initialize() throws SQLException, ClassNotFoundException, IOException {
        name = LoginFormController.name;


        loadDateAndTime();

        new Thread(() -> {
            try {
                socket = new Socket("localhost",5000);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF(name);
                dataOutputStream.flush();

                 while (socket.isConnected()) {
                    String type = dataInputStream.readUTF();
                    if (type.equals("Message")) {
                      //  System.out.println("sms");
                        String sms = dataInputStream.readUTF();
                        setReciveMessage(sms);
                    }
                    if (type.equals("image")) {
                     //imge ek ganna
                        String size = dataInputStream.readUTF();
                        String user_name = dataInputStream.readUTF();
                        System.out.println(user_name+":img send awa");
                        byte[] blob = new byte[Integer.parseInt(size)];
                        dataInputStream.readFully(blob);

                        setImg(blob,user_name);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    private void setImg(byte[] blob, String user_name) {
        Platform.runLater(() -> {
            Label newLabel = new Label("  "+user_name+"  ");
            newLabel.setStyle(
                    "-fx-background-color: #325ee8;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-font: bold 15px ;"
            );


            Image image = new Image(new ByteArrayInputStream(blob));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setLayoutX(5);
            // Create a StackPane to hold the image and label
            StackPane stackPane = new StackPane(imageView, newLabel);
            stackPane.setAlignment(Pos.TOP_CENTER);

            // Create a new HBox to hold the StackPane
            HBox hBox = new HBox(stackPane);
            hBox.setStyle("-fx-padding:20;");
            hBox.setAlignment(Pos.CENTER_LEFT);  // Align to the right

            // Assuming VBox is your target VBox and is accessible within the scope
            Vbox.getChildren().add(hBox);
            messageScrollPane.setVvalue(1.0);
        });
    }



    private void setReciveMessage(String message) {
        Platform.runLater(() -> {
            Label newLabel = new Label("  " + message + "     ");
            newLabel.setStyle(
                    "-fx-background-color: #527bff;" +
                            "-fx-border-radius: 15;" +
                            "-fx-background-radius: 15;" +
                            "-fx-font-size: 15;" +
                            "-fx-font: bold;"
            );

            HBox hBox = new HBox(newLabel);
            hBox.setStyle("-fx-padding:20;");
            hBox.setAlignment(Pos.CENTER_LEFT);  // Align to the right for the user's messages
            Vbox.getChildren().add(hBox);
        });
    }



    private void loadDateAndTime() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        datelbl.setText(format.format(date));

        Timeline time = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            timelbl.setText(currentTime.getHour() + ":" + currentTime.getMinute() + ":" + currentTime.getSecond());
        }), new KeyFrame(Duration.seconds(1)));

        time.setCycleCount(Animation.INDEFINITE);
        time.play();
    }



    public void SendButtonOnAction(ActionEvent actionEvent) throws IOException {
        String message = MessageTextFeild.getText();
        try {
            dataOutputStream.writeUTF("Message");
            dataOutputStream.writeUTF(name+" : "+message); // x:hi
            dataOutputStream.flush();

            Label label = new Label(message);
            label.setStyle( "-fx-background-color: #76ffb3;" +
                    "-fx-border-radius: 15;" +
                    "-fx-background-radius: 15;" +
                    "-fx-font-size: 15;" +
                    "-fx-font: bold;");
            HBox hBox = new HBox(label);
            hBox.setStyle("-fx-padding:20;");
            hBox.setAlignment(Pos.CENTER_RIGHT);  // Align to the right for the user's messages
            Vbox.getChildren().add(hBox);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }







    public void ImgButtonOnAction(ActionEvent actionEvent) {

        FileChooser chooser = new FileChooser();
        File file =chooser.showOpenDialog(mainAnchorPane.getScene().getWindow());
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            if (fileInputStream!=null) {
                Image image = new Image(fileInputStream);

                byte[] blob = imagenToByte(image);
                String path = file.getPath();
                sendImg(blob);
                System.out.println(path);
                setMyImg(image);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setMyImg(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        // Create a new HBox to hold the image
        HBox hBox = new HBox(imageView);
        hBox.setStyle("-fx-padding:20;");
        hBox.setAlignment(Pos.CENTER_RIGHT);  // Align to the right

        // Assuming VBox is your target VBox and is accessible within the scope
        Vbox.getChildren().add(hBox);
        messageScrollPane.setVvalue(1.0);
    }





    private void sendImg(byte[] blob) {
        try {
            dataOutputStream.writeUTF("image");

            dataOutputStream.writeUTF(blob.length+"");
            dataOutputStream.writeUTF(name);
            dataOutputStream.write(blob);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private static byte[] imagenToByte(Image image) {
        BufferedImage bufferimage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferimage, "jpg", output );
            ImageIO.write(bufferimage, "png", output );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte [] data = output.toByteArray();
        return data;
    }

}
