package game.misc;

import game.Board;

// Position is 0-indexed
// (3, 5) means f4
public class Position {
  private final int LOWERCASE_CONSTANT = 97;
  private int row;
  private int column;

  public Position(int row, int column) {
    this.row = row;
    this.column = column;
  }

  public int getRow() {
    return row;
  }

  public int getColumn() {
    return column;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public void setColumn(int column) {
    this.column = column;
  }

  // Allows chaining
  public Position moveLeft() {
    this.column--;
    return this;
  }

  public Position moveRight() {
    this.column++;
    return this;
  }

  public Position moveUp() {
    this.row++;
    return this;
  }

  public Position moveDown() {
    this.row--;
    return this;
  }

  public Position moveForward(Colour colour) {
    if (colour.equals(Colour.WHITE)) {
      return moveUp();
    } else {
      return moveDown();
    }
  }

  public Position getPosBelow(Colour colour) {
    if (colour.equals(Colour.WHITE)) {
      return copy().moveDown();
    } else {
      return copy().moveUp();
    }
  }

  public boolean isOutOfBounds(Board board) {
    // row and column are 0-indexed
    return row < 0
        || row >= board.getNoRows()
        || column < 0
        || column >= board.getNoCols();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Position) {
      return row == ((Position) o).row
          && column == ((Position) o).column;
    } else {
      return false;
    }
  }

  public Position copy() {
    return new Position(row, column);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb
        .append((char) (column + LOWERCASE_CONSTANT))
        .append(row + 1);

    return sb.toString();
  }

  // Testing
  public static void main(String[] args) {
    Position p = new Position(3, 6);
    System.out.println(p);
  }
}
