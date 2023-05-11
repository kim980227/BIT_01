package pkg;

import chatUser.Chat_User;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
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

    Text title = new Text("Chatting title");
    Text userNumber = new Text("0");
    Region filler = new Region();
    Button sendBtn = new Button("전송");
    CheckBox chkNoti = new CheckBox();
    Button sendFileBtn = new Button("파일 전송");
    FileChooser fileChooser = new FileChooser();
    TextField messageField = new TextField();
    Text noticeArea = new Text("이거는 Notice입니다.");
    TextArea dialogArea = new TextArea();

    HBox makeHeader(){
        HBox header = new HBox();
        header.setPadding(new Insets(20, 10, 20, 10));
        title = new Text("Chatting title");
        userNumber = new Text("0");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        userNumber.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        filler = new Region();
        HBox.setHgrow(filler, Priority.ALWAYS);
        header.getChildren().addAll(title, filler, userNumber);
        header.setStyle("-fx-background-color: #336699;");
        return header;
    }

    VBox makeContent(){
        VBox dialogBox = new VBox();
        dialogBox.setPrefSize(300, 500);
        sendBtn = new Button("전송");
        sendBtn.setLayoutY(300);
        chkNoti = new CheckBox();
        sendFileBtn = new Button("파일 전송");
        fileChooser = new FileChooser();
        messageField = new TextField();
        noticeArea = new Text("이거는 Notice입니다.");
        dialogArea = new TextArea();
        dialogArea.setEditable(false);
        HBox sending_part = new HBox();
        HBox.setHgrow(messageField, Priority.ALWAYS);

        sending_part.getChildren().addAll(messageField,sendBtn);
        dialogBox.getChildren().addAll(noticeArea, dialogArea,sending_part);

        return dialogBox;

    }
    @Override
    public void start(Stage arg0) throws Exception {
        //Set Nickname
        VBox setNicknameBox = new VBox();
        setNicknameBox.setPrefSize(300, 500);
        Chat_User user = new Chat_User();

        TextField nicknameField = new TextField();
        Button setNicknameBtn = new Button("닉네임 설정");

        setNicknameBox.getChildren().addAll(nicknameField,setNicknameBtn);
        Scene setNicknameScene = new Scene(setNicknameBox);

        //Dialog Scene-----------------------------
        BorderPane border = new BorderPane();

        border.setTop(makeHeader());
        border.setCenter(makeContent());
        Scene dialogScene = new Scene(border);

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("텍스트를 입력해주세요!");
        //----------------------------------
        setNicknameBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    String nickname = nicknameField.getText();

                    if (!nickname.isEmpty()) {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress("localhost", 5001));

                        new ClientDataReceiveThread(socket, noticeArea, dialogArea).start();

                        oos = new ObjectOutputStream(socket.getOutputStream());

                        user.setName(nickname);

                        Message message = new Message();
    /*
    해당부분 message 에 user 삽입 하는 것으로
                        message.setName(nickname);
    */
                        message.setMsg(nickname+"님, 환영합니다.");
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
                } catch (IOException e) {
                    System.out.println("서버와의 접속이 종료");

                    e.printStackTrace();

                    //TODO: 서버와의 연결이 종료되면 굳이 프로세스가 실행될 필요가 없다. 종료..!
                    System.exit(-1);

                } catch (Exception e2) {
                    System.out.println("기타 에러 발생");
                    e2.printStackTrace();
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

        arg0.setScene(setNicknameScene);
        arg0.setTitle("닉네임 설정");
        arg0.show();

    }

    public static void main(String[] args) {
        launch();
    }
}