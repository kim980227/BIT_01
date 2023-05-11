package message;

import chatUser.Chat_User;

import java.io.File;
import java.io.Serializable;

public class Message implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String msg;

    private Chat_User user;

    private File file;

    private String fileName;

    private long fileSize;
    private Boolean isMute;
    public Chat_User getUser() {
        return user;
    }

    public void setUser(Chat_User user) {
        this.user = user;
    }

    private boolean notice;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setNotice(Boolean notice) {
        this.notice = notice;
    }

    public void setFile(File file, String fileName, long fileSize) {this.file = file; this.fileName=fileName; this.fileSize=fileSize;}
    public void setMute(Boolean isMute){this.isMute = isMute;}

    public String getMsg() {
        return msg;
    }

    public Boolean getNotice() {
        return notice;
    }

    public File getFile() { return file;}

    public long getFileSize() { return fileSize;}

    public String getFileName() { return fileName;}

    public Boolean getMute(){return isMute;}
}
