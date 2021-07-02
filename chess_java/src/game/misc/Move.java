package game.misc;

import game.pieces.Piece;

import java.util.Objects;
import java.util.Set;

public class Move {
  private Piece piece;
  private Position posTo;

  /*
    Update: For now, I don't think this is necessary.
    Especially if we are only searching for all valid moves,
    it feels like a waste to compute the type of move.
    That feels only necessary when a player actually makes a move
    (Then we have to compute whether it's a check, etc.
    to write the notation correctly – possibly...)
   */
  // We use a set since there may be multiple move types
  // For example, a move can be a capture and a check
  // private Set<MoveType> moveTypes;

  public Move (Piece piece, Position posTo) {
    this.piece = piece;
    this.posTo = posTo;
    // this.moveTypes = moveTypes;
  }

  public Piece getPiece() {
    return piece;
  }

  public Position getPosTo() {
    return posTo;
  }

  public void setPiece(Piece piece) {
    this.piece = piece;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Move) {
      return piece.equals(((Move) o).piece)
          && posTo.equals(((Move) o).posTo);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(piece, posTo);
  }

  public Move copy() {
    return new Move(piece.copy(), posTo.copy());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb
        .append(piece)
        .append(piece.getPosition())
        .append("-")
        .append(posTo);

    return sb.toString();
  }
}
