package game.pieces;

import game.Board;
import game.misc.Colour;
import game.misc.Move;
import game.misc.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Pawn extends DefaultPiece implements Piece {

  public Pawn(Colour colour, Position position) {
    super(colour, position);
  }

  @Override
  public PieceType getPieceType() {
    return PieceType.PAWN;
  }

  private void checkMoveValidityAndAdd
      (Board board, ArrayList<Move> movesList, Position pos, boolean needCapture) {

    Optional<Piece> maybePiece = board.findPieceAtPosition(pos);
    Move move = new Move(this, pos);

    if (needCapture) {
      if (maybePiece.isPresent()) {
        Piece piece = maybePiece.get();

        if (!piece.getColour().equals(getColour())) {
          movesList.add(move);
        }
      }
    } else {
      if (maybePiece.isEmpty()) {
        movesList.add(move);
      }
    }
  }

  @Override
  public List<Move> getValidMoves(Board board) {
    ArrayList<Move> moves = new ArrayList<>();
    Position newPos;

    // Move forward 1 square
    newPos = position.copy();
    newPos.moveForward(colour);
    assert !newPos.isOutOfBounds(board);

    checkMoveValidityAndAdd(board, moves, newPos, false);

    // Move forward 2 squares
    int initialRowOffset = 1;

    if ((position.getRow() == initialRowOffset && colour.equals(Colour.WHITE))
     || (position.getRow() == (board.getNoRows() - 1) - initialRowOffset
        && colour.equals(Colour.BLACK))) {
      newPos = position.copy();
      newPos.moveForward(colour).moveForward(colour);
      assert !newPos.isOutOfBounds(board);

      checkMoveValidityAndAdd(board, moves, newPos, false);
    }

    // Capture pieces (left)
    newPos = position.copy();
    newPos.moveLeft().moveForward(colour);
    if (!newPos.isOutOfBounds(board)) {
      checkMoveValidityAndAdd(board, moves, newPos, true);
    }

    // Capture pieces (right)
    newPos = position.copy();
    newPos.moveRight().moveForward(colour);
    if (!newPos.isOutOfBounds(board)) {
      checkMoveValidityAndAdd(board, moves, newPos, true);
    }

    return moves;
  }

  @Override
  public Piece copy() {
    return new Pawn(colour, position.copy());
  }

  @Override
  public String toString() {
    if (colour.equals(Colour.WHITE)) {
      return "P";
    } else {
      return "p";
    }
  }
}
