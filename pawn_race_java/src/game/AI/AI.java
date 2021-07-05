package game.AI;

import game.misc.Colour;
import game.misc.Move;

public interface AI {
  Colour getColour();
  Move chooseMove();
}
