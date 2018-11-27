package arvindandroid.com.arvind.bingoonlinegame.Models;

public class Game {
    public int noOfGameIWin;
    public int totalGame;
    public int wantToPlayAgain; // 1 for no and 2 for yes and initially it will be zero
    public boolean myChance;
    public int noOfBingo;
    public int choosenNumber;

    public Game() {
    }

    public Game(int noOfGameIWin, int totalGame, int wantToPlayAgain, boolean myChance, int noOfBingo, int choosenNumber) {
        this.noOfGameIWin = noOfGameIWin;
        this.totalGame = totalGame;
        this.wantToPlayAgain = wantToPlayAgain;
        this.myChance = myChance;
        this.noOfBingo = noOfBingo;
        this.choosenNumber = choosenNumber;
    }

    public int getChoosenNumber() {
        return choosenNumber;
    }

    public void setChoosenNumber(int choosenNumber) {
        this.choosenNumber = choosenNumber;
    }

    public int getNoOfGameIWin() {
        return noOfGameIWin;
    }

    public void setNoOfGameIWin(int noOfGameIWin) {
        this.noOfGameIWin = noOfGameIWin;
    }

    public int getTotalGame() {
        return totalGame;
    }

    public void setTotalGame(int totalGame) {
        this.totalGame = totalGame;
    }

    public int isWantToPlayAgain() {
        return wantToPlayAgain;
    }

    public void setWantToPlayAgain(int wantToPlayAgain) {
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
