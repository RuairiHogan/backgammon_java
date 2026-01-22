# 🎲 Backgammon Game (Java)

This project is a Java implementation of the classic board game **Backgammon**.
It is built using object-oriented programming principles and includes the full gameplay loop, dice rolling, board handling, and match tracking.

---

## ✅ Features

- Two-player Backgammon gameplay
- Board state representation and checker movement
- Dice rolling (supports doubles)
- Turn-based gameplay loop
- Match tracking across games
- Test runner included
- Dice roll logging for debugging (`dice_rolls.log`) :contentReference[oaicite:0]{index=0}

---

## 🗂 Files in This Project

The project includes the following Java files:

- `Main.java`
- `Game.java`
- `Board.java`
- `Player.java`
- `Dice.java`
- `MatchTracker.java`
- `TestGame.java`
- `dice_rolls.log` :contentReference[oaicite:1]{index=1}

---

## 🧩 Class Breakdown (Project Structure)

This project is organized into several classes, each with a clear responsibility.

### `Main.java`
**Purpose:** Starts the application.

**What it does:**
- Acts as the entry point (`public static void main`)
- Creates or starts a `Game`
- Runs the program from the command line/IDE

---

### `Game.java`
**Purpose:** Controls the main game flow.

**What it does:**
- Manages turns between players
- Rolls dice using the `Dice` class
- Calls `Board` functions to apply moves
- Detects when a player has won
- Logs dice roll outcomes into `dice_rolls.log` for debugging :contentReference[oaicite:2]{index=2}

---

### `Board.java`
**Purpose:** Represents the Backgammon board state.

**What it does:**
- Stores the layout of points (triangles) and checkers
- Updates the board when moves occur
- Helps enforce movement constraints (such as blocked points)
- Displays or prints the board state (if included in implementation)

---

### `Player.java`
**Purpose:** Represents a player in the game.

**What it does:**
- Stores player identity (name / color / side)
- Tracks player checkers and progress
- Works with `Game` to perform actions on a turn

---

### `Dice.java`
**Purpose:** Simulates dice rolling.

**What it does:**
- Generates two random dice values each turn
- Supports standard Backgammon dice logic (including doubles)

---

### `MatchTracker.java`
**Purpose:** Tracks the overall match.

**What it does:**
- Records game results (wins/losses)
- Tracks score or match history across multiple games
- Can be used to run a multi-round match instead of a single game

---

### `TestGame.java`
**Purpose:** Testing and debugging support.

**What it does:**
- Provides a test driver for the game
- Helps validate the logic without needing full manual play
- Useful for checking moves, dice behavior, and game state transitions

---

## ▶️ How to Compile and Run

Open a terminal inside the project folder.

### Compile
```bash
javac *.java
