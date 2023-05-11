package threadClient;

import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientUserNameThread extends Thread {
    Socket socket;
    TextArea userArea;
    ObjectInputStream objectStream = null;

    public ClientUserNameThread(Socket socket, TextArea userArea) {
        this.socket = socket;
        this.userArea = userArea;
    }

    public void run() {
//        userArea.setText("하이");


        while (true) {
            try {
                objectStream = ClientDataReceiveThread.objectStream;
                Message message = (Message) objectStream.readObject();
                System.out.println(message.getUser().getName());
                System.out.println(message.getMsg());

                if (message.getUser().getName().equals("서버") && message.getMsg().contains("[")) {
                    userArea.setText(message.getMsg());
                }
            } catch (IOException e) {
                System.out.println("유저네임 에러발생. IO");
                break;

            } catch (ClassNotFoundException e) {
                System.out.println("클래스발견 x");
                break;
            }
        }


    }

}
