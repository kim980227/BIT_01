package pkg;

import java.io.*;
import java.net.Socket;

public class FileTransfer {
    public static File file = null;
    FileTransfer(){}
    FileTransfer(File file){
        this.file = file;
    }
    public static void receiveFile(Socket socket) {
        try (InputStream inputStream = socket.getInputStream();
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {

            long fileSize = dataInputStream.readLong();
            String fileName = dataInputStream.readUTF();

            String savePath = "./savePath"; // 저장 위치 설정

            File directory = new File(savePath);
            // 디렉토리 없으면 만들기
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
            }
            File file = new File(directory, fileName); // 파일 생성


            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[8192];
                int byteRead;
                int sumOfByteRead = 0;

                // 1. 총 읽은 바이트가 파일사이즈보다 작고
                // 2. 인풋스트림에서 buffer 배열을 읽어와 byteRead에 할당
                // 3. 2가 -1(EOF?)가 아니면 계속 반복
                while (sumOfByteRead < fileSize && (byteRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, byteRead);
                    sumOfByteRead += byteRead;
                }
            }
            System.out.println("File received and saved: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendFile(Socket socket) {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             OutputStream outputStream = socket.getOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {

            String fileName = file.getName();
            long fileSize = file.length();
            // Send file size and name
            dataOutputStream.writeLong(fileSize);
            dataOutputStream.writeUTF(fileName);
            System.out.println(fileSize);
            System.out.println(fileName);

            // Send file data
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("File sent successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
