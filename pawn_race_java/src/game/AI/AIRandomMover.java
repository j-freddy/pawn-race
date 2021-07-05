package game.AI;

import game.Game;
import game.misc.Colour;
import game.misc.Move;

import java.util.List;
import java.util.Random;

public class AIRandomMover implements AI {
  private Random random = new Random();

  private final Game game;
  private final Colour colour;

  public AIRandomMover(Game game, Colour colour) {
    this.game = game;
    this.colour = colour;
  }

  @Override
  public Colour getColour() {
    return this.colour;
  }

  @Override
  public Move chooseMove() {
    List<Move> validMoves = game.getPlayerTurn().getValidMoves();
    return validMoves.get(random.nextInt(validMoves.size()));
  }
}
