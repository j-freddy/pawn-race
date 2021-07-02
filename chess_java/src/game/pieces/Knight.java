package game.pieces;

import game.Board;
import game.misc.Colour;
import game.misc.Move;
import game.misc.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Knight extends DefaultPiece implements Piece {

  public Knight(Colour colour, Position position) {
    super(colour, position);
  }

  @Override
  public PieceType getPieceType() {
    return PieceType.KNIGHT;
  }

  private void moveKnight(Position pos, int moveId) {
    switch (moveId) {
      case 0 -> pos.moveLeft().moveLeft().moveUp();
      case 1 -> pos.moveLeft().moveUp().moveUp();
      case 2 -> pos.moveRight().moveUp().moveUp();
      case 3 -> pos.moveRight().moveRight().moveUp();
      case 4 -> pos.moveRight().moveRight().moveDown();
      case 5 -> pos.moveRight().moveDown().moveDown();
      case 6 -> pos.moveLeft().moveDown().moveDown();
      case 7 -> pos.moveLeft().moveLeft().moveDown();
    }
  }

  @Override
  public List<Move> getValidMoves(Board board) {
    ArrayList<Move> moves = new ArrayList<>();

    // There are 8 moves for a knight in total.
    for (int i = 0; i < 8; i++) {
      Position newPos = position.copy();
      moveKnight(newPos, i);

      if (!newPos.isOutOfBounds(board)) {
        // Move isn't out of bounds, so we continue to check its validity
        Move move = new Move(this, newPos);

        Optional<Piece> maybePiece = board.findPieceAtPosition(newPos);

        if (maybePiece.isPresent()) {
          Piece piece = maybePiece.get();

          if (!piece.getColour().equals(getColour())) {
            // Different colour means move is valid (capture)
            moves.add(move);
          }
        } else {
          // We didn't hit a piece, so move is valid
          moves.add(move);
        }
      }
    }

    return moves;
  }

  @Override
  public Piece copy() {
    return new Knight(colour, position.copy());
  }

  @Override
  public String toString() {
    if (colour.equals(Colour.WHITE)) {
      return "N";
    } else {
      return "n";
    }
  }
}
