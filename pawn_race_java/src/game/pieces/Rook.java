package game.pieces;

import game.Board;
import game.misc.Colour;
import game.misc.Move;
import game.misc.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Rook extends DefaultPiece implements Piece {

  public Rook (Colour colour, Position position) {
    super(colour, position);
  }

  @Override
  public PieceType getPieceType() {
    return PieceType.ROOK;
  }

  @Override
  public List<Move> getValidMoves(Board board) {
    ArrayList<Move> moves = new ArrayList<>();

    Position newPos;

    // 4 iterations – once for each direction (left, right, up, down)
    for (int i = 0; i < 4; i++) {
      newPos = position.copy();

      while (true) {
        // We can move first, since we don't have to validate our starting position
        // In fact, if we don't move first, it gets messed up
        // since the board detects our current piece at the start position
        newPos = newPos.copy();

        if (i == 0) {
          newPos.moveLeft();
        } else if (i == 1) {
          newPos.moveRight();
        } else if (i == 2) {
          newPos.moveUp();
        } else {
          newPos.moveDown();
        }

        if (newPos.isOutOfBounds(board)) {
          break;
        }

        Move move = new Move(this, newPos);

        Optional<Piece> maybePiece = board.findPieceAtPosition(newPos);

        if (maybePiece.isPresent()) {
          Piece piece = maybePiece.get();

          if (!piece.getColour().equals(getColour())) {
            // Different colour means we can capture piece
            moves.add(move);
          }
          // Same colour means we stop immediately
          break;
        } else {
          // We didn't hit a piece, so continue
          moves.add(move);
        }
      }
    }

    return moves;
  }

  @Override
  public Piece copy() {
    return new Rook(colour, position.copy());
  }

  @Override
  public String toString() {
    if (colour.equals(Colour.WHITE)) {
      return "R";
    } else {
      return "r";
    }
  }
}
