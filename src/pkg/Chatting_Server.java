package pkg;


import threadServer.ServerConnectThread;

public class Chatting_Server{

    public static void main(String[] args) {
        ServerConnectThread connectThread = new ServerConnectThread();
        connectThread.start();
    }

}