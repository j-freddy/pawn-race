package game.pieces;

import game.Board;
import game.misc.Colour;
import game.misc.Move;
import game.misc.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Bishop extends DefaultPiece implements Piece {

  public Bishop (Colour colour, Position position) {
    super(colour, position);
  }

  @Override
  public PieceType getPieceType() {
    return PieceType.BISHOP;
  }

  // See Rook for comments
  @Override
  public List<Move> getValidMoves(Board board) {
    ArrayList<Move> moves = new ArrayList<>();

    Position newPos;

    for (int i = 0; i < 4; i++) {
      newPos = position.copy();

      while (true) {
        newPos = newPos.copy();

        if (i == 0) {
          // Left-up
          newPos.moveLeft().moveUp();
        } else if (i == 1) {
          // Right-up
          newPos.moveRight().moveUp();
        } else if (i == 2) {
          // Left-down
          newPos.moveLeft().moveDown();
        } else {
          // Right-down
          newPos.moveRight().moveDown();
        }

        if (newPos.isOutOfBounds(board)) {
          break;
        }

        Move move = new Move(this, newPos);

        Optional<Piece> maybePiece = board.findPieceAtPosition(newPos);

        if (maybePiece.isPresent()) {
          Piece piece = maybePiece.get();

          if (!piece.getColour().equals(getColour())) {
            moves.add(move);
          }
          break;
        } else {
          moves.add(move);
        }
      }
    }

    return moves;
  }

  @Override
  public Piece copy() {
    return new Bishop(colour, position.copy());
  }

  @Override
  public String toString() {
    if (colour.equals(Colour.WHITE)) {
      return "B";
    } else {
      return "b";
    }
  }
}
