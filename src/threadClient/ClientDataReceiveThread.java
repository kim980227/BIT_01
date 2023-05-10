package threadClient;

import javafx.scene.control.TextArea;
import message.Message;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientDataReceiveThread  extends Thread{
    private Socket socket;
    private TextArea dialogArea;
    InputStream inputStream;
    private ObjectInputStream objectInputStream;
    public ClientDataReceiveThread(Socket socket, TextArea dialogArea){
        System.out.println("Constructor");
        this.socket = socket;
        this.dialogArea = dialogArea;
        System.out.println("Hi");
    }

    @Override
    public void run(){
        try{
            this.inputStream = socket.getInputStream();
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Done");
            while(true) {
                System.out.println("run method while");
                Message message = (Message)objectInputStream.readObject();
                System.out.println(message.getUser().getName() + " "+ message.getMsg());
                dialogArea.appendText(message.getUser().getName()+"\n");
                dialogArea.appendText(message.getMsg()+"\n");
                dialogArea.appendText("\n");

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}