// ******************************************************************
// Group number: 41
// Names: Patrick Kavanagh, Ruairi Hogan, Eoin Ryan
// GitHub IDs: pkav2, rh-ucd, JoeBahama
// ******************************************************************

/**
 * The MatchTracker class is responsible for managing the state and progression of a match,
 * typically tracking scores, managing the doubling cube, and determining game outcomes based on players' scores and game rules.
 * This class handles the logic for doubling stakes, recording wins, and declaring game over conditions.
 *
 * @author Patrick Kavanagh
 * @version 1.0
 */


public class MatchTracker {
    private final int matchLength;
    private final Player player1;
    private final Player player2;
    private int doublingCubeValue;
    private Player cubeOwner;
    private final Game game;

    /**
     * Constructs a MatchTracker with specified match length and player details.
     * Initialises the doubling cube value to 1 and sets the cube owner to null at the start.
     *
     * @param matchLength the total number of points required to win the match.
     * @param player1 the first player participating in the match.
     * @param player2 the second player participating in the match.
     * @param game the game instance this match is a part of.
     */
    public MatchTracker(int matchLength, Player player1, Player player2, Game game) {
        this.matchLength = matchLength;
        this.player1 = player1;
        this.player2 = player2;
        this.doublingCubeValue = 1;
        this.cubeOwner = null;
        this.game = game;

    }

    /**
     * Offers to double the stakes of the match. This method manages the offer and response
     * to a doubling request by checking cube ownership and processing player responses.
     *
     * @param offeringPlayer the player making the double offer.
     * @param otherPlayer the player to respond to the offer.
     * @return a string indicating the outcome of the offer ("accepted", "refused", "invalid").
     */
    public String doubleOffer(Player offeringPlayer, Player otherPlayer) {
        // Prevent an offer if the player doesn't have the cube
        if (cubeOwner != null && cubeOwner != offeringPlayer) {
            System.out.println("Only the current cube owner can offer a double.");
            return "invalid";
        }

        System.out.println(offeringPlayer.getName() + " has offered to double the stakes.");
        // Processes the response from the other player to either accept or refuse the double
        while (true) {
            System.out.print(otherPlayer.getName() + ", do you 'accept' or 'refuse'? ");
            String response = game.getCommand();

            // If they accept, double the stakes and transfer the cube ownership
            if (response.equals("accept")) {
                doublingCubeValue *= 2;
                cubeOwner = otherPlayer;
                System.out.println(otherPlayer.getName() + " accepted the double. Stakes are now " + doublingCubeValue + "x.");
                return "accepted";

            } else if (response.equals("refuse")) {
                System.out.println(otherPlayer.getName() + " refused the double. " + offeringPlayer.getName() + " wins with the current stakes.");
                return "refused";
            } else {
                System.out.println("Invalid input. Please respond with 'accept' or 'refuse'.");
            }
        }
    }

    /**
     * Records a win for a player and updates their score. Also checks if the match is over based on current scores
     * and either ends the match or prompts for continuation.
     *
     * @param player the player who won.
     * @param points the number of points won in this game.
     */

    public boolean recordWin(Player player, int points) {
        player.addScore(points);
        cubeOwner = null;
        doublingCubeValue = 1;

        if (isMatchOver()) {
            System.out.println("The match is over! Winner: " + getWinner());
            getMatchScore();
            System.out.print("Would you like to start a new match? (yes/no): ");
            String response = game.getCommand();

            if (response.equals("yes")) {
                startNewMatch();
                return true;
            } else {
                // if match is over, end the game
                return false;
            }
        } else {
            getMatchScore();
            return true;
        }
    }

    /**
     * Starts a new match by reinitialising the game settings to their default states.
     * This includes setting the doubling cube value back to 1, nullifying the cube owner,
     * and calling the game's reset method to clear the board and other game-related states.
     */
    protected void startNewMatch() {
        System.out.println("Starting a new match...");
        game.initialiseMatch();
        cubeOwner = null;
        doublingCubeValue = 1;
        game.resetGameState();
    }

    /**
     * Determines if the match is over by comparing players' scores against the match length.
     *
     * @return true if the match is over, false otherwise.
     */
    public boolean isMatchOver() {
        return player1.getScore() >= matchLength || player2.getScore() >= matchLength;
    }

    /**
     * Gets the winner of the match based on who first reaches or exceeds the match length in points.
     *
     * @return the name of the winning player, or null if no winner yet.
     */

    public String getWinner() {
        if (player1.getScore() >= matchLength) {
            return player1.getName();
        } else if (player2.getScore() >= matchLength) {
            return player2.getName();
        }
        return null;
    }

    /**
     * Gets the current value of the doubling cube. This value represents the multiplier
     * for the game stakes and is doubled whenever a double offer is accepted.
     *
     * @return the current doubling cube value.
     */
    public int getDoublingCubeValue() {
        return doublingCubeValue;
    }

    /**
     * Displays the current scores and the owner of the doubling cube.
     */
    public void getMatchScore() {
        System.out.println(player1.getName() + ": " + player1.getScore() + " points, " +
                player2.getName() + ": " + player2.getScore() + " points.");
        if (cubeOwner != null) {
            System.out.println("Doubling Cube Owner: " + cubeOwner.getName());
        } else {
            System.out.println("Doubling Cube is currently unowned.");
        }
    }

    // ******************************************************************
    // *------------------- Methods used for testing -------------------*
    // ******************************************************************
    /**
     * Gets the cube owner, the player who currently has control of the doubling cube.
     * The cube owner is the only player who can propose doubling the game stakes.
     *
     * @return the player who owns the doubling cube or null if no player currently owns it.
     */
    public Player getCubeOwner() {
        return cubeOwner;
    }

    /**
     * Returns the match length, the total points that a player must reach to win the match.
     *
     * @return the number of points required to win the match.
     */
    public int getMatchLength() {
        return matchLength;
    }

    /**
     * Gets the score of the specified player. This method can be used for quick access
     * to a player's current score from outside the class.
     *
     * @param player the player whose score is to be retrieved.
     * @return the score of the specified player.
     */
    public int getPointsWon(Player player) {
        return player.getScore();
    }

    /**
     * Resets the scores of both players to zero.
     */
    public void resetPoints() {
        player1.resetScore();
        player2.resetScore();
    }

    /**
     * Sets the owner of the doubling cube. This method is particularly useful in game scenarios
     * where control of the cube needs to be transferred or reset, and is also used for testing purposes.
     *
     * @param owner the player to be set as the new owner of the doubling cube.
     */
    public void setCubeOwner(Player owner) {
        this.cubeOwner = owner;
    }

}
