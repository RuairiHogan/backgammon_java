// ******************************************************************
// Group number: 41
// Names: Patrick Kavanagh, Ruairi Hogan, Eoin Ryan
// GitHub IDs: pkav2, rh-ucd, JoeBahama
// ******************************************************************

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Game {

    // Class variables to manage game components
    private final Scanner  scanner = new Scanner(System.in);
    private Player player1;
    private Player player2;
    private int diceRoll1;
    private int diceRoll2;
    private int initialDiceRoll1;
    private int initialDiceRoll2;
    private int forcedDice1 = -1;
    private int forcedDice2 = -1;
    private final Board board;
    private static final Logger logger = Logger.getLogger(Game.class.getName());
    private Player currentPlayer;
    private final List<String> commands = Arrays.asList("roll", "quit", "hint", "pip", "double", "dice", "test");
    private MatchTracker matchTracker;
    private int matchLength;
    protected BufferedReader fileReader = null;
    private boolean isFileMode = false;
    public boolean gameRunning;

    public Game(){
        board = new Board();
        gameRunning = false;// Initialises the game board
    }

    // Initialises a match by setting up players and a length
    public void initialiseMatch() {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter name for Player 1: ");
        String name1 = in.nextLine().trim();
        player1 = new Player(name1, 1);

        System.out.print("Enter name for Player 2: ");
        String name2 = in.nextLine().trim();
        player2 = new Player(name2, 2);

        matchLength = 0;
        while (matchLength <= 0) {
            try {
                System.out.print("Enter the number of points to reach to win the game: ");
                matchLength = Integer.parseInt(in.nextLine().trim());
                if (matchLength <= 0) {
                    System.out.println("Points must be a positive number. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        // Initialise a match tracker
        matchTracker = new MatchTracker(matchLength, player1, player2, this);


        // Welcome message
        System.out.println("Welcome, " + player1.getName() + " and " + player2.getName() + "!");
        System.out.println("The match will be up to " + matchLength + " points.");
        determineFirstPlayer();
    }


    // Main game loop that handles players turns and game commands
    public void startGame() {
        initialiseMatch();
        logger.info("Using initial dice rolls: Roll1: " + initialDiceRoll1 + ", Roll2: " + initialDiceRoll2);
        gameRunning = true;

        while (gameRunning) {
            System.out.println(currentPlayer.getName() + "'s turn.");
            System.out.print("Enter 'roll' to roll the dice, 'hint' for available commands, 'double' to double the stakes, dice <> <> to enter values or 'quit' to end the game: ");
            String command = getCommand();

            // Handles the test mode for command input from files
            if (command.startsWith("test ")) {
                String[] parts = command.split(" ");
                if (parts.length == 2) {
                    initializeFileMode(parts[1]);
                    continue;
                } else {
                    System.out.println("Invalid command format. Use 'test <filename>'.");
                }
            }
            boolean validMove = false;

            // Handling direct dice value inputs for testing
            if (command.startsWith("dice")) {
                handleDiceCommand(command);
                continue;
            }

            // Process normal game commands
            switch (command) {
                case "roll":
                    if (forcedDice1 != -1 && forcedDice2 != -1) {
                        diceRoll1 = forcedDice1;
                        diceRoll2 = forcedDice2;
                        forcedDice1 = -1;
                        forcedDice2 = -1;
                    } else {
                        diceRoll1 = Dice.rollDice();
                        diceRoll2 = Dice.rollDice();
                    }

                    displayBoardWithPips(currentPlayer, diceRoll1, diceRoll2, false);
                    Logger.getGlobal().info("Creating a point: Roll1: " + diceRoll1 + ", Roll2: " + diceRoll2);

                    if (diceRoll1 != diceRoll2) {
                        board.getList(diceRoll1, diceRoll2, currentPlayer); // Gets and prints the list of possible moves
                        int rolledDice = 0;

                        if (!board.moves_List.isEmpty()) {
                            // First move
                            System.out.print(currentPlayer.getName() + ", Enter the first move you would like to make: ");
                            command = getCommand();
                            while (board.checkValidMove(command)) { // Check if the move is valid
                                command = getCommand();
                            }
                            rolledDice = board.moveChecker(command, currentPlayer);
                            displayBoardWithPips(currentPlayer, diceRoll1, diceRoll2, false);
                        } else {
                            System.out.println("No valid moves available for the first move.");
                        }
                        board.clearMoves();

                        // Alternative dice rolls for getting the list of moves, the second time
                        int diceRoll1_Alt = 0;
                        int diceRoll2_Alt = 0;

                        switch (rolledDice) {
                            case 1:
                                diceRoll1_Alt = 100;
                                diceRoll2_Alt = diceRoll2;
                                break;
                            case 2:
                                diceRoll1_Alt = diceRoll1;
                                diceRoll2_Alt = 100;
                                break;
                        }
                        board.getList(diceRoll1_Alt, diceRoll2_Alt, currentPlayer); // Gets and prints the list of possible moves

                        if (!board.moves_List.isEmpty()) {
                            System.out.print(currentPlayer.getName() + ", Enter the second move you would like to make: ");
                            command = getCommand();
                            while (board.checkValidMove(command)) { // Checks if the move is valid
                                command = getCommand();
                            }
                            board.moveChecker(command, currentPlayer);
                            displayBoardWithPips(currentPlayer, diceRoll1, diceRoll2, false);
                        } else {
                            System.out.println("No valid moves available for the second move.");
                        }

                        board.clearMoves();
                        validMove = true;
                    }
                    else {
                        // Using diceRoll2 for display
                        int dicePrint2 = diceRoll2;
                        diceRoll2 = 100;

                        // If dice are the same, user makes 4 moves
                        for (int i = 0; i <= 3; i++) {
                            board.getList(diceRoll1, diceRoll2, currentPlayer); // Gets and prints the list of possible moves

                            if (!board.moves_List.isEmpty()) {
                                System.out.print(currentPlayer.getName() + ", Enter move number " + (i+1) + ", you would like to make: ");
                                command = getCommand();
                                while (board.checkValidMove(command)) {
                                    command = getCommand();
                                }
                                board.moveChecker(command, currentPlayer);
                                displayBoardWithPips(currentPlayer, diceRoll1, dicePrint2, false);
                            } else {
                                System.out.println("No valid moves available for the first move.");
                            }
                            board.clearMoves();
                        }
                        validMove = true;
                    }
                    break;

                case "quit":
                    gameRunning = false;
                    System.out.println("Game has ended.");
                    break;

                case "hint":
                    showHint();
                    break;

                case "pip":
                    displayBoardWithPips(currentPlayer, diceRoll1, diceRoll2, true);
                    break;

                case "double":
                    String doubleResult = matchTracker.doubleOffer(currentPlayer, (currentPlayer == player1) ? player2 : player1);

                    switch (doubleResult) {
                        case "accepted":
                            System.out.println("Game continues at " + matchTracker.getDoublingCubeValue() + "x stakes.");
                            break;

                        case "refused":
                            System.out.println(currentPlayer.getName() + " wins this game because the double was refused!");
                            int pointsWon = matchTracker.getDoublingCubeValue();
                            gameRunning = matchTracker.recordWin(currentPlayer, pointsWon);

                            if (gameRunning) {
                                resetGameState(); // Start a new game if the match isn't over
                            }
                            else {
                                System.out.println("Thanks for playing! Goodbye.");
                            }
                            break;

                        case "invalid":
                            System.out.println("Double offer is invalid. You may not double at this time.");
                            break;

                        default:
                            System.out.println("An unexpected error occurred with the double offer.");
                            break;
                    }
                    break;

                default:
                    System.out.println("Invalid command. Please enter 'roll' or 'quit'.");
                    break;
            }

            // Only switch players if a valid move was made
            if (validMove) {
                // Check if the player has won
                if (board.checkWin(currentPlayer, matchTracker)) {
                    gameRunning = false;
                    System.out.println("Congratulations! " + currentPlayer.getName() + " has won the game!");
                } else {
                    // Switch to the next player if the game hasn't ended
                    switchPlayer();
                }
            }
        }

        scanner.close();
    }

    // Determines the first player based on rolling the dice
    protected void determineFirstPlayer() {
        // Loop until the dice rolls are not a tie
        while (true) {
            // Checking if the dice should be forced to a number
            if (forcedDice1 != -1 && forcedDice2 != -1) {
                initialDiceRoll1 = forcedDice1;
                initialDiceRoll2 = forcedDice2;
                forcedDice1 = -1;
                forcedDice2 = -1;
            } else {
                initialDiceRoll1 = Dice.rollDice();
                initialDiceRoll2 = Dice.rollDice();
            }

            System.out.println(player1.getName() + " rolls " + initialDiceRoll1);
            System.out.println(player2.getName() + " rolls " + initialDiceRoll2);

            // If the 1st die is greater than the second die
            if (initialDiceRoll1 > initialDiceRoll2) {
                currentPlayer = player1;
                System.out.println(player1.getName() + " goes first");
                break;
            } else if (initialDiceRoll2 > initialDiceRoll1) {
                currentPlayer = player2;
                System.out.println(player2.getName() + " goes first");
                break;
            } else {
                System.out.println("It's a tie, rolling again...");
            }
        }

        // Perform initial moves using the dice rolls
        Logger.getGlobal().info("Using initial dice rolls: Roll1: " + initialDiceRoll1 + ", Roll2: " + initialDiceRoll2);
        displayBoardWithPips(currentPlayer, initialDiceRoll1, initialDiceRoll2, false);
        board.getList(initialDiceRoll1, initialDiceRoll2, currentPlayer);

        if (board.moves_List.isEmpty()) {
            System.out.println("No valid moves available for " + currentPlayer.getName());
        } else {
            // First move
            System.out.print(currentPlayer.getName() + ", enter the first move you would like to make: ");
            String command = getCommand();
            while (board.checkValidMove(command)) {
                command = getCommand();
            }
            int rolledDice = board.moveChecker(command, currentPlayer);
            displayBoardWithPips(currentPlayer, initialDiceRoll1, initialDiceRoll2, false);
            board.clearMoves();

            // Adjust dice rolls for the second move
            int diceRoll1_Alt = (rolledDice == 1) ? 100 : initialDiceRoll1;
            int diceRoll2_Alt = (rolledDice == 2) ? 100 : initialDiceRoll2;

            board.getList(diceRoll1_Alt, diceRoll2_Alt, currentPlayer);

            if (!board.moves_List.isEmpty()) {
                // Second move if available
                System.out.print(currentPlayer.getName() + ", enter the second move you would like to make: ");
                command = getCommand();
                while (board.checkValidMove(command)) {
                    command = getCommand();
                }
                board.moveChecker(command, currentPlayer);
                displayBoardWithPips(currentPlayer, initialDiceRoll1, initialDiceRoll2, false);
            } else {
                System.out.println("No valid moves available for the second move.");
            }
        }
        board.clearMoves();
        switchPlayer();
    }

    // Displays a list of available commands
    public void showHint() {
        System.out.println("Available commands:");
        for (String command : commands) {
                System.out.println("- " + command);

        }
    }

    // Displays the board along with additional info like the pip counts
    public void displayBoardWithPips(Player player, int dice1, int dice2, boolean showBothPips) {

        System.out.println("\t\t\t\t\t\t\tBlack checkers at home: " + board.getBearNumbers("black") + " / 15");
        board.displayBoard(player, dice1, dice2, matchTracker); // Display board
        System.out.println("\t\t\t\t\t\t\tWhite checkers at home: " + board.getBearNumbers("white") + " / 15");

        if (showBothPips) {
            // Calculate and display pip counts for both players
            int player1PipCount = board.calculatePipCount(player1);
            int player2PipCount = board.calculatePipCount(player2);
            System.out.println(player1.getName() + "'s Pip Count: " + player1PipCount);
            System.out.println(player2.getName() + "'s Pip Count: " + player2PipCount);
        } else {
            // Only calculate and display pip count for the current player
            int playerPipCount = board.calculatePipCount(player);
            System.out.println(player.getName() + "'s Pip Count: " + playerPipCount);
        }
        System.out.println("Match Length: " + matchLength);
        matchTracker.getMatchScore();
        System.out.println();
    }

    // Handles the rolling of a dice, including forced values for testing
    public void handleDiceCommand(String command) {
        if (command.startsWith("dice")) {
            String[] parts = command.split(" ");
            if (parts.length == 3) {
                try {
                    forcedDice1 = Integer.parseInt(parts[1]);
                    forcedDice2 = Integer.parseInt(parts[2]);

                    if (forcedDice1 < 1 || forcedDice1 > 6 || forcedDice2 < 1 || forcedDice2 > 6) {
                        System.out.println("Dice values must be between 1 and 6. Override canceled.");
                        forcedDice1 = -1;
                        forcedDice2 = -1;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid dice values. Please use the format 'dice <int> <int>' with numbers between 1 and 6.");
                    forcedDice1 = -1;
                    forcedDice2 = -1;
                }
            }
        }
    }

    // Resets the game state to start a new match or game session
    public void resetGameState() {
        board.clearMoves();
        board.resetBoard();
        initialDiceRoll1 = Dice.rollDice();
        initialDiceRoll2 = Dice.rollDice();
        determineFirstPlayer(); // Determine which player goes first
        System.out.println("Starting a new game in the match.");
    }

    // Switches the current player to the other player
    protected void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
        System.out.println("It's now " + currentPlayer.getName() + "'s turn.");
    }

    // Initialises reading commands from the file
    protected void initializeFileMode(String filename) {
        try {
            fileReader = new BufferedReader(new FileReader(filename));
            isFileMode = true;
            System.out.println("File mode initialized with file: " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
            isFileMode = false;
        }
    }

    // Gets a command from the user or the input file
    public String getCommand() {

        if (isFileMode) {
            try {
                String line = fileReader.readLine();
                if (line != null) {
                    System.out.println("Command from file: " + line);
                    return line.trim().toLowerCase();
                } else {
                    // End of the file is reached so switch to standard input
                    System.out.println("End of file reached. Switching to standard input.");
                    fileReader.close();
                    isFileMode = false;
                }
            } catch (IOException e) {
                System.out.println("Error reading the file. Switching to standard input.");
                isFileMode = false;
            }
        }

        // Reading from the console as default
        return scanner.nextLine().trim().toLowerCase();
    }
    // ******************************************************************
    // *------------------- Methods used for testing -------------------*
    // ******************************************************************

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public Board getBoard() {
        return this.board;
    }

    public MatchTracker getMatchTracker() {
        return this.matchTracker;
    }

    public int getForcedDice1() {
        return this.forcedDice1;
    }

    public int getForcedDice2() {
        return this.forcedDice2;
    }

    public int getDiceRoll1() {
        return this.diceRoll1;
    }

    public int getDiceRoll2() {
        return this.diceRoll2;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public int getInitialDiceRoll1(){
        return this.initialDiceRoll1;
    }

    public int getInitialDiceRoll2(){
        return this.initialDiceRoll2;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public void setMatchTracker(MatchTracker matchTracker) {
        this.matchTracker = matchTracker;
        this.matchLength = matchTracker.getMatchLength();
    }

    public boolean getGameRunning(){return this.gameRunning;}

    public boolean isFileMode(){return isFileMode;}

    public BufferedReader getFileReader(){return fileReader;}


}