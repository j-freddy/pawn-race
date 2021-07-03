package game;

import game.misc.Colour;
import game.misc.Move;
import game.misc.MoveType;
import game.misc.Position;
import game.pieces.King;
import game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Player {
  private Colour colour;
  private Board board;

  public Player (Colour colour, Board board) {
    this.colour = colour;
    this.board = board;
  }

  public Colour getColour() {
    return colour;
  }

  public List<Move> getValidMoves() {
    ArrayList<Move> moves = new ArrayList<>();

    for (Piece piece : board.getPieces()) {
      if (piece.getColour().equals(colour)) {
        moves.addAll(piece.getFilteredValidMoves(board));
      }
    }

    return moves;
  }

  private boolean makeMove(Move move, boolean mustBeValid, Board b) {
    if (mustBeValid && !getValidMoves().contains(move)) {
      // Trying to make an invalid move
      return false;
    }

    // Set type of move
    Optional<Move> moveInValidMoves = getValidMoves()
        .stream()
        .filter(move::equals)
        .findFirst();

    assert(moveInValidMoves.isPresent());
    move = moveInValidMoves.get();

    // If there is a piece, capture that piece.
    Optional<Piece> maybePiece = b.findPieceAtPosition(move.getPosTo());
    if (maybePiece.isPresent()) {
      Piece pieceToCapture = maybePiece.get();
      b.removePiece(pieceToCapture);
    }

    // Pawn Race: En Passant
    if (move.getMoveType() == MoveType.EN_PASSANT) {
      maybePiece = b.findPieceAtPosition(move.getPosTo().getPosBelow(move.getPiece().getColour()));
      assert(maybePiece.isPresent());
      Piece pieceToCapture = maybePiece.get();
      b.removePiece(pieceToCapture);
    }

    // Move piece to square
    move.getPiece().setPosition(move.getPosTo());

    // Record piece last moved
    b.setLastMoved(move.getPiece());
    move.getPiece().incrementNumTimesMoved();

    return true;
  }

  public boolean makeMove(Move move, boolean mustBeValid) {
    return makeMove(move, mustBeValid, board);
  }

  public boolean makeMove(Move move) {
    return makeMove(move, true);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Player) {
      // Player is the same if colour is the same
      return colour.equals(((Player) o).colour);
    } else {
      return false;
    }
  }
}
