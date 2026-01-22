// ******************************************************************
// Group number: 41
// Names: Patrick Kavanagh, Ruairi Hogan, Eoin Ryan
// GitHub IDs: pkav2, rh-ucd, JoeBahama
// ******************************************************************

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    // Tests initial conditions
    @Test
    void testPlayerInitialization() {
        // Initialises a player
        Player player = new Player("Eoin", 1);

        // Assertations
        assertEquals("Eoin", player.getName(), "getName should return the correct name");
        assertEquals(1, player.getPlayerNum(), "getPlayerNum should return the correct player number");
        assertEquals(0, player.getScore(), "Initial score should be 0");
    }

    //
    @Test
    void testAddScore() {
        Player player = new Player("Eoin", 2);

        // Add points and test
        player.addScore(10);
        assertEquals(10, player.getScore(), "Score should correctly reflect added points");

        player.addScore(5);
        assertEquals(15, player.getScore(), "Score should correctly update after multiple additions");
    }

    @Test
    void testGetName() {
        Player player = new Player("Patrick", 3);

        // Assertation
        assertEquals("Patrick", player.getName(), "getName should return the correct name");
    }

    @Test
    void testGetPlayerNum() {
        Player player = new Player("Ruairi", 4);

        // Test getPlayerNum
        assertEquals(4, player.getPlayerNum(), "getPlayerNum should return the correct player number");
    }

    @Test
    void testGetScore() {
        Player player = new Player("Jack", 5);

        // Test getScore
        assertEquals(0, player.getScore(), "Initial score should be 0");

        // Add points and test again
        player.addScore(20);
        assertEquals(20, player.getScore(), "getScore should return the updated score after adding points");
    }

    @Test
    void testAddNegativeScore() {
        Player player = new Player("Tom", 6);

        // Add a negative score
        player.addScore(-5);
        assertEquals(-5, player.getScore(), "Score should correctly handle negative values");
    }

}
