package app;

import game.Game;
import game.misc.Colour;
import game.pieces.Piece;
import game.pieces.PieceType;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

  // Note: I'm going to keep all of the data here for now.
  // Keeping it in one place for easier modifications
  /* ---------- Dimensions ---------- */
  public final int appWidth = 600;
  public final int appHeight = 600;
  // Note: Eventually I want to add a move history / other functionalities
  // So the app will have the canvas and some other panels.
  public final int boardWidth = 600;
  public final int boardHeight = 600;

  /* ---------- Graphics ---------- */
  public final Color colourCellLight = Color.rgb(233, 207, 190);
  public final Color colourCellDark = Color.rgb(172, 135, 72);
  public final Color colourCellHighlighted = Color.rgb(255, 255, 0, 0.3);

  /* ---------- Images ---------- */
  private final Map<Pair<PieceType, Colour>, Image> pieceImages = new HashMap<>();

  private Game game;
  private BoardController boardController;

  public Main() {
    game = new Game();

    // Dictionary of Piece images
    /**
     * Staunty Chess pieces, LiChess
     * https://github.com/ornicar/lila/tree/master/public/piece/staunty
     */
    pieceImages.put(new Pair<>(PieceType.BISHOP, Colour.WHITE), new Image("file:img/wB.png"));
    pieceImages.put(new Pair<>(PieceType.KING  , Colour.WHITE), new Image("file:img/wK.png"));
    pieceImages.put(new Pair<>(PieceType.KNIGHT, Colour.WHITE), new Image("file:img/wN.png"));
    pieceImages.put(new Pair<>(PieceType.PAWN  , Colour.WHITE), new Image("file:img/wP.png"));
    pieceImages.put(new Pair<>(PieceType.QUEEN , Colour.WHITE), new Image("file:img/wQ.png"));
    pieceImages.put(new Pair<>(PieceType.ROOK  , Colour.WHITE), new Image("file:img/wR.png"));

    pieceImages.put(new Pair<>(PieceType.BISHOP, Colour.BLACK), new Image("file:img/bB.png"));
    pieceImages.put(new Pair<>(PieceType.KING  , Colour.BLACK), new Image("file:img/bK.png"));
    pieceImages.put(new Pair<>(PieceType.KNIGHT, Colour.BLACK), new Image("file:img/bN.png"));
    pieceImages.put(new Pair<>(PieceType.PAWN  , Colour.BLACK), new Image("file:img/bP.png"));
    pieceImages.put(new Pair<>(PieceType.QUEEN , Colour.BLACK), new Image("file:img/bQ.png"));
    pieceImages.put(new Pair<>(PieceType.ROOK  , Colour.BLACK), new Image("file:img/bR.png"));
  }

  public int getCellWidth() {
    return boardWidth / game.getBoard().getNoCols();
  }

  public int getCellHeight() {
    return boardHeight / game.getBoard().getNoRows();
  }

  public Color getColourOfCell(int row, int column) {
    if (row % 2 == 1) {
      if (column % 2 == 0) {
        return colourCellDark;
      } else {
        return colourCellLight;
      }
    } else {
      if (column % 2 == 0) {
        return colourCellLight;
      } else {
        return colourCellDark;
      }
    }
  }

  public Image getImageOfPiece(Piece piece) {
    Pair<PieceType, Colour> key = new Pair<>(piece.getPieceType(), piece.getColour());
    Image img = pieceImages.get(key);

    return img;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Pane root = new Pane();
    Scene scene = new Scene(root, appWidth, appHeight);

    // Canvas for board
    Canvas canvas = new Canvas(boardWidth, boardHeight);
    root.getChildren().add(canvas);
    boardController = new BoardController(this, canvas, game);
    boardController.enableEvents();
    boardController.draw();

    // Set up app and display window
    primaryStage.setTitle("Chess Java");
    primaryStage.setScene(scene);
    primaryStage.show();
  }


  public static void main(String[] args) {
    launch(args);
  }
}
