package pkg;

import chatUser.Chat_User;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import message.Message;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

class ServerThread extends Thread{
    Socket socket;
    TextArea noticeArea;
    TextArea dialogArea;
    ObjectInputStream objectStream = null;
    ServerThread(Socket socket, TextArea noticeArea, TextArea dialogArea){
        this.socket = socket;
        this.noticeArea = noticeArea;
        this.dialogArea = dialogArea;
    }

    @Override
    public void run(){
        try{
            objectStream = new ObjectInputStream(socket.getInputStream());
            while(true) {
                Message message  = (Message) objectStream.readObject();

                if (!message.getNotice())
                {dialogArea.appendText(message.getMsg()+"\n");}
                else{
                    noticeArea.setText(message.getMsg());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}

public class Chatting_with_UI extends Application {
    Socket socket = null;
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

        CheckBox chkNoti = new CheckBox();

        Button sendFileBtn = new Button("파일 전송");
        FileChooser fileChooser = new FileChooser();
        TextField messageField = new TextField();
        TextArea noticeArea = new TextArea();
        TextArea dialogArea = new TextArea();

        dialogBox.getChildren().addAll(noticeArea, dialogArea,sendBtn, sendFileBtn, messageField, chkNoti);
        Scene dialogScene = new Scene(dialogBox);

        //----------------------------------
        setNicknameBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress("localhost", 5001));

                    new ServerThread(socket, noticeArea, dialogArea).start();

                    oos = new ObjectOutputStream(socket.getOutputStream());

                    String nickname = nicknameField.getText();

                    Message message = new Message();
/*
해당부분 message 에 user 삽입 하는 것으로
                    message.setName(nickname);
*/
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
/*해당부분 message 에 user 삽입 하는 것으로
                    message.setName(nickname);
*/                    message.setMsg(msg);
                    if (chkNoti.isSelected()){message.setNotice(true);}
                    else{message.setNotice(false);}

                    oos.writeObject(message);
                    chkNoti.setSelected(false);
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