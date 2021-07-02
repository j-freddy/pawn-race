package game.pieces;

import game.Board;
import game.Player;
import game.misc.Colour;
import game.misc.Move;
import game.misc.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class King extends DefaultPiece implements Piece {

  public King(Colour colour, Position position) {
    super(colour, position);
  }

  @Override
  public PieceType getPieceType() {
    return PieceType.KING;
  }

  private Position makeMove(Piece piece, Board board, int moveId) {
    Player player = new Player(colour, board);
    Position pos = piece.getPosition().copy();

    switch (moveId) {
      case 0 -> pos.moveLeft();
      case 1 -> pos.moveLeft().moveUp();
      case 2 -> pos.moveUp();
      case 3 -> pos.moveRight().moveUp();
      case 4 -> pos.moveRight();
      case 5 -> pos.moveRight().moveDown();
      case 6 -> pos.moveDown();
      case 7 -> pos.moveLeft().moveDown();
    }

    Move move = new Move(piece, pos);
    player.makeMove(move, false);

    return pos;
  }

  private List<Move> getValidMoves(Board board, boolean canMoveIntoCheck) {
    ArrayList<Move> moves = new ArrayList<>();

    for (int i = 0; i < 8; i++) {
      Board boardCopy = board.copy();
      King kingCopy = boardCopy.getKing(colour);
      Position newPos = makeMove(kingCopy, boardCopy, i);

      // King cannot be in check after move
      // Move must not be out of bounds
      // We make use of lazy evaluation
      if (canMoveIntoCheck || !kingCopy.isInCheck(boardCopy)) {
        if (!newPos.isOutOfBounds(board)) {
          // The rest of the code is same as Knight
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
    }

    return moves;
  }

  // We make a copy of the board
  // We make a move, then check if that move causes the King to be in check.
  @Override
  public List<Move> getValidMoves(Board board) {
    return getValidMoves(board, false);
  }

  public boolean isInCheck(Board board) {
    for (Piece piece : board.getPieces()) {
      if (!piece.getColour().equals(colour)) {
        List<Move> moves;

        // Check if opposite coloured piece can capture king
        if(piece.getPieceType().equals(PieceType.KING)) {
          assert piece instanceof King;
          King king = (King) piece;
          // This prevents an infinite loop
          // Since getValidMoves calls isInCheck
          moves = king.getValidMoves(board, true);
        } else {
          moves = piece.getValidMoves(board);
        }

        Optional<Move> maybeMoveCheck = moves
            .stream()
            .filter(move -> move.getPosTo().equals(position))
            .findFirst();

        if (maybeMoveCheck.isPresent()) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public Piece copy() {
    return new King(colour, position.copy());
  }

  @Override
  public String toString() {
    if (colour.equals(Colour.WHITE)) {
      return "K";
    } else {
      return "k";
    }
  }
}
