package threadServer;

import chatUser.Chat_User;
import message.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ServerDataReceiveThread extends Thread{

    Socket socket;
    HashMap<Socket, ObjectOutputStream> socketList;

    ThreadLocal<Boolean> identify = new ThreadLocal<Boolean>();
    Chat_User user = null;
    InputStream is = null;
    ObjectInputStream ois = null;

    boolean isOrder = false;

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

    public void mute(Message message,String nickname) {
        if (ServerConnectThread.name_socket_mapper.keySet().contains(nickname)){
            if(ServerConnectThread.name_socket_mapper.get(nickname)!=ServerConnectThread.leader){
                message.setMsg("관리자에 의해 대화가 금지되었습니다.");
                message.setMute(true);
                try {
                    socketList.get(ServerConnectThread.name_socket_mapper.get(nickname)).writeObject(message);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                message.setMsg("관리자 본인을 차단할 수 없습니다.");
                try {
                    socketList.get(ServerConnectThread.leader).writeObject(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            message.setMsg("해당 닉네임을 사용하는 참가자가 존재하지 않습니다.");
            try {
                socketList.get(ServerConnectThread.leader).writeObject(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearMute(Message message, String target){
        if (ServerConnectThread.name_socket_mapper.keySet().contains(target)){
            if(ServerConnectThread.name_socket_mapper.get(target)!=ServerConnectThread.leader){
                message.setMsg("대화 차단이 해제되었습니다.");
                message.setMute(false);
                try {
                    socketList.get(ServerConnectThread.name_socket_mapper.get(target)).writeObject(message);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                message.setMsg("관리자 본인을 차단할 수 없습니다.");
                try {
                    socketList.get(ServerConnectThread.leader).writeObject(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            message.setMsg("해당 닉네임을 사용하는 참가자가 존재하지 않습니다.");
            try {
                socketList.get(ServerConnectThread.leader).writeObject(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void whisper(Message message, String[] target) {
        if(target.length>2) {
            if (ServerConnectThread.name_socket_mapper.keySet().contains(target[1])) {
                if (ServerConnectThread.name_socket_mapper.get(target[1]) != socket) {
                    socketList = ServerConnectThread.getSocketList();
                    String content = "";
                    for (int i = 2; i < target.length; i++) {
                        content += target[i];
                    }
                    message.setUser(user);
                    message.setMsg(message.getUser().getName() + "에게만: " + content);
                    System.out.println("w이름: " + message.getUser().getName());
                    System.out.println("w메시지:" + message.getMsg());
                    //Write
                    try {
                        socketList.get(socket).writeObject(message);
                        socketList.get(ServerConnectThread.name_socket_mapper.get(target[1])).writeObject(message);
                    } catch (IOException e) {
                        IOExceptionHandler(e);
                    }
                    System.out.println("Writing whisper data Finish: ");
                } else {
                    message.setMsg("본인에게 귓속말을 보낼 수 없습니다..");
                    try {
                        socketList.get(socket).writeObject(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                message.setMsg("해당 닉네임을 사용하는 참가자가 존재하지 않습니다.");
                try {
                    socketList.get(socket).writeObject(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            message.setMsg("전달할 내용이 존재하지 않습니다.");
            try {
                socketList.get(socket).writeObject(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void mandate(Message message){
        Random pickLeader = new Random();
        Object[] participants = ServerConnectThread.name_socket_mapper.keySet().toArray();
        ArrayList<Object> candidates = new ArrayList<Object>();
        for(int i=0;i<participants.length;i++){
            if(!participants[i].equals(user.getName().replace("(방장)", ""))){
                candidates.add(participants[i]);
            }
        }

        if(candidates.size()>0) {
            String newLeader = (String) candidates.get(pickLeader.nextInt(candidates.size()));

            ServerConnectThread.leader = ServerConnectThread.name_socket_mapper.get(newLeader);
            ServerConnectThread.leaderChanged=true;

            try {
                message.setMsg("방장으로 임명되셨습니다.");
                socketList.get(ServerConnectThread.leader).writeObject(message);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            try {
                message.setMsg("현재 다른 참여자가 존재하지 않습니다.");
                socketList.get(ServerConnectThread.leader).writeObject(message);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void getOut(Message message, String target) {
        if (ServerConnectThread.name_socket_mapper.get(target)!=ServerConnectThread.leader){
            if (ServerConnectThread.name_socket_mapper.keySet().contains(target)){
                Socket opponent = ServerConnectThread.name_socket_mapper.get(target);
                try {
                    ServerConnectThread.socketList.remove(opponent);
                    opponent.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                message.setMsg("해당 닉네임을 사용하는 참가자가 존재하지 않습니다.");
                try {
                    socketList.get(ServerConnectThread.leader).writeObject(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            message.setMsg("방장 본인을 퇴장시킬 수 없습니다.");
            try {
                socketList.get(ServerConnectThread.leader).writeObject(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public boolean command(String command) {
        List<String> commandList = new ArrayList<>();
        String pattern = "(^/)([가-힣]+)";
        Matcher matcher = Pattern.compile(pattern).matcher(command);
        return matcher.find();
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
                isOrder = command(message.getMsg());


                if(isOrder) {
                    String[] param = message.getMsg().split("\\s+");
                    if(ServerConnectThread.leader == socket) {
                        if(param.length>1){
                            if (param[0].equals("/강퇴")) {
                                getOut(message, param[1]);
                            }
                            else if (param[0].equals("/차단")) {
                                mute(message, param[1]);
                            }
                            else if (param[0].equals("/차단해제")) {
                                clearMute(message, param[1]);
                            }
                            else{
                                sendToAll(message);
                            }
                        }
                        else {
                            if (param[0].equals("/위임")) {
                                mandate(message);
                            }
                            else{
                                sendToAll(message);
                            }
                        }
                    }
                    else{
                        if(param.length>1){
                            if (param[0].equals("/귓속말")) {
                                whisper(message, param);
                            }
                        }
                        else{
                            sendToAll(message);
                        }
                    }
                }
                else{
                    sendToAll(message);
                }

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