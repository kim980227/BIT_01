package threadServer;

import chatUser.Chat_User;
import message.Message;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

class ServerDataReceiveThread extends Thread{

    Socket socket;
    HashMap<Socket, ObjectOutputStream> socketList;

    ThreadLocal<Boolean> identify = new ThreadLocal<Boolean>();
    Chat_User user = null;
    InputStream is = null;
    ObjectInputStream ois = null;

    public ServerDataReceiveThread(Socket socket) {
        // TODO Auto-generated constructor stub
        this.socket = socket;
    }

    public void IOExceptionHandler(Exception e){
        System.out.println("클라이언트와의 접속이 종료");
        socketList = ServerConnectThread.getSocketList();
        socketList.remove(socket);
        System.out.println("현재인원: "+ socketList.size());
        if((ServerConnectThread.leader == socket) && (ServerConnectThread.socketList.size() >= 1)){
            ServerConnectThread.leader = ServerConnectThread.socketList.keySet().stream().findFirst().get();
            System.out.println("방장변경...");
            System.out.println(ServerConnectThread.leader);
            ServerConnectThread.leaderChanged=true;
        }
        e.printStackTrace();

        try {
            is.close();
            socket.close();
            this.interrupt();

        } catch (Exception e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }
    }


    public void sendToAll(Message message){
        socketList = ServerConnectThread.getSocketList();
        message.setUser(user);
        message.setMsg(message.getUser().getName() + ": "+message.getMsg());
        System.out.println("이름: "+message.getUser().getName());
        System.out.println("메시지:" + message.getMsg());
        //Write
        for (Socket currentSocket: socketList.keySet()) {
            try {
                socketList.get(currentSocket).writeObject(message);
            } catch (IOException e) {
                IOExceptionHandler(e);
            }
            System.out.println("Writing data Finish: ");
        }
    }

    public void checkIdentify(Message message){
        if(!identify.get() ){
            user = message.getUser();
            ServerConnectThread.name_socket_mapper.put(user.getName(), socket);
            if (ServerConnectThread.leader == socket){
                user.setName(user.getName()+"(방장)");
            }
            identify.set(true);
            ServerConnectThread.leaderChanged=false;
        }
    }

    public void checkLeader(){
        if(ServerConnectThread.leaderChanged){
            if (ServerConnectThread.leader == socket){
                user.setName(user.getName()+"(방장)");
            }
            ServerConnectThread.leaderChanged=false;
        }
    }


    public void run() {
        identify.set(false);
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            while(true) {
                //Read
                Message message  = (Message) ois.readObject();//Blocking 함수.
                System.out.println("작성자: "+ message.getUser().getName() + " 내용: "+ message.getMsg());
                checkIdentify(message);
                checkLeader();
                sendToAll(message);
                //Result를 모든 socket에게 전송하기.
            }
        } catch (ClassNotFoundException e1) {
            System.out.println("메시지 객체 타입 오류");
            e1.printStackTrace();
        }
        catch(IOException e2){
            IOExceptionHandler(e2);
        }
    }
}