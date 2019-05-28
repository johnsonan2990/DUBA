package reachability;

import java.util.Set;
import java.util.Stack;

/**
 * A Machine (concurrent pushdown system) in a reachability explorer.
 * 
 * @author Andrew
 *
 */
public interface IMachine {
  /**
   * Returns all of the possible successors to the given state using this
   * machine's rewrite rules
   * 
   * @param state the state to find the successors of
   * @return The set of all successor states to the given one using this machine's
   *         rules
   */
  Set<State> getSuccessors(State state);

  /**
   * The initial stack for this machine
   * 
   * @return The initial stack for this machine.
   */
  Stack<Integer> initStack();

}
