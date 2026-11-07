# Manual GUI Checklist

Run the application with `.\mvnw.cmd exec:java "-Dexec.mainClass=com.tfdsimoes.tictactoe.TicTacToeApp"` on Windows or `sh ./mvnw exec:java -Dexec.mainClass="com.tfdsimoes.tictactoe.TicTacToeApp"` on Linux/macOS.

## Menu and navigation

- [ ] Each of the three modes is available and has an understandable accessible name and description.
- [ ] Use Tab and Shift+Tab to reach mode buttons, board cells, and Main menu; use Space to activate each focused button.
- [ ] Check that focus is visible and follows a predictable order through the menu, board, and Main menu control.
- [ ] Return to the main menu during an unfinished game; confirm the board is discarded and pending timers do not trigger later.
- [ ] Re-enter a mode after completing a round; confirm its scores and participant mark rotation are preserved.
- [ ] Start a mode for the first time in a fresh application session; confirm Player 1 is X.

## Game modes and round lifecycle

- [ ] Player vs Player allows both participants to place marks, rejects occupied cells, and announces a win.
- [ ] Player vs Machine accepts human moves and waits approximately one second before each machine move.
- [ ] After a completed human-first round, confirm the machine receives X, moves first after the one-second delay, and the human receives O.
- [ ] Machine vs Machine runs both turns with the one-second delay.
- [ ] After a win, the result remains visible for approximately three seconds, then the board resets with X moving first and participants swapping marks.
- [ ] Repeat after a draw; confirm the same three-second reset and participant swap behavior.
- [ ] Confirm scores belong to participants as their marks change, and draws are counted separately for each mode.

## Layout and accessibility

- [ ] Resize the window and confirm the board and controls remain visible and usable.
- [ ] Inspect board cell accessible names and descriptions before and after a move with the operating system's accessibility tooling.
- [ ] With a screen reader, inspect the current turn, round result, participant scores, and draw count; confirm updated accessible descriptions are understandable and changes are announced or otherwise discoverable.
- [ ] Verify keyboard focus and status/result feedback using at least one supported platform accessibility tool; record the operating system and screen reader if announcement behavior differs.
