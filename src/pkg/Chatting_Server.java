package pkg;


import threadServer.ServerConnectThread;
import threadServer.ServerSendUserList;

public class Chatting_Server{

    public static void main(String[] args) {
        ServerConnectThread connectThread = new ServerConnectThread();
        connectThread.start();
        System.out.println("Connection 끝");
//        ServerSendUserList serverSendUserList = new ServerSendUserList();
//        serverSendUserList.start();
//        System.out.println("유저 스레드 생성 끝");
    }

}
