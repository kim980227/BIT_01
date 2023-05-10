package chatUser;

import java.net.Socket;

public class Chat_User {
    private String name;
    private int id;
    private boolean leader;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = true;
    }

}