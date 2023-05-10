package threadServer;

import message.Message;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

class ServerDataReceiveThread extends Thread{

    Socket socket;
    HashMap<Socket, ObjectOutputStream> socketList;

    public ServerDataReceiveThread(Socket socket) {
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

                Message message  = (Message) ois.readObject();//Blocking 함수.
                System.out.println("작성자: "+ message.getName() + " 내용: "+ message.getMsg());

                //Result를 모든 socket에게 전송하기.
                socketList = ServerConnectThread.getSocketList();

                //Write
                for (Socket currentSocket: socketList.keySet()) {
//                    os = currentSocket.getOutputStream();
//                    oos = new ObjectOutputStream(os);
                    socketList.get(currentSocket).writeObject(message);
//                    oos.writeObject(message);
                    System.out.println("Writing data Finish: ");
                    // os.close();

                }
            }
        } catch (ClassNotFoundException e1) {
            System.out.println("메시지 객체 타입 오류");
            e1.printStackTrace();
        }
        catch(IOException e2){
            System.out.println("클라이언트와의 접속이 종료");
            socketList = ServerConnectThread.getSocketList();
            socketList.remove(socket);
            System.out.println("현재인원: "+ socketList.size());
            e2.printStackTrace();
            try {
                is.close();
                os.close();
                socket.close();
                this.interrupt();
            } catch (Exception e3) {
                // TODO Auto-generated catch block
                e3.printStackTrace();
            }
        }
    }
}