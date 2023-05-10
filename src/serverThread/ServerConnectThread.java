package serverThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerConnectThread extends Thread{
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
                ServerDataReceiveThread dt = new ServerDataReceiveThread(socket);
                dt.start();
            }

        } catch (IOException e) {

        }
    }

    static public List<Socket> getSocketList() {
        return ServerConnectThread.socketList;
    }
}