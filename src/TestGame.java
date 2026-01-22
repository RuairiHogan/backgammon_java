// ******************************************************************
// Group number: 41
// Names: Patrick Kavanagh, Ruairi Hogan, Eoin Ryan
// GitHub IDs: pkav2, rh-ucd, JoeBahama
// ******************************************************************

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class TestGame extends Game {
    private String command; // Holds a simulated command
    private BufferedReader testFileReader = null;
    private boolean testFileMode = false;


    // Method to set a simulated command, allowing controlled tests
    public void setCommand(String command) {
        this.command = command;
    }

    // Overrides the getCommand method
    @Override
    public String getCommand() {
        if (testFileMode) {
            try {
                String line = testFileReader.readLine();
                if (line != null) {
                    System.out.println("Command from file: " + line);
                    return line.trim().toLowerCase();
                } else {
                    // End of the file is reached so switch to standard input
                    System.out.println("End of file reached. Switching to standard input.");
                    testFileReader.close();
                    testFileMode = false;
                }
            } catch (IOException e) {
                System.out.println("Error reading the file. Switching to standard input.");
                testFileMode = false;
            }
        }

        return command;

    }

    public void TestFileModeOn() {
        testFileMode = true;
        String filename = "GameTest.txt";
        try {
            testFileReader = new BufferedReader(new FileReader(filename));
            System.out.println("File mode initialized with file: " + filename);
        } catch (
                FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        }
    }

}
