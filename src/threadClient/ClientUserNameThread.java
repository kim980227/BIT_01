package threadClient;

import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientUserNameThread extends Thread{
    Socket socket;
    TextArea userList;
    ObjectInputStream objectStream = null;
    public ClientUserNameThread(Socket socket, TextArea userList){
        this.socket = socket;
        this.userList = userList;
    }

    public void run(){
        try {
            objectStream = new ObjectInputStream(socket.getInputStream());
            while(true){
                Message message = (Message)objectStream.readObject();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

}
