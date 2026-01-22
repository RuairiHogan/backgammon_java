// ******************************************************************
// Group number: 41
// Names: Patrick Kavanagh, Ruairi Hogan, Eoin Ryan
// GitHub IDs: pkav2, rh-ucd, JoeBahama
// ******************************************************************

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;
    private Player playerWhite; // Player 1
    private Player playerBlack; // Player 2
    private MatchTracker matchTracker;
    private TestGame game;

    @BeforeEach
    public void setUp() {
        game = new TestGame();
        playerWhite = new Player("WhitePlayer", 1);
        playerBlack = new Player("BlackPlayer", 2);
        matchTracker = new MatchTracker(5, playerWhite, playerBlack, game);
        board = new Board();
        matchTracker.resetPoints();
        board.resetBoard();
    }

    @Test
    public void testClearBoard() {
        // Ensure initial setup places checkers
        assertFalse(board.getBoardPoint(0).isEmpty(), "Initial board should have checkers at point 1.");

        board.clearBoard();

        // After clearing, all points should be empty
        for (int i = 0; i < 24; i++) {
            assertTrue(board.getBoardPoint(i).isEmpty(), "Point " + (i+1) + " should be empty after clearing.");
        }

        // Bar counts and bear counts should be reset to 0
        assertEquals(0, board.getBlackBarCount(), "Black bar count should be reset to 0.");
        assertEquals(0, board.getWhiteBarCount(), "White bar count should be reset to 0.");
        assertEquals(0, board.getBearNumbers("black"), "Black bear count should be reset to 0.");
        assertEquals(0, board.getBearNumbers("white"), "White bear count should be reset to 0.");

        // Moves and dice rolled lists should all be empty
        assertTrue(board.getMovesList().isEmpty(), "moves_List should be empty after clearing.");
        assertTrue(board.getMoves_num().isEmpty(), "moves_num should be empty after clearing.");
        assertTrue(board.getDiceRolled().isEmpty(), "dice_rolled should be empty after clearing.");
    }

    @Test
    public void testHitChecker() {
        // Putting one black checker on a point
        board.clearBoard();
        board.placeCheckers(10, "⚫", 1);
        assertEquals(1, board.getBoardPoint(10).size(), "Point 11 should have 1 Black checker.");

        // Putting a white checker on the black one
        board.placeCheckers(10, "⚪", 1);

        // The black checker should now be on the black bar
        assertEquals(0, board.getWhiteBarCount(), "White bar count should still be 0.");
        assertEquals(1, board.getBlackBarCount(), "Black bar should have 1 checker after being hit.");

        // Checking that only the white checker remains on point 11
        ArrayDeque<String> pointDeque = board.getBoardPoint(10);
        assertEquals(1, pointDeque.size(), "Point 11 should have 1 White checker after hit.");
        assertTrue(pointDeque.contains("⚪"), "Point 11 should contain a White checker after hitting black.");
    }


    @Test
    public void testCheckAllHomePlayer1() {
        // Checking that not all the checkers are at home
        assertFalse(board.checkAllHome(playerWhite), "Initially, White checkers are not all in home board.");

        // Putting all the white checkers into the home
        board.clearBoard();
        board.placeCheckers(0, "⚪", 15);

        assertTrue(board.checkAllHome(playerWhite), "All white checkers should be considered home now.");
    }

    @Test
    public void testCheckAllHomePlayer2() {
        assertFalse(board.checkAllHome(playerBlack), "Initially, Black checkers are not all in home board.");

        // Putting all the black checkers in its home
        board.clearBoard();
        board.placeCheckers(23, "⚫", 15);
        assertTrue(board.checkAllHome(playerBlack), "All black checkers should be considered home now.");
    }

    @Test
    public void testMoveCheckerSimpleMove() {

        // Moving one checker from point 6 to point 5
        board.moves_List.add("Play 6 - 5");
        board.moves_num.add(5); // from index 5
        board.moves_num.add(4); // to index 4
        board.dice_rolled.add(1);

        // Performing the move
        Integer dieUsed = board.moveChecker("1", playerWhite);
        board.clearMoves();
        assertEquals(Integer.valueOf(1), dieUsed, "Die used should be 1.");
        assertEquals(4, board.getBoardPoint(5).size(), "Point 6 should have 4 White checkers after move.");
        assertEquals(1, board.getBoardPoint(4).size(), "Point 5 should have 1 White checker after move.");
        assertTrue(board.getMovesList().isEmpty(), "moves_List should be cleared after move.");
        assertTrue(board.getMoves_num().isEmpty(), "moves_num should be cleared after move.");
        assertTrue(board.getDiceRolled().isEmpty(), "dice_rolled should be cleared after move.");
    }


    @Test
    public void testBearOffPlayer1() {
        board.clearBoard();
        // Place all the white checkers in their home: Points 1-6
        board.placeCheckers(0, "⚪", 15);

        // Adding a bear-off move to moves list: Bear off from point 1
        board.moves_List.add("Bear off 1 with die 1");
        board.moves_num.add(0);
        board.moves_num.add(400);   // The 400 code indicates White home bearing off
        board.dice_rolled.add(1);

        // Bearing off the checkers
        board.moveChecker("1", playerWhite);

        // One checker should now be borne off
        assertEquals(1, board.getBearNumbers("white"), "White should have 1 checker borne off.");
        assertEquals(14, board.getBoardPoint(0).size(), "Point 1 should now have 14 White checkers left.");
    }

    @Test
    public void testCheckWinConditionForPlayer1() {
        //  Player 1 bearing off all 15 checkers
        board.clearBoard();
        for (int i = 0; i < 15; i++) {
            board.incrementWhiteBear();
        }

        boolean hasWon = board.checkWin(playerWhite, matchTracker);
        assertTrue(hasWon, "Player 1 should have won the game after all checkers are borne off.");
    }



    @Test
    public void testCalculatePipCountAfterMovingCheckers() {
        // Initially, pip counts are the same
        int initialPipWhite = board.calculatePipCount(playerWhite);
        int initialPipBlack = board.calculatePipCount(playerBlack);

        assertEquals(167, initialPipWhite, "Initial pip count for White should be 167.");
        assertEquals(167, initialPipBlack, "Initial pip count for Black should be 167.");

        // Moving a single White checker closer to home
        board.clearMoves();
        board.moves_List.add("Play 6 - 5");
        board.moves_num.add(5);
        board.moves_num.add(4);
        board.dice_rolled.add(1);
        board.moveChecker("1", playerWhite);

        int newPipWhite = board.calculatePipCount(playerWhite);
        assertTrue(newPipWhite < initialPipWhite, "After moving a White checker towards home, pip count should decrease.");
    }

    @Test
    public void testCheckValidMoveIndex() {
        board.moves_List.add("Some Move");

        // "1" is valid (since moves_List.size() = 1)
        assertFalse(board.checkValidMove("1"), "Move '1' should be valid.");

        // "2" is invalid (no second move)
        assertTrue(board.checkValidMove("2"), "Move '2' should be invalid.");
    }

    @Test
    public void testCheckValidMoveNonNumericInput() {
        board.moves_List.add("Some Move");

        // Non-numeric input is invalid
        assertTrue(board.checkValidMove("abc"), "Non-numeric input should be invalid.");
    }
    @Test
    public void testGetBlackEncircledNumber_ValidNumbers() {
        assertEquals("❶", board.getBlackEncircledNumber(1));
        assertEquals("❷", board.getBlackEncircledNumber(2));
        assertEquals("❸", board.getBlackEncircledNumber(3));
        assertEquals("❹", board.getBlackEncircledNumber(4));
        assertEquals("❺", board.getBlackEncircledNumber(5));
        assertEquals("❻", board.getBlackEncircledNumber(6));
        assertEquals("❼", board.getBlackEncircledNumber(7));
        assertEquals("❽", board.getBlackEncircledNumber(8));
        assertEquals("❾", board.getBlackEncircledNumber(9));
        assertEquals("❿", board.getBlackEncircledNumber(10));
    }

    @Test
    public void testGetBlackEncircledNumber_InvalidNumbers() {
        assertEquals("(11)", board.getBlackEncircledNumber(11));
        assertEquals("(0)", board.getBlackEncircledNumber(0));
    }

    @Test
    public void testGetWhiteEncircledNumber_ValidNumbers() {
        assertEquals("①", board.getWhiteEncircledNumber(1));
        assertEquals("②", board.getWhiteEncircledNumber(2));
        assertEquals("③", board.getWhiteEncircledNumber(3));
        assertEquals("④", board.getWhiteEncircledNumber(4));
        assertEquals("⑤", board.getWhiteEncircledNumber(5));
        assertEquals("⑥", board.getWhiteEncircledNumber(6));
        assertEquals("⑦", board.getWhiteEncircledNumber(7));
        assertEquals("⑧", board.getWhiteEncircledNumber(8));
        assertEquals("⑨", board.getWhiteEncircledNumber(9));
        assertEquals("⑩", board.getWhiteEncircledNumber(10));
    }

    @Test
    public void testGetWhiteEncircledNumber_InvalidNumbers() {
        assertEquals("(11)", board.getWhiteEncircledNumber(11));
        assertEquals("(0)", board.getWhiteEncircledNumber(0));
    }

    @Test
    public void testResetBoard() {
        // Changing the board state to suit
        board.placeCheckers(0, "⚪", 3);
        board.moves_List.add("Test Move");
        board.moves_num.add(0);
        board.moves_num.add(1);
        board.dice_rolled.add(2);
        board.incrementWhiteBear();

        board.resetBoard();

        // Assert that all points are set up correctly
        for (int i = 0; i < 24; i++) {
            ArrayDeque<String> point = board.getBoardPoint(i);
            if (i == 5 || i == 7 || i == 12 || i == 23) { // Starting positions for White
            } else if (i == 0 || i == 11 || i == 16 || i == 18) { // Starting positions for Black
            } else {
                assertTrue(point.isEmpty(), "Point " + (i+1) + " should be empty after reset.");
            }
        }

        // Assert that move lists and dice rolls are cleared
        assertTrue(board.getMovesList().isEmpty(), "moves_List should be empty after reset.");
        assertTrue(board.getMoves_num().isEmpty(), "moves_num should be empty after reset.");
        assertTrue(board.getDiceRolled().isEmpty(), "dice_rolled should be empty after reset.");

        // Assert that bar and bear counts are reset to 0
        assertEquals(0, board.getBlackBarCount(), "Black bar count should be reset to 0.");
        assertEquals(0, board.getWhiteBarCount(), "White bar count should be reset to 0.");
        assertEquals(0, board.getBearNumbers("black"), "Black bear count should be reset to 0.");
        assertEquals(0, board.getBearNumbers("white"), "White bear count should be reset to 0.");
    }

    @Test
    public void testDisplayBoard() {
        Player player = playerWhite;
        int dice1 = 3;
        int dice2 = 5;

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            board.displayBoard(player, dice1, dice2, matchTracker);

            // Capturing the output
            String output = outContent.toString();

            // Output should contain certain expected strings
            assertTrue(output.contains("⚪") || output.contains("⚫"), "Display should contain checker symbols.");
            assertTrue(output.contains("⚀") || output.contains("⚁") || output.contains("⚂") ||
                    output.contains("⚃") || output.contains("⚄") || output.contains("⚅") ||
                    output.contains("Invalid roll"), "Display should contain dice symbols or 'Invalid roll'.");

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testGetList_Player1_NoBar() {
        // Making sure Player 1 has no checkers on the bar
        board.clearBoard();
        // Placing checkers in positions allowing specific moves
        board.placeCheckers(5, "⚪", 5);
        board.placeCheckers(7, "⚪", 3);
        board.placeCheckers(12, "⚪", 5);
        board.placeCheckers(23, "⚪", 2);
        board.placeCheckers(0, "⚫", 2);
        board.placeCheckers(11, "⚫", 5);
        board.placeCheckers(16, "⚫", 3);
        board.placeCheckers(18, "⚫", 5);

        // Calling getList with these dice rolls
        int dice1 = 3;
        int dice2 = 2;
        board.getList(dice1, dice2, playerWhite);

        // Expected moves:
        List<String> expectedMoves = List.of(
                "Play 6 - 3",   // 5 - 3 = 2 (Point 3)
                "Play 6 - 4",   // 5 - 2 = 3 (Point 4)
                "Play 8 - 5",   // 7 - 3 = 4 (Point 5)
                "Play 8 - 6",   // 7 - 2 = 5 (Point 6)
                "Play 13 - 10", // 12 - 3 = 9 (Point 10)
                "Play 13 - 11", // 12 - 2 = 10 (Point 11)
                "Play 24 - 21", // 23 - 3 = 20 (Point 21)
                "Play 24 - 22"  // 23 - 2 = 21 (Point 22)
        );

        for (String move : expectedMoves) {
            assertTrue(board.getMovesList().contains(move), "Move '" + move + "' should be in moves_List.");
        }

        // Show the size of the list
        assertEquals(expectedMoves.size(), board.getMovesList().size(), "moves_List should contain the correct number of moves.");
    }


    @Test
    public void testCanMove_Player1_ValidMove() {
        // Player 1 has a checker at point 6
        board.clearBoard();
        board.placeCheckers(5, "⚪", 1);

        // Call canMove directly
        board.canMove(5, 1, 2, playerWhite);

        // Show that moves are added correctly
        List<String> expectedMoves = List.of(
                "Play 6 - 5", // 5 - 1 = 4 (Point 5)
                "Play 6 - 4"  // 5 - 2 = 3 (Point 4)
        );
        assertEquals(expectedMoves, board.getMovesList(), "canMove should add correct moves for Player 1.");
        assertEquals(List.of(5, 4, 5, 3), board.getMoves_num(), "moves_num should contain correct move indices.");
        assertEquals(List.of(1, 2), board.getDiceRolled(), "dice_rolled should contain correct dice usage.");
    }

    @Test
    public void testCanMove_Player2_NoValidMove() {
        // Player 2 has a checker at point 1
        board.clearBoard();
        board.placeCheckers(0, "⚫", 1);

        // Blocking the destination points
        board.placeCheckers(4, "⚪", 2);
        board.placeCheckers(5, "⚪", 2);

        // Calling canMove directly
        board.canMove(0, 4, 3, playerBlack);

        // Proving that no moves are added since destinations are blocked
        board.clearMoves();
        assertTrue(board.getMovesList().isEmpty(), "canMove should not add any moves when destinations are blocked.");
        assertTrue(board.getMoves_num().isEmpty(), "moves_num should be empty when no moves are possible.");
        assertTrue(board.getDiceRolled().isEmpty(), "dice_rolled should be empty when no moves are possible.");
    }

    @Test
    public void testRemoveChecker_Player1_Success() {
        // Player 1 has a checker at point 6
        board.clearBoard();
        board.placeCheckers(5, "⚪", 1);

        // Removing that checker
        String removedChecker = board.removeChecker(5, playerWhite);
        assertEquals("⚪", removedChecker, "Should remove a White checker.");
        assertTrue(board.getBoardPoint(5).isEmpty(), "Point 6 should be empty after removal.");
    }

    @Test
    public void testRemoveChecker_Player2_Success() {
        // Player 2 has a checker at point 7
        board.clearBoard();
        board.placeCheckers(6, "⚫", 1);

        // Removing that checker
        String removedChecker = board.removeChecker(6, playerBlack);
        assertEquals("⚫", removedChecker, "Should remove a Black checker.");
        assertTrue(board.getBoardPoint(6).isEmpty(), "Point 7 should be empty after removal.");
    }

    @Test
    public void testRemoveChecker_Player2_NoChecker() {
        // Player 2 has no checker at point 6
        board.clearBoard();

        // Try to remove a checker
        String removedChecker = board.removeChecker(5, playerBlack);
        assertEquals("1", removedChecker, "Should return '1' when no checker is removed.");
        assertTrue(board.getBoardPoint(5).isEmpty(), "Point 6 should remain empty.");
    }

    @Test
    public void testHasCheckersInOpponentHome_Player1_WithCheckers() {
        // Opponent is Player 2 (⚫)
        board.clearBoard();
        board.placeCheckers(2, "⚫", 1); // Point 3 (Index 2) - in Player 2's home (1-6)

        assertTrue(board.hasCheckersInOpponentHome("⚫"), "Should detect checkers in opponent's home for Player 1.");
    }

    @Test
    public void testHasCheckersInOpponentHome_Player1_NoCheckers() {
        // Opponent is Player 2 (⚫)
        board.clearBoard();

        assertFalse(board.hasCheckersInOpponentHome("⚫"), "Should not detect any checkers in opponent's home for Player 1.");
    }

    @Test
    public void testHasCheckersInOpponentHome_Player2_WithCheckers() {
        // Opponent is Player 1 (⚪)
        board.clearBoard();
        board.placeCheckers(19, "⚪", 1); // Point 20 (Index 19) - in Player 1's home (19-24)

        assertTrue(board.hasCheckersInOpponentHome("⚪"), "Should detect checkers in opponent's home for Player 2.");
    }

    @Test
    public void testHasCheckersInOpponentHome_Player2_NoCheckers() {
        // Opponent is Player 1 (⚪)
        board.clearBoard();

        assertFalse(board.hasCheckersInOpponentHome("⚪"), "Should not detect any checkers in opponent's home for Player 2.");
    }

    @Test
    public void testCheckWin_NormalWin_Player1() {
        // Player 1 has borne off all 15 checkers, Player 2 has borne off 1
        board.clearBoard();

        // Player 1 bears off all checkers
        for (int i = 0; i < 15; i++) {
            board.incrementWhiteBear();
        }

        // Player 2 has borne off 1 checker
        board.setBlackBear(1);

        // Get the initial score
        int initialScore = matchTracker.getPointsWon(playerWhite);

        // Call checkWin
        boolean hasWon = board.checkWin(playerWhite, matchTracker);

        // Assertions
        assertTrue(hasWon, "Player 1 should have a normal win.");
        assertEquals(initialScore + 1, matchTracker.getPointsWon(playerWhite),
                "Player 1 should receive 1 point for a normal win.");
    }



    @Test
    public void testCheckWin_Gammon_Player2() {
        // Resetting the board to a clean state
        board.clearBoard();

        // Set bear-off counts
        board.setBlackBear(15);
        board.setWhiteBear(0);

        // Ensure Player 1 has no checkers on the board or on the bar
        removeAllCheckers(playerWhite);

        // Get the initial score
        int initialScore = matchTracker.getPointsWon(playerBlack);

        // Call checkWin
        boolean hasWon = board.checkWin(playerBlack, matchTracker);

        // Assertions
        assertTrue(hasWon, "Player 2 should have a gammon win.");
        assertEquals(initialScore + 2, matchTracker.getPointsWon(playerBlack),
                "Player 2 should receive 2 points for a gammon win.");
    }

    @Test
    public void testCheckWin_Gammon_Player1() {
        // Resetting the board to a clean state
        board.clearBoard();

        // Set bear-off counts
        board.setBlackBear(0);
        board.setWhiteBear(15);

        // Ensure Player 2 has no checkers on the board or on the bar
        removeAllCheckers(playerBlack);

        // Get the initial score
        int initialScore = matchTracker.getPointsWon(playerWhite);

        // Call checkWin
        boolean hasWon = board.checkWin(playerWhite, matchTracker);

        // Assertions
        assertTrue(hasWon, "Player 2 should have a gammon win.");
        assertEquals(initialScore + 2, matchTracker.getPointsWon(playerWhite),
                "Player 2 should receive 2 points for a gammon win.");
    }

    @Test
    public void testCheckWin_Backgammon_Player1() {
        // Player 1 has borne off all 15 checkers, Player 2 has borne off 0 and has checkers on the bar
        board.setWhiteBear(15);
        board.setBlackBear(0);
        board.setBlackBarCount(1);

        // Alternatively, Player 2 could have checkers in Player 1's home board
        // board.placeCheckers(2, "⚫", 1); // Point 3 (Index 2)

        // Get the initial score
        int initialScore = matchTracker.getPointsWon(playerWhite);

        // Call checkWin
        boolean hasWon = board.checkWin(playerWhite, matchTracker);

        // Assertions
        assertTrue(hasWon, "Player 1 should have a backgammon win.");
        assertEquals(initialScore + 3, matchTracker.getPointsWon(playerWhite),
                "Player 1 should receive 3 points for a backgammon win.");
    }

    private void removeAllCheckers(Player player) {
        String checkerSymbol = (player.getPlayerNum() == 1) ? "⚪" : "⚫";
        for (int i = 0; i < 24; i++) {
            ArrayDeque<String> point = board.getBoardPoint(i);
            point.removeIf(checker -> checker.equals(checkerSymbol));
        }
    }
}
