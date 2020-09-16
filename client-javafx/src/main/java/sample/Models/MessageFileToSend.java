package sample.Models;

import java.io.Serializable;

public class MessageFileToSend implements Serializable {

    private Device from;
    private Device to;
    private byte[] content;

    public MessageFileToSend(Device from, Device to, byte[] content){
        this.from = from;
        this.to = to;
        this.content = content;
    }


    public byte[] getContent() {
        return content;
    }

    public Device getFrom() {
        return from;
    }

    public Device getTo() {
        return to;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setFrom(Device from) {
        this.from = from;
    }

    public void setTo(Device to) {
        this.to = to;
    }
}
