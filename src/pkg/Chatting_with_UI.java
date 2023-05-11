package pkg;

import chatUser.Chat_User;
import message.Message;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;


public class Chatting_with_UI extends Application {
    Socket socket = null;
    OutputStream os = null;
    ObjectOutputStream oos = null;
    @Override
    public void start(Stage arg0) throws Exception {
        //Set Nickname
        GridPane setNicknameBox = new GridPane();
        setNicknameBox.setMinSize(300, 100);
        setNicknameBox.setPadding(new Insets(10,10,10,10));
        setNicknameBox.setVgap(5);
        setNicknameBox.setHgap(5);
        setNicknameBox.setAlignment(Pos.CENTER);
        Chat_User user = new Chat_User();


        TextField nicknameField = new TextField();
        Button setNicknameBtn = new Button("닉네임 설정");

        setNicknameBox.add(nicknameField, 0, 0);
        setNicknameBox.add(setNicknameBtn, 1, 0);

//        setNicknameBox.getChildren().addAll(nicknameField,setNicknameBtn);
        Scene setNicknameScene = new Scene(setNicknameBox);

        //Dialog Scene-----------------------------
        GridPane dialogBox = new GridPane();
        dialogBox.setMinSize(300, 300);
        dialogBox.setPadding(new Insets(10,10,10,10));
        dialogBox.setVgap(10); //
        dialogBox.setHgap(10);

        Button sendBtn = new Button("전송");
        sendBtn.setLayoutY(300);

        CheckBox chkNoti = new CheckBox();


        Button sendFileBtn = new Button("파일 전송");
        FileChooser fileChooser = new FileChooser();
        TextField messageField = new TextField();
        TextArea noticeArea = new TextArea();
        TextArea dialogArea = new TextArea();
        TextArea userArea = new TextArea();


        //*******************************************
        //공지사항 UI
//        noticeArea.setMinSize(100, 100);
        //시작 멘트
        noticeArea.setText("*공지사항*");
        //크기 설정
        noticeArea.setPrefSize(500, 50);
        //직접 삭제 못하게 하는 코드
        noticeArea.setEditable(false);
        //*******************************************
        //채팅창 UI
        //직접 삭제 못하게 하는 코드
        dialogArea.setEditable(false);

        //*******************************************

        //*******************************************
        //채팅창 UI
        //직접 삭제 못하게 하는 코드

        //*******************************************
        //
        userArea.setPrefSize(100, 50);
        //*******************************************
        dialogBox.add(noticeArea,0, 0);
        HBox userpart = new HBox();
        userpart.setSpacing(10);
        userpart.getChildren().addAll(dialogArea, userArea);
        dialogBox.add(userpart,0, 1);

        dialogBox.add(sendFileBtn,0,3);

        HBox.setHgrow(messageField, Priority.ALWAYS);
        HBox sendpart = new HBox();
        sendpart.setSpacing(10);
        sendpart.getChildren().addAll(messageField, chkNoti, sendBtn);
        dialogBox.add(sendpart,0,2);



        Scene dialogScene = new Scene(dialogBox);

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("텍스트를 입력해주세요!");
        //----------------------------------

        // messageField에 있는 내용을 Enter로 보내는 코드
        messageField.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                try {
                    String msg = messageField.getText();
                    if(!msg.isEmpty()){
                        Message message = new Message();

                        /*해당부분 message 에 user 삽입 하는 것으로
                        message.setName(nickname);
                        */
                        String enter_msg = messageField.getText();

                        message.setUser(user);
                        message.setMsg(enter_msg);
                        message.setFile(null, null,0);
                        if (chkNoti.isSelected()){message.setNotice(true);}
                        else{message.setNotice(false);}

                        oos.writeObject(message);
                        chkNoti.setSelected(false);
                        messageField.setText("");}
                    else{
                        a.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        setNicknameBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    String nickname = nicknameField.getText();

                    if (!nickname.isEmpty()) {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress("localhost", 5001));


                        new ClientDataReceiveThread(socket, noticeArea, dialogArea, userArea).start();

                        oos = new ObjectOutputStream(socket.getOutputStream());

                        user.setName(nickname);

                        Message message = new Message();
    /*
    해당부분 message 에 user 삽입 하는 것으로
                        message.setName(nickname);
    */
                        message.setMsg(nickname + "님, 환영합니다.");
                        message.setUser(user);
                        message.setNotice(false);
                        message.setFile(null, null, 0);

                        oos.writeObject(message);

                        arg0.setTitle("대화창");
                        arg0.setScene(dialogScene);
                    }
                    else{
                        a.show();
                    }
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
                    if(!msg.isEmpty()){
                        Message message = new Message();

                        /*해당부분 message 에 user 삽입 하는 것으로
                        message.setName(nickname);
                        */
                        message.setUser(user);
                        message.setMsg(msg);
                        message.setFile(null, null,0);
                        if (chkNoti.isSelected()){message.setNotice(true);}
                        else{message.setNotice(false);}

                        oos.writeObject(message);
                        chkNoti.setSelected(false);
                        messageField.setText("");}
                    else{
                        a.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        sendFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    File file = fileChooser.showOpenDialog(arg0);

                    Message message = new Message();
                    message.setUser(user);
                    message.setMsg("");
                    message.setFile(file, file.getName(), file.length());
                    message.setNotice(false);

                    oos.writeObject(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        arg0.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try{
                    Message message = new Message();
                    message.setUser(user);
                    message.setMsg(user.getName()+"님이 퇴장하셨습니다.");

                    oos.writeObject(message);
                }catch(Exception e){
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