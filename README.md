# Tic Tac Toe

A polished, desktop tic-tac-toe game built with Java Swing. Choose a local two-player match, challenge the computer, or watch two computer players compete.

## Requirements

- Java 17 or newer

## Build and run

Run the test suite and package the application from the project root.

**Windows PowerShell**

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd exec:java "-Dexec.mainClass=com.tfdsimoes.tictactoe.TicTacToeApp"
```

**Linux/macOS**

```shell
sh ./mvnw clean verify
```

Launch the game from the project root:

```shell
sh ./mvnw exec:java -Dexec.mainClass="com.tfdsimoes.tictactoe.TicTacToeApp"
```

Alternatively, run the compiled classes:

**Windows PowerShell**

```powershell
.\mvnw.cmd compile
java -cp target\classes com.tfdsimoes.tictactoe.TicTacToeApp
```

**Linux/macOS**

```shell
sh ./mvnw compile
java -cp target/classes com.tfdsimoes.tictactoe.TicTacToeApp
```

The Maven Wrapper downloads the pinned Maven distribution the first time it runs and needs network access for that download. The supported launch path is through the Maven Wrapper; the generated JAR is not a standalone executable.

## How to play

- **Player vs Player:** two people share the keyboard and take turns.
- **Player vs Machine:** play against the computer; player and machine switch marks after each round.
- **Machine vs Machine:** both sides are controlled by the computer.
- The computer uses minimax, so it will not lose when playing optimally. It waits one second before each move.
- After a win or draw, the result stays on screen for three seconds. The next round then starts automatically with X moving first and the participants swapping marks; a player who had X moves second as O in the next round.
- Scores follow participant identities and are kept separately for each mode for the lifetime of the running application. Each mode's mark rotation is also preserved when returning to the main menu and re-entering that mode; a new application session starts with Player 1 as X.

Use **Main menu** to choose another mode without losing that mode's scores or mark rotation. Leaving an unfinished round discards its board and cancels pending computer moves or round resets.

## Controls and accessibility

Use the mouse or keyboard. Press **Tab** or **Shift+Tab** to move focus and **Space** to activate a focused game-mode or board button. Board buttons have accessible names describing their row and column, and their accessible descriptions update to identify empty and played cells. Turn, result, and score labels also expose accessible names and descriptions; screen-reader announcement behavior can vary by platform.

## Troubleshooting

- The first wrapper run requires internet access to download Maven. If it fails, check the network/proxy settings and retry the command.
- Confirm that Java 17 or newer is installed and available on `PATH` by running `java -version`.
- Run `.\mvnw.cmd --batch-mode --no-transfer-progress verify` in PowerShell to see build and test diagnostics.
- A graphical desktop session is required to launch the Swing interface; builds and tests can run without opening the game window.

## Project layout

- `domain`: marks, board state, and rule enforcement.
- `game`: play modes, session orchestration, and score tracking.
- `ai`: computer move strategy.
- `ui`: Swing screens and rendering.

Game rules, computer decisions, session timing, scheduler behavior, and Swing panel interactions are tested:

```shell
sh ./mvnw test
```

`clean verify` generates a JaCoCo report at `target/site/jacoco/index.html` and enforces at least 80% line coverage separately for the `domain` and `game` packages. UI rendering is excluded from that threshold because most of it is presentation code; UI interactions and accessibility properties have focused tests and a manual checklist.

See [the manual GUI checklist](docs/manual-gui-checklist.md) for interactive verification steps.

## Contributing

Use Gitflow for changes: branch from `develop`, open a pull request back to `develop`, and keep unrelated fixes in separate branches. Run `.\mvnw.cmd --batch-mode --no-transfer-progress clean verify` on Windows or `sh ./mvnw --batch-mode --no-transfer-progress clean verify` on Linux/macOS before opening a pull request. The CI workflow checks Java 17 on Windows and Linux and Java 21 on Linux. Dependabot proposes monthly Maven and GitHub Actions updates. To run the optional dependency-review job, enable the repository dependency graph and set the `DEPENDENCY_REVIEW_ENABLED` repository variable to `true`; private repositories also require GitHub Advanced Security.

## License

This project is distributed under the Apache License, Version 2.0. See [LICENSE](LICENSE).
