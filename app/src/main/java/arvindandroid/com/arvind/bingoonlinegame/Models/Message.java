package arvindandroid.com.arvind.bingoonlinegame.Models;

import java.io.Serializable;

public class Message{

    public String message;
    public boolean seen;
    public boolean mine;//if the message is send by me then min will be true otherwise false.

    public Message() {
    }

    public Message(String message, boolean seen,boolean mine) {
        this.message = message;
        this.seen = seen;
        this.mine=mine;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Message))
            return false;
        Message message1=(Message)obj;
        return message1.getMessage().equalsIgnoreCase(this.getMessage()) &&
                message1.isMine()==this.isMine() && message1.isSeen()==this.isSeen();
    }
}
