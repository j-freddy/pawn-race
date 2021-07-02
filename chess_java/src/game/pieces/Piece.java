package game.pieces;

import game.Board;
import game.misc.Colour;
import game.misc.Move;
import game.misc.Position;

import java.util.List;

public interface Piece {
  Colour getColour();
  PieceType getPieceType();
  Position getPosition();
  void setPosition(Position position);
  List<Move> getValidMoves(Board board);
  List<Move> getFilteredValidMoves(Board board);
  Piece copy();
}
