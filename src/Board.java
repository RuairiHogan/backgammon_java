// ******************************************************************
// Group number: 41
// Names: Patrick Kavanagh, Ruairi Hogan, Eoin Ryan
// GitHub IDs: pkav2, rh-ucd, JoeBahama
// ******************************************************************

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;


public class Board {

    // Defining the number of points and the maximum amount of checkers that can be stacked
    protected static final int POINTS = 24;
    private static final int MAX_CHECKERS = 5;

    // Lists to hold the state of the game, e.g positions of checkers, record of moves and dice rolls
    private final List<ArrayDeque<String>> board = new ArrayList<>();
    public final ArrayList<String> moves_List = new ArrayList<>();
    protected final ArrayList<Integer> moves_num = new ArrayList<>();
    protected final ArrayList<Integer> dice_rolled = new ArrayList<>();

    // Variables to track the number of checkers on the bar and checkers borne off for each player
    private int blackBarCount;
    private int whiteBarCount;
    private int blackBear;
    private int whiteBear;

    // Constructor to initialise the board and setup the game
    public Board() {
        whiteBarCount = 0;
        blackBarCount = 0;
        blackBear = 0;
        whiteBear = 0;

        // Initialising each point with an ArrayDeque to hold the checkers
        for (int i = 0; i < POINTS; i++) {
            board.add(new ArrayDeque<>());
        }

        initializeBoard();
    }

    // Method to reset the board to the initial setup
    public void resetBoard() {
        for (ArrayDeque<String> point : board) {
            point.clear();
        }
        moves_List.clear();
        moves_num.clear();
        dice_rolled.clear();

        blackBarCount = 0;
        whiteBarCount = 0;
        blackBear = 0;
        whiteBear = 0;

        initializeBoard();
        System.out.println("Board has been reset to its initial state.");
    }

    // Method to set the initial positions of checkers
    private void initializeBoard() {
        placeCheckers(5, "⚪", 5);
        placeCheckers(7, "⚪", 3);
        placeCheckers(12, "⚪", 5);
        placeCheckers(23, "⚪", 2);

        placeCheckers(0, "⚫", 2);
        placeCheckers(11, "⚫", 5);
        placeCheckers(16, "⚫", 3);
        placeCheckers(18, "⚫", 5);
    }

    // Method to display the current state of the board
    public void displayBoard(Player player, int dice1, int dice2, MatchTracker matchTracker) {
        // Prints the top of the board with the pip numbers, reversing the order depending on the player
        if (player.getPlayerNum() == 2) {
            for (int i = 11; i >= 6; i--) {
                System.out.printf("\t%2d ", i + 1);
            }
        } else {
            for (int i = (POINTS / 2); i < 18; i++) {
                System.out.printf("\t%2d ", i + 1);
            }
        }

        System.out.print("\t\t");
        if (player.getPlayerNum() == 2) {
            for (int i = 5; i >= 0; i--) {
                System.out.printf("\t%2d ", i + 1);
            }
        } else {
            for (int i = 18; i < POINTS; i++) {
                System.out.printf("\t%2d ", i + 1);
            }
        }

        System.out.println();

        // Prints the visual design between the numbers and the checkers
        String design;
        System.out.print("▩\t");
        for (int i = 0; i <= (POINTS / 4) - 1; i++) {
            design = (i % 2 == 0) ? "\u2009\u2009▽\t" : "\u2009\u2009▼\t";
            System.out.print(design);
        }
        System.out.print("\u2009\u2009▩\t▩\t");
        for (int i = 0; i <= (POINTS / 4) - 1; i++) {
            design = (i % 2 == 0) ? "\u2009\u2009▽\t" : "\u2009\u2009▼\t";
            System.out.print(design);
        }
        System.out.print("▩\t");

        System.out.println();

        // Prints the checkers on the board going over each point
        for (int i = 0; i <= MAX_CHECKERS; i++) {
            System.out.print("\u200A|");
            for (int j = (POINTS / 2); j < ((POINTS * 3) / 4); j++) {
                ArrayDeque<String> point = board.get(j);
                int size = point.size();
                if (size > i) {
                    // Display individual checkers if they exist
                    if (i < MAX_CHECKERS) {
                        if (point.contains("⚪")) {
                            System.out.print("\t⚪");
                        } else {
                            System.out.print("\t⚫");
                        }
                    } else if (size > MAX_CHECKERS) {
                        // When theres more checkers than the max, show the total amount using a encircled number
                        String countDisplay = point.contains("⚪")
                                ? getBlackEncircledNumber(size)
                                : getWhiteEncircledNumber(size);
                        System.out.print("\t\u200A\u200A" + countDisplay);
                    } else {
                        System.out.print("\t");
                    }
                } else {
                    System.out.print("\t");
                }
            }

            System.out.print("\t\u2009\u2009\u200A|");

            // Middle bar and bar counts, printing the details for the bar
            if (i == 5) {
                String blackDisplay;
                if (blackBarCount == 0) {
                    blackDisplay = "\t";
                } else if (blackBarCount == 1) {
                    blackDisplay = "⚫";
                } else {
                    blackDisplay = "\u2009\u200A" + getWhiteEncircledNumber(blackBarCount) + "\u2009";
                }
                System.out.print(blackDisplay);
            } else {
                System.out.print(" ");
            }

            if (i == 5) {
                System.out.print("\u200A|\t");
            } else {
                System.out.print("\t\u200A|\t");
            }

            // Displays the checkers for the top left part of the board
            for (int j = ((3 * POINTS) / 4); j < (POINTS); j++) {
                ArrayDeque<String> point = board.get(j);
                int size = point.size();
                if (size > i) {
                    if (i < MAX_CHECKERS) {
                        if (point.contains("⚪")) {
                            System.out.print("⚪\t");
                        } else {
                            System.out.print("⚫\t");
                        }
                    } else if (size > MAX_CHECKERS) {

                        String countDisplay = point.contains("⚪")
                                ? getBlackEncircledNumber(size)
                                : getWhiteEncircledNumber(size);
                        System.out.print("\u200A\u200A" + countDisplay + "\t");
                    } else {

                        System.out.print("\t");
                    }
                } else {
                    System.out.print("\t");
                }
            }
            System.out.print("\u200A|");
            System.out.println();

        }

        // Displaying the dice values and the doubling cubes value based on the current player
        if (dice1 != 0 && dice2 != 0 && player != null) {
            if (player.getPlayerNum() == 1) {
                System.out.println("\u200A|\t\t\t" + diceCharacter(dice1) + "\t" + diceCharacter(dice2) + "\t\t\t\u2009\u2009\u200A|\u2009\u200A\u200A" + matchTracker.getDoublingCubeValue() + "\u200A\u200A\u2009|" + "\t\t\t\t\t\t\t\u200A|");
            } else {
                System.out.println("\u200A|\t\t\t\t\t\t\t\u2009\u2009\u200A|\u2009\u200A\u200A" + matchTracker.getDoublingCubeValue() + "\u200A\u200A\u2009|\t\t\t" + diceCharacter(dice1) + "\t" + diceCharacter(dice2) + "\t\t\t\u200A|");
            }
        }

        // Reversing the display of checkers to show the bottom half of the code
        for (int i = MAX_CHECKERS; i >= 0; i--) {
            System.out.print("\u200A|");
            // Lower right part of the board
            for (int j = (POINTS / 2) - 1; j >= ((POINTS) / 4); j--) {
                ArrayDeque<String> point = board.get(j);
                int size = point.size();
                if (size > i) {
                    if (i < MAX_CHECKERS) {
                        if (point.contains("⚪")) {
                            System.out.print("\t⚪");
                        } else {
                            System.out.print("\t⚫");
                        }
                    } else if (size > MAX_CHECKERS) {
                        String countDisplay = point.contains("⚪")
                                ? getBlackEncircledNumber(size)
                                : getWhiteEncircledNumber(size);
                        System.out.print("\t\u200A\u200A" + countDisplay);
                    } else {

                        System.out.print("\t");
                    }
                } else {
                    System.out.print("\t");
                }
            }
            System.out.print("\t\u2009\u2009\u200A|");

            if (i == 5) {
                String whiteDisplay;
                if (whiteBarCount == 0) {
                    whiteDisplay = "\t";
                } else if (whiteBarCount == 1) {
                    whiteDisplay = "⚪";
                } else {
                    whiteDisplay = "\u2009\u200A" + getBlackEncircledNumber(whiteBarCount) + "\u2009";
                }
                System.out.print(whiteDisplay);
            } else {
                System.out.print(" ");
            }
            if (i == 5) {
                System.out.print("\u200A|\t");
            } else {
                System.out.print("\t\u200A|\t");
            }

            // Going over the bottom left part of the board
            for (int j = (POINTS / 4) - 1; j >= 0; j--) {
                ArrayDeque<String> point = board.get(j);
                int size = point.size();
                if (size > i) {
                    if (i < MAX_CHECKERS) {

                        if (point.contains("⚪")) {
                            System.out.print("⚪\t");
                        } else {
                            System.out.print("⚫\t");
                        }
                    } else if (size > MAX_CHECKERS) {
                        String countDisplay = point.contains("⚪")
                                ? getBlackEncircledNumber(size)
                                : getWhiteEncircledNumber(size);
                        System.out.print("\u200A\u200A" + countDisplay + "\t");
                    } else {

                        System.out.print("\t");
                    }
                } else {
                    System.out.print("\t");
                }
            }
            System.out.print("\u200A|");
            System.out.println();
        }
        System.out.print("▩\t");
        for (int i = 0; i <= (POINTS / 4) - 1; i++) {
            design = (i % 2 == 0) ? "\u2009\u2009△\t" : "\u2009\u2009▲\t";
            System.out.print(design);
        }
        System.out.print("\u2009\u2009▩\t▩\t");
        for (int i = 0; i <= (POINTS / 4) - 1; i++) {
            design = (i % 2 == 0) ? "\u2009\u2009△\t" : "\u2009\u2009▲\t";
            System.out.print(design);
        }
        System.out.print("▩\t");
        System.out.println();

        // Displaying the pip numbers again on the bottom
        if (player.getPlayerNum() == 1) {
            for (int i = 11; i >= 6; i--) {
                System.out.printf("\t%2d ", i + 1);
            }
        } else {
            for (int i = (POINTS / 2); i < 18; i++) {
                System.out.printf("\t%2d ", i + 1);
            }
        }

        System.out.print("\t\t");

        if (player.getPlayerNum() == 1) {
            for (int i = 5; i >= 0; i--) {
                System.out.printf("\t%2d ", i + 1);
            }
        } else {
            for (int i = 18; i < POINTS; i++) {
                System.out.printf("\t%2d ", i + 1);
            }
        }
        System.out.println();

    }

    // Placing checkers on the board at a specified point
    public void placeCheckers(int point, String checker, int count) {
        ArrayDeque<String> pointDeque = board.get(point); // Get the deque at the specified point
        String opponentChecker = (checker.equals("⚪")) ? "⚫" : "⚪";

        // If theres one opponent checker at the point, remove it and increase the bar count for that colour
        if (pointDeque.size() == 1 && pointDeque.contains(opponentChecker)) {
            pointDeque.pop();
            if (opponentChecker.equals("⚫")) {
                blackBarCount++;
            } else {
                whiteBarCount++;
            }
        }
        // Adding the specified number of checkers to the point
        for (int i = 0; i < count; i++) {
            pointDeque.push(checker);
        }
    }

    // Converting a dice roll value to a visual representation
    public String diceCharacter(int diceRoll) {
        return switch (diceRoll) {
            case 1 -> "⚀"; // Die Face 1
            case 2 -> "⚁"; // Die Face 2
            case 3 -> "⚂"; // Die Face 3
            case 4 -> "⚃"; // Die Face 4
            case 5 -> "⚄"; // Die Face 5
            case 6 -> "⚅"; // Die Face 6
            default -> "Invalid roll"; // Handle invalid dice rolls
        };
    }

    // Calculate possible moves based on the current dice rolls and game state
    public void getList(int diceRoll1, int diceRoll2, Player player) {
        ArrayDeque<String> index;

        // Handling moves from the bar first
        if (blackBarCount != 0 && player.getPlayerNum() == 2) {
            if (diceRoll1 != 100) {
                index = board.get(diceRoll1 - 1);
                if ((index.size() == 1 && index.contains("⚪")) || index.isEmpty() || index.contains("⚫")) {
                    moves_List.add("Play Bar - " + (diceRoll1));
                    moves_num.add(200);
                    moves_num.add(diceRoll1 - 1);
                    dice_rolled.add(1);
                }
            }
            if (diceRoll2 != 100) {
                index = board.get(diceRoll2 - 1);
                if ((index.size() == 1 && index.contains("⚪")) || index.isEmpty() || index.contains("⚫")) {
                    moves_List.add("Play Bar - " + (diceRoll2));
                    moves_num.add(200);
                    moves_num.add(diceRoll2 - 1);
                    dice_rolled.add(1);
                }
            }
        } else if (whiteBarCount != 0 && player.getPlayerNum() == 1) {
            if (diceRoll1 != 100) {
                index = board.get(POINTS - diceRoll1);
                if ((index.size() == 1 && index.contains("⚫")) || index.isEmpty() || index.contains("⚪")) {
                    moves_List.add("Play Bar - " + (diceRoll1));
                    moves_num.add(300);
                    moves_num.add(24 - diceRoll1);
                    dice_rolled.add(1);
                }
            }
            if (diceRoll2 != 100) {
                index = board.get(24 - diceRoll2);
                if ((index.size() == 1 && index.contains("⚫")) || index.isEmpty() || index.contains("⚪")) {
                    moves_List.add("Play Bar - " + (diceRoll2));
                    moves_num.add(300);
                    moves_num.add(24 - diceRoll2);
                    dice_rolled.add(1);
                }
            }
            // If theres no checkers on the bar, calculate the standard moves for the checkers on the board
        } else {
            for (int i = 0; i < POINTS; i++) {
                if (!board.get(i).isEmpty() && board.get(i).contains(player.getPlayerNum() == 1 ? "⚪" : "⚫")) {
                    canMove(i, diceRoll1, diceRoll2, player);
                }
            }
        }
        //Print all the calculated moves
        for (int i = 0; i < moves_List.size(); i++) {
            System.out.println(i + 1 + ") " + moves_List.get(i));
        }
    }

    // Determines if a checker at a point can be moved based on the dice rolls
    public void canMove(int j, int diceRoll1, int diceRoll2, Player player) {
        // Initialise Variables
        ArrayDeque<String> index;
        String destinationColour;
        String sourceColour;

        // Set colours based on the player
        if (player.getPlayerNum() == 1) {
            destinationColour = "⚫";
            sourceColour = "⚪";

            // If either dice rolls have no moves at the index
            if ((j - diceRoll1) >= 0 && diceRoll1 != 100) {
                index = board.get(j - diceRoll1);

                if (((index.size() == 1 && index.contains(destinationColour)) || index.isEmpty() || index.contains(sourceColour)) && board.get(j).contains(sourceColour)) {
                    moves_List.add("Play " + (j + 1) + " - " + (j + 1 - diceRoll1));
                    moves_num.add(j);
                    moves_num.add(j - diceRoll1);
                    dice_rolled.add(1);
                }
            }

            // Check if the checkers can bear off
            if (checkAllHome(player) && diceRoll1 != 100) {
                if (j - diceRoll1 < 0) {
                    String move = "Bear off " + (j + 1) + " with die 1";
                    moves_List.add(move);
                    moves_num.add(j);
                    moves_num.add(400);
                    dice_rolled.add(1);
                }
            }

            if ((j - diceRoll2) >= 0 && diceRoll2 != 100) {
                index = board.get(j - diceRoll2);

                if (((index.size() == 1 && index.contains(destinationColour)) || index.isEmpty() || index.contains(sourceColour)) && board.get(j).contains(sourceColour)) {
                    moves_List.add("Play " + (j + 1) + " - " + (j + 1 - diceRoll2));
                    moves_num.add(j);
                    moves_num.add(j - diceRoll2);
                    dice_rolled.add(2);
                }
            }
            // Check if the checkers can bear off
            if (checkAllHome(player) && diceRoll2 != 100) {
                if (j - diceRoll2 < 0) {
                    String move = "Bear off " + (j + 1) + " with die 2";
                    moves_List.add(move);
                    moves_num.add(j);
                    moves_num.add(400);
                    dice_rolled.add(2);

                }
            }


        }

        // If it's the second player's turn
        else if (player.getPlayerNum() == 2) {
            destinationColour = "⚪";
            sourceColour = "⚫";

            // If either dice rolls have no moves at the index
            if ((j + diceRoll1) < POINTS && diceRoll1 != 100) {
                index = board.get(j + diceRoll1);

                if (((index.size() == 1 && index.contains(destinationColour)) || index.isEmpty() || index.contains(sourceColour)) && board.get(j).contains(sourceColour)) {
                    moves_List.add("Play " + (25 - (j + 1)) + " - " + (25 - (j + 1 + diceRoll1)));
                    moves_num.add(j);
                    moves_num.add(j + diceRoll1);
                    dice_rolled.add(1);
                }
            }
            // Check can the checkers bear off
            if (checkAllHome(player) && diceRoll1 != 100) {
                if (j + diceRoll1 >= POINTS) {
                    moves_List.add("Bear off " + (25 - (j + 1)) + " die 1");
                    moves_num.add(j);
                    moves_num.add(500);
                    dice_rolled.add(1);
                }
            }
            if ((j + diceRoll2) < POINTS && diceRoll2 != 100) {
                index = board.get(j + diceRoll2);

                if (((index.size() == 1 && index.contains(destinationColour)) || index.isEmpty() || index.contains(sourceColour)) && board.get(j).contains(sourceColour)) {
                    moves_List.add("Play " + (25 - (j + 1)) + " - " + (25 - (j + 1 + diceRoll2)));
                    moves_num.add(j);
                    moves_num.add(j + diceRoll2);
                    dice_rolled.add(2);

                }
            }
            // Check can the checkers bear off
            if (checkAllHome(player) && diceRoll2 != 100) {
                if (j + diceRoll2 >= POINTS) {
                    moves_List.add("Bear off " + (25 - (j + 1)) + " with die 2");
                    moves_num.add(j);
                    moves_num.add(500);
                    dice_rolled.add(1);
                }
            }

        }
    }

    // Clears the moves from the list
    public void clearMoves() {
        moves_List.clear();
        moves_num.clear();
        dice_rolled.clear();
    }

    // Method to move checkers based on a selected move
    public Integer moveChecker(String moveIndex, Player player) {
        String checker;
        // Calculating the indexes for the move numbers and the destination
        int moveIndex1 = 2 * (Integer.parseInt(moveIndex) - 1);
        int moveIndex2 = 1 + 2 * (Integer.parseInt(moveIndex) - 1);
        int moveNumber1 = moves_num.get(moveIndex1);
        int moveNumber2 = moves_num.get(moveIndex2);

        // Handles moves from the bar or bearing off, and regular moves
        if (moveNumber1 == 200) {
            blackBarCount--;
            placeCheckers(moveNumber2, "⚫", 1);
        } else if (moveNumber1 == 300) {
            whiteBarCount--;
            placeCheckers(moveNumber2, "⚪", 1);
        } else if (moveNumber2 == 400) {
            removeChecker(moveNumber1, player);
            whiteBear++;
        } else if (moveNumber2 == 500) {
            removeChecker(moveNumber1, player);
            blackBear++;
        } else {
            // Moving the checker
            checker = removeChecker(moveNumber1, player);
            placeCheckers(moveNumber2, checker, 1);
        }
        return dice_rolled.get(Integer.parseInt(moveIndex) - 1);
    }

    // Remove a checker from a specified point
    public String removeChecker(int index, Player player) {
        String checkerColour;
        if (player.getPlayerNum() == 1) {
            checkerColour = "⚪";
        } else {
            checkerColour = "⚫";
        }
        if (!board.get(index).isEmpty() && board.get(index).contains(checkerColour)) {
            return board.get(index).pop(); // Remove and return the checker
        } else {
            return "1";
        }
    }

    // Calculates the total pip count for all of a players checkers
    public int calculatePipCount(Player player) {
        int pipCount = 0;
        String playerChecker = (player.getPlayerNum() == 1) ? "⚪" : "⚫";

        for (int i = 0; i < board.size(); i++) {
            ArrayDeque<String> point = board.get(i);

            // Count the number of checkers for this player at the current point
            int checkersAtPoint = 0;
            for (String checker : point) {
                if (checker.equals(playerChecker)) {
                    checkersAtPoint++;
                }
            }

            // Calculate the distance from the current point to the home of the player
            int distance;
            if (player.getPlayerNum() == 1) {

                distance = i + 1;
            } else {
                distance = POINTS - i;
            }
            // Add to pip count based on checkers at this point
            pipCount += checkersAtPoint * distance;
        }
        return pipCount;
    }

    // Check if all a players checkers are in their home part on the board
    public boolean checkAllHome(Player player) {
        String colour;
        int homeCount = 0;
        // Getting the home part of the boards ranges
        if (player.getPlayerNum() == 1) {
            colour = "⚪";
            for (int i = 6; i < POINTS; i++) {
                if (board.get(i).contains(colour)) {
                    homeCount++;
                }
            }
        } else {
            colour = "⚫";

            // Counting the checkers
            for (int i = 0; i < (POINTS * 3) / 4; i++) {
                if (board.get(i).contains(colour)) {
                    homeCount++;
                }
            }
        }
        return homeCount == 0;
    }

    // Get the count of checkers that have been borne off based on their colour
    public int getBearNumbers(String colour) {
        if (colour.equals("white")) {
            return whiteBear;
        } else if (colour.equals("black")) {
            return blackBear;
        } else {
            return 101;
        }
    }

    // Check if a player has won the game by bearing off all of their checkers
    public boolean checkWin(Player player, MatchTracker matchTracker) {
        boolean hasWon;
        int victoryType = 1;
        if (player.getPlayerNum() == 1) {
            hasWon = whiteBear == 15;
        } else {
            hasWon = blackBear == 15;
        }
        if (hasWon) {
            System.out.println(player.getName() + " has borne off all checkers!");

            // Checking what type of victory was won
            boolean isGammon = (player.getPlayerNum() == 1 && blackBear == 0) ||
                    (player.getPlayerNum() == 2 && whiteBear == 0);
            if (isGammon) {
                victoryType = 2;
            }

            boolean isBackgammon = (player.getPlayerNum() == 1 && blackBear == 0 &&
                    (blackBarCount > 0 || hasCheckersInOpponentHome("⚫"))) ||
                    (player.getPlayerNum() == 2 && whiteBear == 0 &&
                            (whiteBarCount > 0 || hasCheckersInOpponentHome("⚪")));
            if (isBackgammon) {
                victoryType = 3;
            }

            // Calculating the points won based off the doubling cube and the type of win
            int pointsWon = matchTracker.getDoublingCubeValue() * victoryType;
            matchTracker.recordWin(player, pointsWon);

            // Prints the type of win
            switch (victoryType) {
                case 2 -> System.out.println("Gammon! " + player.getName() + " wins 2x points!");
                case 3 -> System.out.println("Backgammon! " + player.getName() + " wins 3x points!");
                default -> System.out.println("Normal win.");
            }

            // Check if the game is over
            if (matchTracker.isMatchOver()) {
                System.out.println("The match is over! Winner: " + matchTracker.getWinner());
            }
        }
        return hasWon;
    }

    // Check if there are oppenents checkers in a players home part of the board
    protected boolean hasCheckersInOpponentHome(String checker) {
        int start = checker.equals("⚪") ? 18 : 0;
        int end = checker.equals("⚪") ? 24 : 6;

        for (int i = start; i < end; i++) {
            if (board.get(i).contains(checker)) {
                return true;
            }
        }
        return false;
    }

    // Validates a move entered by the player
    public boolean checkValidMove(String command) {
        try {
            int moveIndex = Integer.parseInt(command) - 1;
            if (moveIndex < 0 || moveIndex >= moves_List.size()) {
                System.out.print("Invalid move. Please enter another one: ");
                return true; // Invalid move
            } else {
                return false; // Valid move
            }
        } catch (NumberFormatException e) {
            System.out.print("Invalid input. Please enter a number: ");
            return true; // Invalid move (not a number)
        }
    }

    // Converting a number to a string with an encircled number for display
    protected String getBlackEncircledNumber(int number) {
        return switch (number) {
            case 1 -> "❶";
            case 2 -> "❷";
            case 3 -> "❸";
            case 4 -> "❹";
            case 5 -> "❺";
            case 6 -> "❻";
            case 7 -> "❼";
            case 8 -> "❽";
            case 9 -> "❾";
            case 10 -> "❿";
            default -> "(" + number + ")";
        };
    }

    // Converting a number to a string with an encircled number for display
    protected String getWhiteEncircledNumber(int number) {
        return switch (number) {
            case 1 -> "①";
            case 2 -> "②";
            case 3 -> "③";
            case 4 -> "④";
            case 5 -> "⑤";
            case 6 -> "⑥";
            case 7 -> "⑦";
            case 8 -> "⑧";
            case 9 -> "⑨";
            case 10 -> "⑩";
            default -> "(" + number + ")";
        };
    }


    // ******************************************************************
    // *------------------- Methods used for testing -------------------*
    // ******************************************************************


    public ArrayDeque<String> getBoardPoint(int pointIndex) {
        return board.get(pointIndex);
    }

    public int getBlackBarCount() {
        return blackBarCount;
    }

    public int getWhiteBarCount() {
        return whiteBarCount;
    }

    public ArrayList<String> getMovesList() {
        return moves_List;
    }

    public ArrayList<Integer> getMoves_num() {
        return moves_num;
    }

    public ArrayList<Integer> getDiceRolled() {
        return dice_rolled;
    }

    public void incrementWhiteBear() {
        whiteBear++;
    }

    public void setBlackBear(int count) {
        this.blackBear = count;
    }

    public void setWhiteBear(int count) {
        this.whiteBear = count;
    }

    public void setBlackBarCount(int count) {
        this.blackBarCount = count;
    }

    public void clearBoard() {
        for (ArrayDeque<String> point : board) {
            point.clear();
        }
        moves_List.clear();
        moves_num.clear();
        dice_rolled.clear();

        blackBarCount = 0;
        whiteBarCount = 0;
        blackBear = 0;
        whiteBear = 0;
    }
}