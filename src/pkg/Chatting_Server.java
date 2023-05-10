package pkg;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


class ConnectThread extends Thread{
    static List<Socket> socketList = new ArrayList<Socket>();
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("localhost",5001));//IP와 포트번호를 설정하는 역할을 한다.
            System.out.println("바인딩 완료");

            while(true) {
                Socket socket = serverSocket.accept();
                socketList.add(socket);
                System.out.println("누군가 접속됨..." + socketList.size());
                DataReceiveThread dt = new DataReceiveThread(socket);
                dt.start();
            }

        } catch (IOException e) {

        }
    }

    static public List<Socket> getSocketList() {
        return ConnectThread.socketList;
    }
}

class DataReceiveThread extends Thread{

    Socket socket;
    List<Socket> socketList;

    public DataReceiveThread(Socket socket) {
        // TODO Auto-generated constructor stub
        this.socket = socket;
    }

    public void run() {
        InputStream is = null;
        OutputStream os = null;
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            is = socket.getInputStream();
            ois = new ObjectInputStream(is);

            while(true) {
                //Read
                byte[] data = new byte[512];
                int size = is.read(data);//Blocking 함수.
                String result = new String(data, 0, size, "utf-8");
                System.out.println(size + " " + result);

                //Result를 모든 socket에게 전송하기.
                socketList = ConnectThread.getSocketList();
                String sending_message = result;
                byte[] sending_data = sending_message.getBytes();

                for (Socket currentSocket: socketList) {

                    os = currentSocket.getOutputStream();
//             oos = new ObjectOutputStream(os);
                    os.write(sending_data);
                    // os.close();

                }
            }
        } catch (IOException e) {
            socketList = ConnectThread.getSocketList();
            socketList.remove(socket);
            System.out.println("현재인원: "+ socketList.size());
            e.printStackTrace();
            try {
                is.close();
                os.close();
                socket.close();
                this.interrupt();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }
    }
}


public class Chatting_Server extends Application{

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage arg0) throws Exception {
        // TODO Auto-generated method stub
        VBox root = new VBox();//Vertical Box
        root.setPrefSize(400,300);// VBox의 크기. 가로 400 세로 300.
        ConnectThread connectThread = new ConnectThread();
        //TODO START
        Button btn1 = new Button("서버오픈");
        btn1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                connectThread.start();
            }
        });

        Button btn2 = new Button("데이터 전송");
        root.getChildren().addAll(btn1,btn2);
        //TODO FINISH
        Scene scene = new Scene(root);//장면이 찍힐 화면이 root이다.
        arg0.setScene(scene);
        arg0.setTitle("서버");
        arg0.show();// 창 띄우는거.
    }
}