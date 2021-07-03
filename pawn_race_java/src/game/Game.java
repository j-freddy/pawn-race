package game;

import game.misc.Colour;
import game.misc.Move;

import java.util.List;
import java.util.Random;

public class Game {
  private Random random = new Random();

  private Board board;
  private Player playerWhite;
  private Player playerBlack;
  private Player playerTurn;

  public Game() {
    this.board = new Board();
    this.playerWhite = new Player(Colour.WHITE, this.board);
    this.playerBlack = new Player(Colour.BLACK, this.board);
    this.playerTurn = this.playerWhite;
  }

  public Board getBoard() {
    return board;
  }

  public Player getPlayerTurn() {
    return playerTurn;
  }

  private void switchPlayers() {
    if (playerTurn.equals(playerWhite)) {
      playerTurn = playerBlack;
    } else {
      playerTurn = playerWhite;
    }
  }

  public boolean makeMove(Move move) {
    boolean validMove = playerTurn.makeMove(move);

    if (validMove) {
      this.switchPlayers();
      return true;
    } else {
      return false;
    }
  }

  public Move makeRandomValidMove() {
    List<Move> validMoves = playerTurn.getValidMoves();
    Move chosenMove = validMoves.get(random.nextInt(validMoves.size()));

    boolean success = makeMove(chosenMove);
    assert success;

    return chosenMove;
  }

  @Override
  public String toString() {
    return board.toString();
  }

  // Testing
  public static void main(String[] args) {
    Random random = new Random();
    Game game = new Game();
    Board boardCopy = game.board.copy();

    System.out.println(game);

    for (int i = 0; i < 4; i++) {
      Move chosenMove = game.makeRandomValidMove();

      // Small error: Once you make the move, the chosen move display becomes different.
      // It becomes of the form [Piece][PosTo]-[PosTo] rather than [Piece][PosFrom]-[PosTo]
      System.out.println(chosenMove);
      System.out.println(game);
    }

    System.out.println(boardCopy);

  }

}
