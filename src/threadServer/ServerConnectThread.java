package threadServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerConnectThread extends Thread{
    /*
    * 여기서 방장관리를 해야할까.
    * static으로 방장관리를 해야할 것 같다.
    * */

    static Socket leader;
    static HashMap<Socket, ObjectOutputStream> socketList = new HashMap<>();
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("localhost",5001));//IP와 포트번호를 설정하는 역할을 한다.
            System.out.println("바인딩 완료");

            while(true) {
                Socket socket = serverSocket.accept();
                socketList.put(socket, new ObjectOutputStream(socket.getOutputStream()));
                if(socketList.isEmpty()) leader = socket;

                System.out.println("누군가 접속됨..." + socketList.size());
                ServerDataReceiveThread dt = new ServerDataReceiveThread(socket);
                dt.start();
            }

        } catch (IOException e) {

        }
    }

    static public HashMap<Socket, ObjectOutputStream> getSocketList() {
        return ServerConnectThread.socketList;
    }
}