package reachability;

import java.util.List;
import java.util.Stack;

import State.IConcreteState;
import State.IState;

/**
 * A function object that will abstract from concrete states to the world of
 * abstract ones for comparison with generators
 * 
 * @author Andrew
 *
 */
public interface Abstractor {
  IState apply(IConcreteState concrete);

  List<Stack<Integer>> abstractLocals(List<Stack<Pair<Integer, Integer>>> locals);

  int abstractGlobal(int oldGlobal);

}
