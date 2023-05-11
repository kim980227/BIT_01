package threadServer;

import chatUser.Chat_User;
import message.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ServerSendUserList extends Thread {

    public void run(){
        while(true){
            HashMap<Socket, ObjectOutputStream> clients = ServerConnectThread.socketList;
            if(clients.isEmpty()) {
                System.out.println("client empty");
                continue;
            }
            else{
                System.out.println("Have Client");
                for (Socket socket: clients.keySet()) {
                    Message message = new Message();
                    Chat_User user = new Chat_User();
                    user.setName("서버");
                    message.setMsg(ServerConnectThread.name_socket_mapper.keySet().toString());
                    message.setUser(user);
                    System.out.println(message.getUser().getName());
                    try {
                        clients.get(socket).writeObject(message);
                        System.out.println("message 전송 끝");
                    } catch (IOException e) {
                        System.out.println("유저 전송중 에러발생");
                        throw new RuntimeException(e);
                    }
                }
            }

        }

    }
}
