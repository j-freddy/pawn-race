package game.pieces;

import game.Board;
import game.Player;
import game.misc.Colour;
import game.misc.Move;
import game.misc.Position;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultPiece implements Piece {

  protected Colour colour;
  protected Position position;

  public DefaultPiece (Colour colour, Position position) {
    this.colour = colour;
    this.position = position;
  }

  @Override
  public Colour getColour() {
    return colour;
  }

  @Override
  public PieceType getPieceType() {
    return null;
  }

  @Override
  public Position getPosition() {
    return position;
  }

  @Override
  public void setPosition(Position position) {
    this.position = position;
  }

  protected List<Move> filterMovesCausingPlayerToBeInCheck(List<Move> moves, Board board) {
    return moves
        .stream()
        .filter(move -> {
          // Make the move on a copy of board
          Board boardCopy = board.copy();
          Player player = new Player(colour, boardCopy);
          // We need to make copy of move, so it points to the correct piece on the board copy
          Piece pieceCopy = boardCopy.getPieceAtPosition(move.getPiece().getPosition());
          Position posTo = move.getPosTo().copy();
          Move moveCopy = new Move(pieceCopy, posTo);
          player.makeMove(moveCopy, false);

          // Check if king is in check
          King kingCopy = boardCopy.getKing(colour);
          return !kingCopy.isInCheck(boardCopy);
        })
        .collect(Collectors.toList());
  }

  @Override
  public List<Move> getValidMoves(Board board) {
    return null;
  }

  @Override
  public List<Move> getFilteredValidMoves(Board board) {
    // return filterMovesCausingPlayerToBeInCheck(getValidMoves(board), board);
    // In Pawn Race, we don't have a king
    return getValidMoves(board);
  }

  @Override
  public Piece copy() {
    return new DefaultPiece(colour, position.copy());
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Piece) {
      // Same piece if in the same position and same piece type
      // (latter is optional)
      return position.equals(((Piece) o).getPosition())
          && getPieceType().equals(((Piece) o).getPieceType());
    } else {
      return false;
    }
  }
}
