package threadClient;

import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientUserNameThread extends Thread{
    Socket socket;
    TextArea userArea;
    ObjectInputStream objectStream = null;
    public ClientUserNameThread(Socket socket, TextArea userArea){
        this.socket = socket;
        this.userArea = userArea;
    }

    public void run(){
        try {
            objectStream = new ObjectInputStream(socket.getInputStream());
            while(true){
                Message message = (Message)objectStream.readObject();
                if(message.getUser().getName().equals("서버") && message.getMsg().contains("[")){
                    userArea.setText(message.getMsg());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

}
