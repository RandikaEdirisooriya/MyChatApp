package lk.ijse.MyChatApp.Controller;

import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    Socket socket;
    ServerSocket serverSocket;
    String message = "";
    private int y=662;
    private double totalHeight = 0; // Initialize total height to 0
    private double totalHeightRecive = 40; // Initialize total height to 0

    public void initialize() throws SQLException, ClassNotFoundException, IOException {
        loadDateAndTime();
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(44000);
                System.out.println("server started");
                socket = serverSocket.accept();
                System.out.println("client connected");
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                while (!message.equals("exit")) {
                    message = dataInputStream.readUTF();
                    setReciveMessage(message);
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }).start();
    }

    private void setReciveMessage(String message) {
        Platform.runLater(() -> {
            Label newLabel = new Label("  " + message + "     ");
            newLabel.setStyle(
                    "-fx-background-color: #2A5EFF;" +
                            "-fx-border-radius: 15;" +
                            "-fx-background-radius: 15;" +
                            "-fx-font-size: 15;" +
                            "-fx-font: bold;"
            );

            AnchorPane.setTopAnchor(newLabel, totalHeightRecive);
            AnchorPane.setLeftAnchor(newLabel, 0.1);

            MessageAnchore.getChildren().add(newLabel);

            totalHeightRecive += newLabel.getBoundsInParent().getHeight() + 40;
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
        sendMessage();
    }




    public void sendMessage() throws IOException {
        dataOutputStream.writeUTF(MessageTextFeild.getText().trim());
        dataOutputStream.flush();

        setMessage();
    }



    public void setMessage() {
        Label newLabel = new Label("  " + MessageTextFeild.getText() + "     ");
        newLabel.setStyle(
                "-fx-background-color: #06FF7F;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-font-size: 15;" +
                        "-fx-font: bold;"
        );

        AnchorPane.setTopAnchor(newLabel, totalHeight);
        AnchorPane.setRightAnchor(newLabel, 0.1);

        MessageAnchore.getChildren().add(newLabel);

        totalHeight += newLabel.getBoundsInParent().getHeight() + 40;

        MessageTextFeild.clear();
    }



    public void ImgButtonOnAction(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        File file =chooser.showOpenDialog(mainAnchorPane.getScene().getWindow());
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            if (fileInputStream!=null) {
                Image image = new Image(fileInputStream);

                byte[] blob = imagenToByte(image);
              //  sendImg(blob);
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
        imageView.setLayoutX(400);
        imageView.setLayoutY(totalHeight); // Use totalHeight instead of y
        MessageAnchore.getChildren().add(imageView);
        messageScrollPane.setVvalue(1.0);

        // Increment totalHeight to update the position for the next message
        totalHeight += imageView.getBoundsInParent().getHeight() + 40;
    }




    private void sendImg(byte[] blob) {
        //
        try {

            dataOutputStream.writeUTF(blob.length+"");

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
