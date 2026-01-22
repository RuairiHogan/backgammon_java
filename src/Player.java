// ******************************************************************
// Group number: 41
// Names: Patrick Kavanagh, Ruairi Hogan, Eoin Ryan
// GitHub IDs: pkav2, rh-ucd, JoeBahama
// ******************************************************************

public class Player {

    // Player Attributes
    private final String name;
    private final int player_num;
    private int score;

    // Constructor for initialising a player object
    public Player(String name, int player_num) {
        this.player_num = player_num;
        this.name = name;
        this.score = 0;
    }

    // Getter methods
    public String getName(){
        return name;
    }
    public int getPlayerNum(){
        return player_num;
    }
    public int getScore() {
        return score;
    }

    // Method to add points to the players score
    public void addScore(int points) {
        score += points;
    }
    //Resets  the score
    public void resetScore() {
        this.score = 0;
    }

}

