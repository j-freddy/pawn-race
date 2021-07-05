package game.AI;

import game.Board;
import game.Game;
import game.Player;
import game.misc.Colour;
import game.misc.Move;
import lib.NodeTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

// AI is always the maximiser, regardless of colour.
public class AIMinimax implements AI {
  private Random random = new Random();

  private final Game game;
  private final Colour colour;
  private final int MAX_DEPTH = 5;
  private int visitedNodesCount;

  public AIMinimax(Game game, Colour colour) {
    this.game = game;
    this.colour = colour;
    this.visitedNodesCount = 0;
  }

  @Override
  public Colour getColour() {
    return colour;
  }

  private Colour getOppositeColour(Colour c) {
    return (c == Colour.WHITE) ? Colour.BLACK : Colour.WHITE;
  }

  // Give a static evaluation of the board (without looking ahead)
  // Assuming the position is not over
  private int evaluatePosition(Board board) {
    int evaluation = 0;

    // Factor 1: number of pieces on the board
    int numMyPieces = board.getPieces(colour).size();
    int numOpponentPieces = board.getPieces(getOppositeColour(colour)).size();
    evaluation += numMyPieces - numOpponentPieces;

    return evaluation;
  }

  private NodeData evaluateNode(NodeTree.Node<NodeData> node) {
    Board board = node.value.currBoard;
    Player player = new Player(node.value.colourToMove, board);

    if (board.checkWin(colour)) {
      node.value.weight = Integer.MAX_VALUE;
    } else if (board.checkWin(getOppositeColour(colour))) {
      node.value.weight = Integer.MIN_VALUE;
    } else if (board.checkDraw(player)) {
      node.value.weight = 0;
    } else {
      // Current position is not over
      node.value.weight = evaluatePosition(board);
    }

    return node.value;
  }

  /**
   * Minimax on NODE TREE
   *
   * Performs minimax algorithm on node tree
   * Updates all values of the tree up to @maxDepth, except leaf nodes
   */
  // Returns chosen move as index (i.e. nth child of root)
  private int minimax(NodeTree<NodeData> tree, int maxDepth, boolean isMaximiser) {
    if (tree.isEmpty()) {
      // Tree shouldn't be empty
      return -1;
    }

    NodeTree.Node<NodeData> root = tree.getRootNode();

    // Perform minimax (recursive)
    visitedNodesCount = 0;
    minimax(maxDepth, isMaximiser, root, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

    // Compare root value to its children's to determine which move to make
    ArrayList<Integer> optimalMovesIndex = new ArrayList<>();

    for (int i = 0; i < root.getChildren().size(); i++) {
      if (root.getChild(i).value.weight == root.value.weight) {
        optimalMovesIndex.add(i);
      }
    }

    // Due to the nature of backpropagation of minimax,
    // We should always find at least 1 children that has same value as root
    // We'll pick a random optimal move for fun
    // return optimalMovesIndex.get(random.nextInt(optimalMovesIndex.size()));
    /**
     * Note: We can't get a random move, due to the pruning...
     */
    return optimalMovesIndex.get(0);
  }

  private NodeData minimax(int maxDepth, boolean isMaximiser,
                           NodeTree.Node<NodeData> currNode, int currDepth, int alpha, int beta) {
    if (currNode == null) {
      // Null nodes should not exist in this implementation
      assert(false);
      System.out.println("Error: minimax encountered null node");
      return null;
    }

    if (currNode.isLeaf() || currDepth >= maxDepth) {
      visitedNodesCount++;
      return evaluateNode(currNode);
    }

    NodeData bestChildData, value;
    ArrayList<NodeData> childrenValues = new ArrayList<>();

    if (isMaximiser) {
      for (NodeTree.Node<NodeData> child : currNode.getChildren()) {
        NodeData nodeData = minimax(maxDepth, false, child, currDepth + 1, alpha, beta);
        assert(nodeData != null);
        int eval = nodeData.weight;
        childrenValues.add(nodeData);
        alpha = Math.max(alpha, eval);

        // Pruning magic here
        if (beta <= alpha) {
          break;
        }
      }

      bestChildData = Collections.max(childrenValues);
    } else {
      for (NodeTree.Node<NodeData> child : currNode.getChildren()) {
        NodeData nodeData = minimax(maxDepth, true, child, currDepth + 1, alpha, beta);
        assert(nodeData != null);
        int eval = nodeData.weight;
        childrenValues.add(nodeData);
        beta = Math.min(beta, eval);

        // Pruning magic here
        if (beta <= alpha) {
          break;
        }
      }

      bestChildData = Collections.min(childrenValues);
    }

    // Update current node value as the min/max of children
    // We only update the weight
    value = new NodeData(currNode.value.currBoard, currNode.value.move,
                         bestChildData.weight, currNode.value.colourToMove);
    currNode.value = value;

    visitedNodesCount++;
    return value;
  }

  private void extendNode(NodeTree<NodeData> tree, NodeTree.Node<NodeData> curr, Colour colourToMove,
                          int currDepth, int maxDepth) {
    // Base case: max depth exceeded
    if (currDepth >= maxDepth) {
      return;
    }
    // Base case: player wins in current board
    if (curr.value.currBoard.checkWin(colourToMove)) return;
    // Base case: opponent wins in current board
    if (curr.value.currBoard.checkWin(getOppositeColour(colourToMove))) return;

    // Base case: current board is a draw
    Player playerOfCurrBoard = new Player(colourToMove, curr.value.currBoard);

    if (curr.value.currBoard.checkDraw(playerOfCurrBoard)) {
      curr.value.weight = 0;
      return;
    }

    // Recursive case: extend node
    for (Move move : playerOfCurrBoard.getValidMoves()) {
      Board boardCopy = curr.value.currBoard.copy();

      // Make move and create node
      Player playerOfNewBoard = new Player(colourToMove, boardCopy);
      playerOfNewBoard.makeMove(move);

      NodeTree.Node<NodeData> child
          = tree.addNode(new NodeData(boardCopy, move, getOppositeColour(colourToMove)), curr);
      // Extend child node
      extendNode(tree, child, getOppositeColour(colourToMove), currDepth + 1, maxDepth);
    }
  }

  private NodeTree<NodeData> createTree(Board currBoard) {
    NodeTree<NodeData> tree = new NodeTree<>();
    assert(colour == game.getPlayerTurn().getColour());
    tree.setRootNode(new NodeData(currBoard, colour));
    extendNode(tree, tree.getRootNode(), colour, 0, MAX_DEPTH);
    return tree;
  }

  @Override
  public Move chooseMove() {
    Move chosenMove;

    NodeTree<NodeData> tree = createTree(game.getBoard());
    int childIndex = minimax(tree, MAX_DEPTH, true);
    NodeData chosenMoveData = tree.getRootNode().getChild(childIndex).value;

    System.out.println("Size of tree: " + tree.getSize());
    System.out.println("No. of visited nodes: " + visitedNodesCount);
    System.out.println("Evaluation (me): " + chosenMoveData.weight);

    chosenMove = chosenMoveData.move;
    return chosenMove;
  }

  static class NodeData implements Comparable<NodeData> {
    Board currBoard;
    Colour colourToMove;
    Move move;
    int weight;

    public NodeData(Board board, Move move, int weight, Colour colour) {
      this.currBoard = board;
      this.colourToMove = colour;
      this.move = move;
      this.weight = weight;
    }

    public NodeData(Board board, Move move, Colour colour) {
      this(board, move, 0, colour);
    }

    public NodeData(Board board, Colour colour) {
      this(board, null, 0, colour);
    }

    @Override
    public int compareTo(NodeData other) {
      return Integer.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {
      return String.valueOf(weight);
    }
  }
}
