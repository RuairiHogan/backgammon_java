// ******************************************************************
// Group number: 41
// Names: Patrick Kavanagh, Ruairi Hogan, Eoin Ryan
// GitHub IDs: pkav2, rh-ucd, JoeBahama
// ******************************************************************

import java.util.Random;

public class Dice {
    // Variable for random number generation
    private static final Random random = new Random();

    // Method to simulate a dice roll of a six-sided dice
    public static int rollDice() {
        return random.nextInt(6) + 1;
    }
}
