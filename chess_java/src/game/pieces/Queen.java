package game.pieces;

import game.Board;
import game.misc.Colour;
import game.misc.Move;
import game.misc.Position;

import java.util.ArrayList;
import java.util.List;

public class Queen extends DefaultPiece implements Piece {

  public Queen(Colour colour, Position position) {
    super(colour, position);
  }

  @Override
  public PieceType getPieceType() {
    return PieceType.QUEEN;
  }

  @Override
  public List<Move> getValidMoves(Board board) {
    ArrayList<Move> moves = new ArrayList<>();

    // A Queen is a hybrid of a Rook or Bishop
    // Note: We create a rook and bishop, but they do not exist on the board.
    Rook rook = new Rook(colour, position);
    moves.addAll(rook.getValidMoves(board));
    Bishop bishop = new Bishop(colour, position);
    moves.addAll(bishop.getValidMoves(board));

    // But the Queen makes the moves, not the Rook / Bishop.
    moves.forEach(m -> m.setPiece(this));

    return moves;
  }

  @Override
  public Piece copy() {
    return new Queen(colour, position.copy());
  }

  @Override
  public String toString() {
    if (colour.equals(Colour.WHITE)) {
      return "Q";
    } else {
      return "q";
    }
  }
}
