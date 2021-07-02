package app;

import game.Board;
import game.Game;
import game.misc.Move;
import game.misc.Position;
import game.pieces.Piece;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;

enum Status {
  TO_SELECT_PIECE,
  TO_MOVE_PIECE
}

public class BoardController {
  private Main gui;
  private Canvas canvas;
  private Game game;

  private boolean eventsEnabled = false;
  private Status status = Status.TO_SELECT_PIECE;
  private Optional<Piece> selectedPiece = Optional.empty();

  public BoardController(Main gui, Canvas canvas, Game game) {
    this.gui = gui;
    this.canvas = canvas;
    this.game = game;
  }

  // -------------------- A bunch of aliases --------------------
  private int getBoardWidth() {
    return gui.boardWidth;
  }

  private int getBoardHeight() {
    return gui.boardHeight;
  }

  private int getCellWidth() {
    return gui.getCellWidth();
  }

  private int getCellHeight() {
    return gui.getCellHeight();
  }

  private Board getBoard() {
    return game.getBoard();
  }
  // -------------------- End of aliases --------------------

  private Position getPosition(double x, double y) {
    // Rounded to integers
    int xr = (int) x;
    int yr = (int) y;

    int row = getBoard().getNoRows() - yr / getCellHeight() - 1;
    int column = xr / getCellWidth();

    return new Position(row, column);
  }

  private boolean selectPiece(Position position) {
    selectedPiece = getBoard().findPieceAtPosition(position);

    if (selectedPiece.isPresent()) {
      Piece piece = selectedPiece.get();

      // If selected piece is the same colour as player to move, it is a valid piece.
      if (piece.getColour() == game.getPlayerTurn().getColour()) {
        status = Status.TO_MOVE_PIECE;
        return true;
      }
    }

    return false;
  }

  private boolean movePiece(Position position) {
    assert selectedPiece.isPresent();

    Move move = new Move(selectedPiece.get(), position);
    boolean success = game.makeMove(move);

    // For now, if an invalid move is made, deselect the piece.
    // So we do the same thing regardless if a move is valid or not
    selectedPiece = Optional.empty();
    status = Status.TO_SELECT_PIECE;

    return success;
  }

  // Draws board (without pieces)
  public void drawBase() {
    GraphicsContext ctx = canvas.getGraphicsContext2D();
    Board board = getBoard();

    for (int i = 0; i < board.getNoRows(); i++) {
      // By changing the column on the inner loop
      // We are rendering the board from left to right, per row.
      for (int j = 0; j < board.getNoCols(); j++) {
        // x depends on column
        int x = getCellWidth() * j;
        // y depends on row
        int y = getCellHeight() * i;

        ctx.setFill(gui.getColourOfCell(i, j));
        ctx.fillRect(x, y, getCellWidth(), getCellHeight());
      }
    }
  }

  public void drawPieces() {
    GraphicsContext ctx = canvas.getGraphicsContext2D();
    Board board = getBoard();

    for (Piece piece : board.getPieces()) {
      Position pos = piece.getPosition();

      int x = getCellWidth() * pos.getColumn();
      int y = getBoardHeight() - getCellHeight() * (pos.getRow() + 1);

      Image img = gui.getImageOfPiece(piece);

      if (img != null) {
        ctx.drawImage(img, x, y, getCellWidth(), getCellHeight());
      } else {
        ctx.setFill(Color.BLACK);
        ctx.fillRect(x, y, getCellWidth(), getCellHeight());
      }
    }
  }

  public void drawValidMovesOfSelectedPiece() {
    assert selectedPiece.isPresent();
    GraphicsContext ctx = canvas.getGraphicsContext2D();

    Piece piece = selectedPiece.get();
    List<Move> moves = piece.getFilteredValidMoves(getBoard());

    for (Move move : moves) {
      // x depends on column
      int x = getCellWidth() * move.getPosTo().getColumn();
      // y depends on row
      int y = getCellHeight() * (getBoard().getNoCols() - move.getPosTo().getRow() - 1);

      ctx.setFill(gui.colourCellHighlighted);
      ctx.fillRect(x, y, getCellWidth(), getCellHeight());
    }

    // Let's highlight the piece as well
    int x = getCellWidth() * piece.getPosition().getColumn();
    int y = getCellHeight() * (getBoard().getNoCols() - piece.getPosition().getRow() - 1);
    ctx.setFill(gui.colourCellHighlighted);
    ctx.fillRect(x, y, getCellWidth(), getCellHeight());
  }

  public void draw() {
    drawBase();
    drawPieces();
    if (status == Status.TO_MOVE_PIECE) {
      drawValidMovesOfSelectedPiece();
    }
  }

  public void enableEvents() {
    if (eventsEnabled) {
      return;
    }

    canvas.setOnMouseClicked(event -> {
      Position cursorPos = getPosition(event.getSceneX(), event.getSceneY());

      if (status == Status.TO_SELECT_PIECE) {
        selectPiece(cursorPos);
      } else if (status == Status.TO_MOVE_PIECE) {
        movePiece(cursorPos);
      }

      draw();
    });

    eventsEnabled = true;
  }

}
