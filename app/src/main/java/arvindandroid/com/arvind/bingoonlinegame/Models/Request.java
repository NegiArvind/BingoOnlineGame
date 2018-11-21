package arvindandroid.com.arvind.bingoonlinegame.Models;

public class Request {

    public String to;
    public String from;
    public String fromName;
    public String requestAccept;

    public Request() {
    }

    public Request(String to, String from, String fromName, String requestAccept) {
        this.to = to;
        this.from = from;
        this.fromName = fromName;
        this.requestAccept = requestAccept;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getRequestAccept() {
        return requestAccept;
    }

    public void setRequestAccept(String requestAccept) {
        this.requestAccept = requestAccept;
    }
}
