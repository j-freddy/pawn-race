package app;

import game.Board;
import game.Game;
import game.misc.Colour;
import game.misc.Move;
import game.misc.Position;
import game.misc.Status;
import game.pieces.Pawn;
import game.pieces.Piece;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Optional;

enum BoardStatus {
  TO_SELECT_PIECE,
  TO_MOVE_PIECE
}

public class BoardController {
  private Main gui;
  private Canvas canvas;
  private Game game;

  private boolean eventsEnabled = false;
  private BoardStatus status = BoardStatus.TO_SELECT_PIECE;
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
        status = BoardStatus.TO_MOVE_PIECE;
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
    status = BoardStatus.TO_SELECT_PIECE;

    return success;
  }

  private void drawOverlay() {
    GraphicsContext ctx = canvas.getGraphicsContext2D();
    ctx.setFill(gui.colourOverlay);
    ctx.fillRect(0, 0, getBoardWidth(), getBoardHeight());
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

  // Temporary alternative, since images are not drawing (bug)
  // @x, @y is top left
  private void drawPawn(int x, int y, Colour colour) {
    GraphicsContext ctx = canvas.getGraphicsContext2D();

    if (colour == Colour.WHITE) {
      ctx.setFill(Color.WHITE);
    } else {
      ctx.setFill(Color.BLACK);
    }

    double diameterFactor = 0.7;
    double offsetFactor = (1 - diameterFactor) / 2;

    ctx.fillOval(
        x + offsetFactor * getCellWidth(),
        y + offsetFactor * getCellHeight(),
        getCellWidth() * diameterFactor,
        getCellHeight() * diameterFactor
    );

    // Draw black outline
    ctx.setLineWidth(getCellWidth() * 0.08);
    ctx.setFill(Color.BLACK);
    ctx.strokeOval(
        x + offsetFactor * getCellWidth(),
        y + offsetFactor * getCellHeight(),
        getCellWidth() * diameterFactor,
        getCellHeight() * diameterFactor
    );
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
        // ctx.drawImage(img, x, y, getCellWidth(), getCellHeight());

        // In Pawn Race, every piece is a pawn
        assert(piece instanceof Pawn);
        drawPawn(x, y, piece.getColour());
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

  private void drawGameResult() {
    String text;

    switch (game.getStatus()) {
      case WHITE_WINS -> text = "1 - 0";
      case BLACK_WINS -> text = "0 - 1";
      case DRAW       -> text = "0.5 - 0.5";
      default         -> text = "Error";
    }

    assert(!text.equals("Error"));

    GraphicsContext ctx = canvas.getGraphicsContext2D();
    drawOverlay();

    // Draw text
    ctx.setFill(Color.BLACK);
    ctx.setTextAlign(TextAlignment.CENTER);
    ctx.setTextBaseline(VPos.CENTER);
    // Note: I want to move this to Main, but I need getBoardWidth()
    // So I cannot create this font before calling the constructor
    ctx.setFont(new Font("sans-serif", getBoardWidth() * 0.1));
    // Make text bold
    ctx.setLineWidth(getCellWidth() * 0.04);
    ctx.fillText(text, getBoardWidth() / 2.0, getBoardHeight() / 2.0);
    ctx.strokeText(text, getBoardWidth() / 2.0, getBoardHeight() / 2.0);
  }

  public void draw() {
    drawBase();
    drawPieces();

    if (status == BoardStatus.TO_MOVE_PIECE) {
      drawValidMovesOfSelectedPiece();
    }

    if (game.getStatus() != Status.PLAYING) {
      drawGameResult();
    }
  }

  public void enableEvents() {
    if (eventsEnabled) {
      return;
    }

    canvas.setOnMouseClicked(event -> {
      if (game.getStatus() == Status.PLAYING) {
        Position cursorPos = getPosition(event.getSceneX(), event.getSceneY());

        if (status == BoardStatus.TO_SELECT_PIECE) {
          selectPiece(cursorPos);
        } else if (status == BoardStatus.TO_MOVE_PIECE) {
          boolean validMove = movePiece(cursorPos);

          // TODO: Delete this... this simulates a random moving AI
          /**
           * When making an actual AI, this code looks good
           * So, make AI a runnable object, and pass @game
           * Try not to pass @boardController
           * (only purpose: draw() after making move, so see if we can do that elsewhere)
           */
//          if (validMove) {
//            ThreadTest randomAI = new ThreadTest(game, this);
//            Thread thread = new Thread(randomAI);
//            thread.start();
//          }
          // TODO: End of delete section
        }
        draw();
      }

    });

    eventsEnabled = true;
  }

}
