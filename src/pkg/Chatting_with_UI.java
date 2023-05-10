package pkg;

import chatUser.Chat_User;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import message.Message;

import java.io.*;
import java.net.InetSocketAddress;

class ServerThread extends Thread{
    Chat_User chatUser;
    TextArea dialogArea;
    ObjectInputStream objectStream = null;
    ServerThread(Chat_User chatUser, TextArea dialogArea){
        this.chatUser = chatUser;
        this.dialogArea = dialogArea;
    }

    @Override
    public void run(){
        try{
            objectStream = new ObjectInputStream(chatUser.getInputStream());
            while(true) {

                Message message  = (Message) objectStream.readObject();

                dialogArea.appendText(message.getMsg()+"\n");

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}

public class Chatting_with_UI extends Application {
    Chat_User chatUser = null;
    OutputStream os = null;
    ObjectOutputStream oos = null;
    @Override
    public void start(Stage arg0) throws Exception {
        //Set Nickname
        VBox setNicknameBox = new VBox();
        setNicknameBox.setPrefSize(300, 500);

        TextField nicknameField = new TextField();
        Button setNicknameBtn = new Button("닉네임 설정");

        setNicknameBox.getChildren().addAll(nicknameField,setNicknameBtn);
        Scene setNicknameScene = new Scene(setNicknameBox);

        //Dialog Scene-----------------------------
        VBox dialogBox = new VBox();
        dialogBox.setPrefSize(300, 500);

        Button sendBtn = new Button("전송");
        sendBtn.setLayoutY(300);

        Button sendFileBtn = new Button("파일 전송");
        FileChooser fileChooser = new FileChooser();
        TextField messageField = new TextField();
        TextArea dialogArea = new TextArea();

        dialogBox.getChildren().addAll(dialogArea,sendBtn, sendFileBtn, messageField);
        Scene dialogScene = new Scene(dialogBox);

        //----------------------------------
        setNicknameBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    chatUser = new Chat_User();
                    chatUser.connect(new InetSocketAddress("localhost", 5001));

                    new ServerThread(chatUser, dialogArea).start();

                    oos = new ObjectOutputStream(chatUser.getOutputStream());

                    String nickname = nicknameField.getText();
                    chatUser.setName(nickname);

                    Message message = new Message();
                    message.setName(nickname);
                    message.setMsg(nickname+"님, 환영합니다.");
                    message.setNotice(false);

                    oos.writeObject(message);

                    arg0.setTitle("대화창");
                    arg0.setScene(dialogScene);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        sendBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {
                    String msg = messageField.getText();
                    Message message = new Message();
                    message.setName(chatUser.getName());
                    message.setMsg(msg);
                    message.setNotice(false);

                    oos.writeObject(message);
                    messageField.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        arg0.setScene(setNicknameScene);
        arg0.setTitle("닉네임 설정");
        arg0.show();

    }

    public static void main(String[] args) {
        launch();
    }
}