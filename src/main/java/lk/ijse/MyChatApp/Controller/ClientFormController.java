package lk.ijse.MyChatApp.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class ClientFormController {
    public AnchorPane mainAnchorPane;
    public ScrollPane messageScrollPane;
    public AnchorPane MessageAnchore;
    public Label label;
    public Label datelbl;
    public Label timelbl;
    public JFXTextField MessageTextFeild;
    public JFXButton ImgButton;

    String message="";
    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    private double totalHeight = 40; // Initialize total height to 0
    private double totalHeightRecive;
    public void initialize(){
        new Thread(() -> {
            try {

                socket=new Socket("localhost",44000);
                dataInputStream=new DataInputStream(socket.getInputStream());
                dataOutputStream=new DataOutputStream(socket.getOutputStream());
                while (true) {
                    String type = dataInputStream.readUTF();
                    if (type.equals("Message")) {
                        System.out.println("sms");
                        String sms = dataInputStream.readUTF();
                        setReciveMessage(sms);
                    }
                    if (type.equals("image")) {
                        System.out.println("get img");
                        String size = dataInputStream.readUTF();
                        byte[] blob = new byte[Integer.parseInt(size)];
                        dataInputStream.readFully(blob);
                        setReciveImage(blob);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void setReciveImage(byte[] blob) {
        Platform.runLater(new Runnable() {
            public void run() {
                Image image = new Image(new ByteArrayInputStream(blob));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setLayoutX(5);
                imageView.setLayoutY(totalHeight); // Use totalHeight instead of y
                MessageAnchore.getChildren().add(imageView);
                messageScrollPane.setVvalue(1.0);

                // Increment totalHeight to update the position for the next message
                totalHeight += imageView.getBoundsInParent().getHeight() + 40;
            }
        });
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

    public void SendButtonOnAction(ActionEvent actionEvent) throws IOException {
        sendMessage();
    }

    private void sendMessage() throws IOException {
        dataOutputStream.writeUTF("Message");
        dataOutputStream.writeUTF(MessageTextFeild.getText().trim());
        dataOutputStream.flush();

        setMessage();
    }

    private void setMessage() {
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
                sendImg(blob);
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

            dataOutputStream.writeUTF("image");
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

