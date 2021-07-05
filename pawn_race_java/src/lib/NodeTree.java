package lib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

/**
 * DISCLAIMER
 *
 * It is possible to add nodes with the same value multiple times to a NodeTree
 * It is up to the user to refrain from doing this
 */
public class NodeTree<V> implements Tree<V> {
  private Node<V> root;
  private int size = 0;

  public boolean isEmpty() {
    return root == null;
  }

  public Node<V> getRootNode() {
    return root;
  }

  @Override
  public Optional<V> getRoot() {
    return Optional.empty();
  }

  // Do not use getNode() for a NodeTree
  // It searches the entire tree, so it is inefficient
  // Also, the value stored may not be unique
  @Override
  public Optional<V> getNode(V value) {
    return Optional.empty();
  }

  @Override
  public int getSize() {
    return size;
  }

  public Node<V> setRootNode(V value) {
    root = new Node<>(value);
    size++;
    return root;
  }

  // Does not check if @value is already present in tree
  // Does not check if @parent is part of the tree
  // (this is for efficiency purposes, e.g. minimax)
  public Node<V> addNode(V value, Node<V> parent) {
    Node<V> node = new Node<>(value);
    parent.children.add(node);
    size++;
    return node;
  }

  private String toString(Node<V> curr) {
    StringBuilder sb = new StringBuilder();

    sb.append(curr);
    for (Node<V> child : curr.children) {
      sb.append("\n").append(toString(child));
    }

    return sb.toString();
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "Empty tree";
    }

    return toString(root);
  }

  // Node is public since we may access it
  public static class Node<V> {
    public V value;
    private ArrayList<Node<V>> children = new ArrayList<>();

    public Node(V value) {
      this.value = value;
    }

    public ArrayList<Node<V>> getChildren() {
      return children;
    }

    public Node<V> getChild(int index) {
      return children.get(index);
    }

    public boolean isLeaf() {
      return children.isEmpty();
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Node value: ").append(value);

      if (!isLeaf()) {
        sb.append(" (children: ");

        Iterator<Node<V>> it = children.iterator();
        if (it.hasNext()) {
          sb.append(it.next().value);
        }
        while (it.hasNext()) {
          sb.append(", ").append(it.next().value);
        }

        sb.append(")");
      }

      return sb.toString();
    }
  }

  // Testing
  public static void main(String[] args) {
    // Test: print node without children
    Node<Integer> node = new Node<>(3);
    System.out.println("" + node);
    System.out.println();

    // Test: build and print tree
    Node<Integer> root, secondChild;

    NodeTree<Integer> tree = new NodeTree<>();
    root = tree.setRootNode(3);
    // 1st layer
    tree.addNode(1, root);
    secondChild = tree.addNode(2, root);
    tree.addNode(3, root);
    tree.addNode(8, root);
    // 2nd layer
    tree.addNode(-5, secondChild);
    tree.addNode(-7, secondChild);
    tree.addNode(-4, secondChild);

    System.out.println(tree);

    // Test: size of tree
    System.out.println("Size of tree (expected 8): " + tree.getSize());
  }
}
