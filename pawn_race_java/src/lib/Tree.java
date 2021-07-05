package lib;

import java.util.Optional;

public interface Tree<V> {
  Optional<V> getRoot();
  Optional<V> getNode(V value);
  int getSize();
}