package threadClient;

import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import message.Message;

import java.io.*;
import java.net.Socket;

public class ClientDataReceiveThread extends Thread{
    Socket socket;
    TextArea noticeArea;
    TextArea dialogArea;
    static ObjectInputStream objectStream;
    FileInputStream fileInputStream = null;
    String savePath = "./savePath";
    TextArea userArea;
    public ClientDataReceiveThread(Socket socket, TextArea noticeArea, TextArea dialogArea, TextArea userArea){
        this.socket = socket;
        this.noticeArea = noticeArea;
        this.dialogArea = dialogArea;
        this.userArea = userArea;
    }

    @Override
    public void run(){
        try{
            objectStream = new ObjectInputStream(socket.getInputStream());
            while(true) {
                Message message  = (Message) objectStream.readObject();
                File file = message.getFile();

                if (file!=null){
                    long fileSize = message.getFileSize();
                    String fileName = message.getFileName();

                    File directory = new File(savePath);

                    if(!directory.exists()){
                        directory.mkdirs();
                    }

                    File createFile = new File(directory, fileName);

                    try (FileOutputStream fileOutputStream = new FileOutputStream(createFile)) {
                        byte[] buffer = new byte[8192];
                        int byteRead = 0;

                        // 1. 총 읽은 바이트가 파일사이즈보다 작고
                        // 2. 인풋스트림에서 buffer 배열을 읽어와 byteRead에 할당
                        // 3. 2가 -1(EOF?)가 아니면 계속 반복
                        fileInputStream = new FileInputStream(file);
                        while ((byteRead = fileInputStream.read()) != -1) {
                            fileOutputStream.write(byteRead);
                        }

                        dialogArea.appendText(message.getUser().getName()+"님께서 보낸 파일이 ./savePath 폴더에 저장되었습니다.\n");
                    }
                }

                else{
                    if(message.getUser().getName().equals("서버")){
                        userArea.setText(message.getMsg());
                    }
                    else if(!message.getNotice()) dialogArea.appendText(message.getMsg()+"\n");
                    else noticeArea.setText(message.getMsg());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File 존재하지 않음");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("서버와의 연결 종료");
            System.exit(-1);
            throw new RuntimeException(e);

        } catch (ClassNotFoundException e) {
            System.out.println("클래스 캐스팅이 잘못되었음");
            throw new RuntimeException(e);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}