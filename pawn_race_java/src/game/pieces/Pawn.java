package game.pieces;

import game.Board;
import game.misc.Colour;
import game.misc.Move;
import game.misc.MoveType;
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

  private boolean checkMoveValidityAndAdd
      (Board board, ArrayList<Move> movesList, Position pos, boolean needCapture) {

    Optional<Piece> maybePiece = board.findPieceAtPosition(pos);

    if (needCapture) {
      if (maybePiece.isPresent()) {
        Piece piece = maybePiece.get();

        if (!piece.getColour().equals(getColour())) {
          Move move = new Move(this, pos, MoveType.CAPTURE);
          movesList.add(move);
          return true;
        }
      }
    } else {
      if (maybePiece.isEmpty()) {
        Move move = new Move(this, pos, MoveType.PASSIVE);
        movesList.add(move);
        return true;
      }
    }

    return false;
  }

  private boolean canEnPassant(Board board, Position pos) {
    Piece piece = board.getLastMoved();

    if (!(piece instanceof Pawn && piece.getNumTimesMoved() == 1)) {
      return false;
    }

    return piece.getPosition().equals(pos) && !piece.getColour().equals(colour);
  }

  @Override
  public List<Move> getValidMoves(Board board) {
    ArrayList<Move> moves = new ArrayList<>();
    Position newPos;

    // Move forward 1 square
    newPos = position.copy();
    newPos.moveForward(colour);
    assert !newPos.isOutOfBounds(board);
    boolean canMoveOneSquare = checkMoveValidityAndAdd(board, moves, newPos, false);

    // Move forward 2 squares
    int initialRowOffset = 1;
    // (Only possible if pawn can move forward 1 square)
    if (canMoveOneSquare) {
      if ((position.getRow() == initialRowOffset && colour.equals(Colour.WHITE))
          || (position.getRow() == (board.getNoRows() - 1) - initialRowOffset
          && colour.equals(Colour.BLACK))) {
        newPos = position.copy();
        newPos.moveForward(colour).moveForward(colour);
        assert !newPos.isOutOfBounds(board);

        checkMoveValidityAndAdd(board, moves, newPos, false);
      }
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

    // Check En Passant
    int enPassantRow = 4;
    if ((position.getRow() == enPassantRow && colour.equals(Colour.WHITE))
        || (position.getRow() == (board.getNoCols() - 1) - enPassantRow && colour.equals(Colour.BLACK))) {
      // En Passant left
      newPos = position.copy();
      newPos.moveLeft();

      if (canEnPassant(board, newPos)) {
        newPos.moveForward(colour);
        moves.add(new Move(this, newPos, MoveType.EN_PASSANT));
      }

      // En Passant right
      newPos = position.copy();
      newPos.moveRight();

      if (canEnPassant(board, newPos)) {
        newPos.moveForward(colour);
        moves.add(new Move(this, newPos, MoveType.EN_PASSANT));
      }
    }

    return moves;
  }

  public void promote(Board board) {
    Piece piece = new Queen(colour, position);
    board.removePiece(this);
    board.addPiece(piece);
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
