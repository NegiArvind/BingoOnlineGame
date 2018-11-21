package arvindandroid.com.arvind.bingoonlinegame.Models;

public class Game {
    public int noOfGame;
    public int totalGame;
    public boolean wantToPlayAgain;
    public boolean myChance;
    public int noOfBingo;

    public Game() {
    }

    public Game(int noOfGame, int totalGame, boolean wantToPlayAgain, boolean myChance, int noOfBingo) {
        this.noOfGame = noOfGame;
        this.totalGame = totalGame;
        this.wantToPlayAgain = wantToPlayAgain;
        this.myChance = myChance;
        this.noOfBingo = noOfBingo;
    }

    public int getNoOfGame() {
        return noOfGame;
    }

    public void setNoOfGame(int noOfGame) {
        this.noOfGame = noOfGame;
    }

    public int getTotalGame() {
        return totalGame;
    }

    public void setTotalGame(int totalGame) {
        this.totalGame = totalGame;
    }

    public boolean isWantToPlayAgain() {
        return wantToPlayAgain;
    }

    public void setWantToPlayAgain(boolean wantToPlayAgain) {
        this.wantToPlayAgain = wantToPlayAgain;
    }

    public boolean isMyChance() {
        return myChance;
    }

    public void setMyChance(boolean myChance) {
        this.myChance = myChance;
    }

    public int getNoOfBingo() {
        return noOfBingo;
    }

    public void setNoOfBingo(int noOfBingo) {
        this.noOfBingo = noOfBingo;
    }
}
