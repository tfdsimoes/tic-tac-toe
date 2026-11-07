# Tic Tac Toe

A polished, desktop tic-tac-toe game built with Java Swing. Choose a local two-player match, challenge the computer, or watch two computer players compete.

## Requirements

- Java 17 or newer

## Build and run

Run the test suite and package the application:

```shell
sh ./mvnw clean verify
```

Launch the game from the project root:

```shell
sh ./mvnw exec:java -Dexec.mainClass="com.tfdsimoes.tictactoe.TicTacToeApp"
```

Alternatively, run the packaged classes after compiling:

```shell
sh ./mvnw compile
java -cp target/classes com.tfdsimoes.tictactoe.TicTacToeApp
```

On Windows, use `mvnw.cmd` instead of `./mvnw`. The wrapper downloads the pinned Maven distribution on its first run.

## How to play

- **Player vs Player:** two people share the keyboard and take turns.
- **Player vs Machine:** play against the computer; player and machine switch marks after each round.
- **Machine vs Machine:** both sides are controlled by the computer.
- The computer uses minimax, so it will not lose when playing optimally. It waits one second before each move.
- After a win or draw, the result stays on screen for three seconds. The next round then starts automatically with X moving first and the participants swapping marks; a player who had X moves second as O in the next round.
- The scoreboard follows participant identities and is kept separately for each mode for the lifetime of the running application.

Use **Main menu** to choose another mode without losing the session scoreboard.

## Project layout

- `domain`: marks, board state, and rule enforcement.
- `game`: play modes and session score tracking.
- `ai`: computer move strategy.
- `ui`: Swing screens and round timing.

Game rules and computer decisions are tested independently of the graphical interface:

```shell
sh ./mvnw test
```
