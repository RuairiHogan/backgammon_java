// ******************************************************************
// Group number: 41
// Names: Patrick Kavanagh, Ruairi Hogan, Eoin Ryan
// GitHub IDs: pkav2, rh-ucd, JoeBahama
// ******************************************************************

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Board board;
    private Player player1;
    private Player player2;
    private MatchTracker matchTracker;
    private TestGame game;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        // Initialize players
        game = new TestGame();
        player1 = new Player("Alice", 1); // White
        player2 = new Player("Bob", 2);   // Black

        game.setPlayer1(player1);
        game.setPlayer2(player2);
        matchTracker = new MatchTracker(5, player1, player2, game);
        game.setMatchTracker(matchTracker);
        board = new Board();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void simpleTest() {
        assertTrue(true, "This is a simple test to verify the framework is working.");
    }

    @Test
    public void testDisplayBoardWithPips_CurrentPlayerOnly() {
        // Arrange
        int dice1 = 3;
        int dice2 = 5;
        boolean showBothPips = false;
        game.displayBoardWithPips(player1, dice1, dice2, showBothPips);

        // Assert
        String output = outContent.toString();

        // Check for checker symbols
        assertTrue(output.contains("⚪") || output.contains("⚫"), "Output should contain checker symbols.");

        // Check for pip counts
        assertTrue(output.contains(player1.getName() + "'s Pip Count:"),
                "Output should contain the current player's pip count.");

        // Ensure only current player's pip count is shown
        assertFalse(output.contains(player2.getName() + "'s Pip Count:"),
                "Output should not contain the other player's pip count when showBothPips is false.");
    }

    @Test
    public void testDisplayBoardWithPips_InvalidDiceValues() {
        // Arrange
        int dice1 = -1; // Invalid dice value
        int dice2 = 7;  // Invalid dice value
        boolean showBothPips = true;

        // Act
        game.displayBoardWithPips(player1, dice1, dice2, showBothPips);

        // Assert
        String output = outContent.toString();

        // Check for invalid dice roll message
        assertTrue(output.contains("Invalid roll"), "Output should indicate invalid dice rolls.");
    }

    @Test
    public void testDisplayBoardWithPips_BothPlayers() {
        // Arrange
        int dice1 = 4;
        int dice2 = 2;
        boolean showBothPips = true;

        // Act
        game.displayBoardWithPips(player2, dice1, dice2, showBothPips);

        // Assert
        String output = outContent.toString();

        // Check for checker symbols
        assertTrue(output.contains("⚪") || output.contains("⚫"), "Output should contain checker symbols.");

        // Check for both players' pip counts
        assertTrue(output.contains(player2.getName() + "'s Pip Count:"),
                "Output should contain the current player's pip count.");
        assertTrue(output.contains(player1.getName() + "'s Pip Count:"),
                "Output should contain the other player's pip count when showBothPips is true.");
    }

    @Test
    void testInitialization() {
        assertNotNull(game.getPlayer1(), "Player 1 should be initialized.");
        assertEquals("Alice", game.getPlayer1().getName(), "Player 1's name should be Alice.");
        assertNotNull(game.getPlayer2(), "Player 2 should be initialized.");
        assertEquals("Bob", game.getPlayer2().getName(), "Player 2's name should be Bob.");
        assertEquals(5, matchTracker.getMatchLength(), "Match length should be set to 5.");
    }

    @Test
    void testDetermineFirstPlayer() {
        // Setting the forced dice to be 5 and 3
        game.handleDiceCommand("dice 5 3");

        // Simulate the first move input
        game.setCommand("1");

        // Call the method to test
        game.determineFirstPlayer();

        // Assertions
        assertNotNull(game.getCurrentPlayer(), "A current player should be determined.");
        assertEquals(game.getPlayer2(), game.getCurrentPlayer(), "Player 2 should be the first player."); // Because it switches at the end of the method
        assertEquals(5, game.getInitialDiceRoll1(), "Player 1's dice roll should be 5.");
        assertEquals(3, game.getInitialDiceRoll2(), "Player 2's dice roll should be 3.");
    }




    @Test
    void testValidCommands() {
        // Check that the commands list contains expected values
        assertTrue(game.getCommands().contains("roll"), "'roll' should be a valid command.");
        assertTrue(game.getCommands().contains("quit"), "'quit' should be a valid command.");
    }

    @Test
    void testPlayerSwitching() {

        Player initialPlayer = game.getCurrentPlayer();
        game    .switchPlayer(); // Simulate the end of a turn to switch players
        Player nextPlayer = game.getCurrentPlayer();

        assertNotEquals(initialPlayer, nextPlayer, "The current player should switch after a turn.");
    }

    @Test
    void testGameWinningCondition() {
        Player player1 = game.getPlayer1();
        MatchTracker tracker = game.getMatchTracker();

        // Simulate a win by reaching the required match length
        player1.addScore(5); // Add points to reach the match length
        assertTrue(tracker.isMatchOver(), "Game should be over when a player reaches the match length.");
        assertEquals("Alice", tracker.getWinner(), "Alice should be declared the winner.");
    }

    @Test
    void testPipCountCalculation() {
        Player player1 = game.getPlayer1();
        int pipCount = game.getBoard().calculatePipCount(player1);

        assertTrue(pipCount > 0, "Initial pip count for a player should be greater than 0.");
    }
    @Test
    void testDiceOverrideCommand() {
        game.handleDiceCommand("dice 3 5"); // Test valid override command
        assertEquals(3, game.getForcedDice1(), "ForcedDice1 should be set to 3.");
        assertEquals(5, game.getForcedDice2(), "ForcedDice2 should be set to 5.");

        game.handleDiceCommand("dice 7 0"); // Test dice values out of range
        assertEquals(-1, game.getForcedDice1(), "ForcedDice1 should reset to -1 for invalid input.");
        assertEquals(-1, game.getForcedDice2(), "ForcedDice2 should reset to -1 for invalid input.");

        game.handleDiceCommand("dice a b"); // Test invalid command format
        assertEquals(-1, game.getForcedDice1(), "ForcedDice1 should reset to -1 for invalid input.");
        assertEquals(-1, game.getForcedDice2(), "ForcedDice2 should reset to -1 for invalid input.");
    }

    @Test
    void testShowHint() {
        game.showHint();
        // Expected list of commands
        List<String> expectedCommands = Arrays.asList("roll", "quit", "hint", "pip", "double", "dice", "test");
        // Compare the actual and expected lists of commands
        assertEquals(expectedCommands, game.getCommands(), "These are the correct commands.");
        // Verify that the "bear" command is not in the list
        assertFalse(game.getCommands().contains("bear"), "Bear should not be a correct command.");
    }


    @Test
    void testInitialiseMatch() {
        // Simulated input for player names and match length
        String simulatedInput = "Alice\nBob\n5\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);

        game.setCommand("1");
        // Call initialiseMatch
        game.initialiseMatch();

        // Assertions
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();
        MatchTracker matchTracker = game.getMatchTracker();

        assertNotNull(player1, "Player 1 should be initialized.");
        assertEquals("Alice", player1.getName(), "Player 1's name should be Alice.");

        assertNotNull(player2, "Player 2 should be initialized.");
        assertEquals("Bob", player2.getName(), "Player 2's name should be Bob.");

        assertNotNull(matchTracker, "MatchTracker should be initialized.");
        assertEquals(5, matchTracker.getMatchLength(), "Match length should be set to 5 points.");
    }



    @Test
    void testStartGame() throws InterruptedException {
        // Starting a new game
        //TestGame game = new TestGame();
        // Simulate game commands
        // Simulated input for player names and match length
        String simulatedInput1 = "Alice\nBob\n-5\nword\n5\n";
        InputStream inputStream1 = new ByteArrayInputStream(simulatedInput1.getBytes());
        System.setIn(inputStream1);

        game.TestFileModeOn();
        // Run the method
        game.startGame();

        // Assertions
        assertEquals("Alice", game.getPlayer1().getName(), "Player 1 should remain Alice.");
        assertEquals("Bob", game.getPlayer2().getName(), "Player 2 should remain Bob.");
        assertFalse(game.getGameRunning(), "Game should not be running after 'quit' command.");

    }

    @Test
    void testInitializeFileMode_Success(){
                // Call initializeFileMode with the temporary file
        game.initializeFileMode("GameTest.txt");

        // Assertions
        assertTrue(game.isFileMode(), "File mode should be enabled for a valid file.");
        assertNotNull(game.getFileReader(), "BufferedReader should be initialized for a valid file.");
    }

    @Test
    void testInitializeFileMode_FileNotFound() {
        // Call initializeFileMode with a non-existent file
        game.initializeFileMode("non_existent_file.txt");

        // Assertions
        assertFalse(game.isFileMode(), "File mode should be disabled for a non-existent file.");
        assertNull(game.getFileReader(), "BufferedReader should not be initialized for a non-existent file.");
    }



    // ******************************************************************
    // *------------------- Methods used for testing -------------------*
    // ******************************************************************

    @Test
    void testGetPlayer1() {
        assertEquals(player1, game.getPlayer1(), "getPlayer1 should return the correct Player1 instance.");
    }

    @Test
    void testGetPlayer2() {
        assertEquals(player2, game.getPlayer2(), "getPlayer2 should return the correct Player2 instance.");
    }

    @Test
    void testGetCurrentPlayer() {
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.switchPlayer(); // Ensure a player is set as the current player
        assertNotNull(game.getCurrentPlayer(), "getCurrentPlayer should not return null after switching players.");
    }

    @Test
    void testGetBoard() {
        assertNotNull(game.getBoard(), "getBoard should return the initialized board.");
    }

    @Test
    void testGetMatchTracker() {
        assertEquals(matchTracker, game.getMatchTracker(), "getMatchTracker should return the correct MatchTracker instance.");
    }

    @Test
    void testGetForcedDice1() {
        game.handleDiceCommand("dice 3 5");
        assertEquals(3, game.getForcedDice1(), "getForcedDice1 should return the forced dice value 1.");
    }

    @Test
    void testGetForcedDice2() {
        game.handleDiceCommand("dice 3 5");
        assertEquals(5, game.getForcedDice2(), "getForcedDice2 should return the forced dice value 2.");
    }

    @Test
    void testGetDiceRoll1() {
        game.handleDiceCommand("dice 4 6");
        game.switchPlayer(); // Simulate rolling the dice
        assertEquals(0, game.getDiceRoll1(), "getDiceRoll1 should initially return 0 as no roll is made yet.");
    }

    @Test
    void testGetDiceRoll2() {
        game.handleDiceCommand("dice 4 6");
        game.switchPlayer(); // Simulate rolling the dice
        assertEquals(0, game.getDiceRoll2(), "getDiceRoll2 should initially return 0 as no roll is made yet.");
    }

    @Test
    void testGetCommands() {
        assertTrue(game.getCommands().contains("roll"), "getCommands should contain the 'roll' command.");
        assertTrue(game.getCommands().contains("quit"), "getCommands should contain the 'quit' command.");
    }

    @Test
    void testGetInitialDiceRoll1() {
        game.handleDiceCommand("dice 1 2");
        assertEquals(0, game.getInitialDiceRoll1(), "getInitialDiceRoll1 should initially return 0.");
    }

    @Test
    void testGetInitialDiceRoll2() {
        game.handleDiceCommand("dice 1 2");
        assertEquals(0, game.getInitialDiceRoll2(), "getInitialDiceRoll2 should initially return 0.");
    }

    @Test
    void testSetPlayer1() {
        Player newPlayer1 = new Player("Charlie", 1);
        game.setPlayer1(newPlayer1);
        assertEquals(newPlayer1, game.getPlayer1(), "setPlayer1 should update the Player1 instance.");
    }

    @Test
    void testSetPlayer2() {
        Player newPlayer2 = new Player("Dana", 2);
        game.setPlayer2(newPlayer2);
        assertEquals(newPlayer2, game.getPlayer2(), "setPlayer2 should update the Player2 instance.");
    }

    @Test
    void testSetMatchTracker() {
        MatchTracker newTracker = new MatchTracker(7, player1, player2, game);
        game.setMatchTracker(newTracker);
        assertEquals(newTracker, game.getMatchTracker(), "setMatchTracker should update the MatchTracker instance.");
        assertEquals(7, newTracker.getMatchLength(), "Match length should be updated to the new value.");
    }

}