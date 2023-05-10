package pkg;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import message.Message;
import threadClient.ClientDataReceiveThread;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;


public class Chatting_with_UI extends Application {
    Socket socket = null;
    OutputStream os = null;
    ObjectOutputStream oos = null;
    @Override
    public void start(Stage arg0)  {
        //Vertical box, Horizontal box
        VBox root = new VBox();
        root.setPrefSize(300, 500);
        //----------------------------------
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 5001));
            os = socket.getOutputStream();
            oos = new ObjectOutputStream(os);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("바인딩 끝");


        Button sendBtn = new Button("전송");
        sendBtn.setLayoutY(300);
        TextField messageField = new TextField();
        TextArea dialogArea = new TextArea();

        new ClientDataReceiveThread(socket, dialogArea).start();


        sendBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {
                    String msg = messageField.getText();
                    Message message = new Message();
                    message.setName("주원");
                    message.setMsg(msg);
                    message.setNotice(false);
                    oos.writeObject(message);
                    messageField.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        root.getChildren().addAll(sendBtn, messageField, dialogArea);
        //----------------------------------
        Scene scene = new Scene(root);
        arg0.setScene(scene);
        arg0.setTitle("클라이언트");
        arg0.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
