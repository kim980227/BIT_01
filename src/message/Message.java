package message;

import chatUser.Chat_User;

import java.io.Serializable;

public class Message implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String msg;
    private Chat_User user;

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


    public String getMsg() {
        return msg;
    }

    public Boolean getNotice() {
        return notice;
    }


}
