package game.AI;

import app.BoardController;
import game.Game;
import game.misc.Colour;
import game.misc.Move;
import game.misc.Status;

// AIController supports any combination of AIs
// i.e. @aiWhite, @aiBlack can be null
public class AIController implements Runnable {
  // Time to wait in milliseconds
  private final int cooldownPeriod = 500;
  private AI aiWhite;
  private AI aiBlack;
  private Game game;
  // Needs refactoring: Keep GUI away from here
  private BoardController boardController;

  public AIController(AI aiWhite, AI aiBlack, Game game, BoardController boardController) {
    this.aiWhite = aiWhite;
    this.aiBlack = aiBlack;
    this.game = game;
    this.boardController = boardController;
  }

  @Override
  public void run() {
    try {
      while (game.getStatus() == Status.PLAYING) {
        Thread.sleep(cooldownPeriod);

        Move move = null;

        if (game.getPlayerTurn().getColour() == Colour.WHITE) {
          if (aiWhite != null) {
            move = aiWhite.chooseMove();
          }
        } else {
          if (aiBlack != null) {
            move = aiBlack.chooseMove();
          }
        }

        if (move != null) {
          boolean valid = game.makeMove(move);
          assert(valid);
          boardController.draw();
        }
      }
    } catch (InterruptedException e) {
      // Do nothing
    }

  }
}
