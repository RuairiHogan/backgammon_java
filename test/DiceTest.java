// ******************************************************************
// Group number: 41
// Names: Patrick Kavanagh, Ruairi Hogan, Eoin Ryan
// GitHub IDs: pkav2, rh-ucd, JoeBahama
// ******************************************************************

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DiceTest {

    @Test
    void testRollDiceWithinRange() {
        for (int i = 0; i < 1000; i++) { // Testing multiple rolls for consistency
            int result = Dice.rollDice();
            assertTrue(result >= 1 && result <= 6,
                    "rollDice should return a value between 1 and 6");
        }
    }

    @Test
    void testRollDiceRandomness() {
        int[] counts = new int[6]; // Track counts of each dice face
        int totalRolls = 6000; // Roll a large number of times for a randomness check

        for (int i = 0; i < totalRolls; i++) {
            int result = Dice.rollDice();
            counts[result - 1]++; // Increment count for the rolled value
        }

        // Check that each face appears roughly equally
        double expectedFrequency = totalRolls / 6.0;
        for (int count : counts) {
            assertTrue(count > expectedFrequency * 0.8 && count < expectedFrequency * 1.2,
                    "Each face should appear roughly equally (±20%)");
        }
    }
}
