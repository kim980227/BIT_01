package message;

import java.io.Serializable;

public class Message implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String msg;
    private String name;
    private boolean notice;

    public void setName(String name) {
        this.name = name;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setNotice(Boolean notice) {
        this.notice = notice;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }

    public Boolean getNotice() {
        return notice;
    }


}
