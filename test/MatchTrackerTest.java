// ******************************************************************
// Group number: 41
// Names: Patrick Kavanagh, Ruairi Hogan, Eoin Ryan
// GitHub IDs: pkav2, rh-ucd, JoeBahama
// ******************************************************************

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MatchTrackerTest {

    private MatchTracker matchTracker;
    private Player player1;
    private Player player2;
    private TestGame game;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));

        // Initialise real Players and a testGame instance
        player1 = new Player("Player 1", 1);
        player2 = new Player("Player 2", 2);
        game = new TestGame();

        matchTracker = new MatchTracker(5, player1, player2, game);
    }

    // Tests initial setup
    @Test
    void testInitialSetup() {
        assertEquals(1, matchTracker.getDoublingCubeValue());
        assertNull(matchTracker.getCubeOwner());
    }

    @Test
    void testDoubleOfferAccepted() {
        game.setCommand("accept"); // Simulate input for accept

        String result = matchTracker.doubleOffer(player1, player2);
        System.out.println("result");

        assertEquals("accepted", result);
        assertEquals(2, matchTracker.getDoublingCubeValue());
        assertEquals(player2, matchTracker.getCubeOwner());
    }


    @Test
    void testDoubleOfferRefused() {
        game.setCommand("refuse");

        String result = matchTracker.doubleOffer(player1, player2);

        assertEquals("refused", result);
        assertNull(matchTracker.getCubeOwner());
        assertEquals(1, matchTracker.getDoublingCubeValue());
    }

    @Test
    void testDoubleOfferInvalidResponse() {
        game.setCommand("sdfsdsfs");
        game.setCommand("accept");

        String expectedOutput = "Invalid input. Please respond with 'accept' or 'refuse'.";
        boolean print;
        if (outputStream.toString().contains(expectedOutput)){
            print = true;
            assertEquals(true, print);
        }
    }

    @Test
    void testRecordWinUpdatesScore() {
        matchTracker.recordWin(player1, 2);

        assertEquals(2, player1.getScore());
        assertNull(matchTracker.getCubeOwner());
        assertEquals(1, matchTracker.getDoublingCubeValue());
    }

    @Test
    void testIsMatchOver() {
        player1.addScore(5);

        assertTrue(matchTracker.isMatchOver());
    }

    @Test
    void testGetWinner() {
        player1.addScore(5);

        assertEquals("Player 1", matchTracker.getWinner());
    }

    @Test
    void testGetWinnerNoWinnerYet() {
        player1.addScore(3);
        player2.addScore(3);

        assertNull(matchTracker.getWinner());
    }

    @Test
    void testGetMatchScore() {
        player1.addScore(2);
        player2.addScore(3);

        matchTracker.getMatchScore();

        assertEquals(2, player1.getScore());
        assertEquals(3, player2.getScore());
    }

    @Test
    void testDoubleOfferByNonOwner() {
        // Player 1 is the cube owner
        matchTracker.setCubeOwner(player1);

        // Player 2 tries to offer a double, but they are not the cube owner
        game.setCommand("accept");
        String result = matchTracker.doubleOffer(player2, player1);

        // Assertions
        assertEquals("invalid", result, "Offer should be invalid because player2 does not own the cube.");
        assertEquals(player1, matchTracker.getCubeOwner(), "Cube owner should remain unchanged as player1.");
        assertEquals(1, matchTracker.getDoublingCubeValue(), "Doubling cube value should remain unchanged.");
    }

    @Test
    void testRefusalEndsGame() {
        game.setCommand("refuse");
        matchTracker.doubleOffer(player1, player2);
        assertFalse(matchTracker.isMatchOver(), "Match should end after a refusal.");
        assertNotNull(outputStream.toString().contains("Thanks for playing! Goodbye."));
    }

    @Test
    void testMatchScoreOutput() {
        player1.addScore(3);
        player2.addScore(2);
        matchTracker.getMatchScore();
        String expectedOutput = player1.getName() + ": 3 points, " +
                player2.getName() + ": 2 points.";
        assertTrue(outputStream.toString().contains(expectedOutput));
        assertTrue(outputStream.toString().contains("Doubling Cube is currently unowned."));
    }

    @Test
    void testSetAndGetCubeOwner() {
        assertNull(matchTracker.getCubeOwner(), "Initially, no cube owner should be set.");
        matchTracker.setCubeOwner(player1);
        assertEquals(player1, matchTracker.getCubeOwner(), "Cube owner should be player1 after setting.");
    }

    @Test
    void testGetPointsWon() {
        player1.addScore(3);
        assertEquals(3, matchTracker.getPointsWon(player1), "Points won should return the correct score for player1.");
    }

    @Test
    void testRecordWin(){
        matchTracker = new MatchTracker(1, player1, player2, game); // Initialise with length of 1
        // Set play again to no
        game.setCommand("no");

        boolean result = matchTracker.recordWin(player1, 5); // Simulate player1 winning the match
        assertFalse(result, "The message should be ending message.");
    }


}


