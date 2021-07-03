package game.misc;

import game.pieces.Piece;

import java.util.Objects;
import java.util.Set;

public class Move {
  private Piece piece;
  private Position posTo;
  private MoveType moveType;

  public Move (Piece piece, Position posTo, MoveType moveType) {
    this.piece = piece;
    this.posTo = posTo;
    this.moveType = moveType;
  }

  public Move (Piece piece, Position posTo) {
    this(piece, posTo, null);
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

  public MoveType getMoveType() {
    return moveType;
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
    return new Move(piece.copy(), posTo.copy(), moveType);
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
