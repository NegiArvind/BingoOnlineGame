package arvindandroid.com.arvind.bingoonlinegame.Models;

public class User {

    public String username;
    public String imageUrl;
    public boolean online;
    public Game game;
    public Request request;
    public static User currentUser;

    public User(String username, String imageUrl, boolean online, Game game, Request request) {
        this.username = username;
        this.imageUrl = imageUrl;
        this.online = online;
        this.game = game;
        this.request = request;
    }

    public User(String username, String imageUrl) {
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public User() {
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public static void setCurrentUser(String username,String imageUrl)
    {
        currentUser=new User(username,imageUrl);
    }

    public static User getCurrentUser(){
        if(currentUser!=null)
            return currentUser;
        return null;
    }

}
