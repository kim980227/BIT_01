package pkg;

import chatUser.Chat_User;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    FileInputStream fileInputStream = null;
    String savePath = "./savePath";
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
                File file = message.getFile();

                if (file!=null){
                    long fileSize = message.getFileSize();
                    String fileName = message.getFileName();

                    File directory = new File(savePath);

                    if(!directory.exists()){
                        directory.mkdirs();
                    }

                    File createFile = new File(directory, fileName);

                    try (FileOutputStream fileOutputStream = new FileOutputStream(createFile)) {
                        byte[] buffer = new byte[8192];
                        int byteRead = 0;

                        // 1. 총 읽은 바이트가 파일사이즈보다 작고
                        // 2. 인풋스트림에서 buffer 배열을 읽어와 byteRead에 할당
                        // 3. 2가 -1(EOF?)가 아니면 계속 반복
                        fileInputStream = new FileInputStream(file);
                        while ((byteRead = fileInputStream.read()) != -1) {
                            fileOutputStream.write(byteRead);
                        }

                        dialogArea.appendText(message.getUser().getName()+"님께서 보낸 파일이 ./savePath 폴더에 저장되었습니다.\n");
                    }
                }

                else{
                    if (!message.getNotice())
                    {dialogArea.appendText(message.getMsg()+"\n");}
                    else{
                        noticeArea.setText(message.getMsg());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File 존재하지 않음");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("서버와의 연결 종료");
            System.exit(-1);
            throw new RuntimeException(e);

        } catch (ClassNotFoundException e) {
            System.out.println("클래스 캐스팅이 잘못되었음");
            throw new RuntimeException(e);
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
        Chat_User user = new Chat_User();

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

                        new ServerThread(socket, noticeArea, dialogArea).start();

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