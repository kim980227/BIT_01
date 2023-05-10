package pkg;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

class ServerThread extends Thread{
    Socket socket;
    TextArea dialogArea;
    DataInputStream dataInputStream = null;
    ServerThread(Socket socket, TextArea dialogArea){
        this.socket = socket;
        this.dialogArea = dialogArea;
    }

    @Override
    public void run(){
        try{
            while(true) {
                dataInputStream = new DataInputStream(socket.getInputStream());
                byte[] result_byte = new byte[2048];
                int size = dataInputStream.read(result_byte);
                String result = new String(result_byte, 0, size, "utf-8");
                System.out.println(result);
                result+="\n";

                dialogArea.appendText(result);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}

public class Chatting_with_UI extends Application {
    Socket socket = null;

    // 그림그리기
    StackPane pane = new StackPane();
    Scene scene = new Scene(pane, 800, 500);
    Canvas canvas = new Canvas(800,500); // 캔버스 생성 및 화면 크기 설정
    GraphicsContext gc;

    @Override
    public void start(Stage arg0) throws Exception {
        //Vertical box, Horizontal box
        VBox root = new VBox();
        root.setPrefSize(300, 500);
        //----------------------------------

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 5001));
        } catch (Exception e) {
            e.printStackTrace();
        }


        Button sendBtn = new Button("전송");
        sendBtn.setLayoutY(300);
        TextField messageField = new TextField();
        TextArea dialogArea = new TextArea();

        new ServerThread(socket, dialogArea).start();

        sendBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {

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
