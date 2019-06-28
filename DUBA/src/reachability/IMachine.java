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
   * @param state      the state to find the successors of
   * @param machineNum the index of machine running
   * @return The set of all successor states to the given one using this machine's
   *         rules
   */
  Set<State> getSuccessors(State state, int machineNum);

  /**
   * The initial stack for this machine
   * 
   * @return The initial stack for this machine.
   */
  Stack<Integer> initStack();

  /**
   * Simplifies this machine by transforming all of its rules into simpler ones
   * that guarantee exhaustive reachability analysis.
   * 
   * @param the Stack bound to use
   * @return The new machine that looks like this one but has simpler rules.
   */
  IMachine simplify(int bound);

  /**
   * Is the given state a generator for this machine?
   * 
   * @param s       the state to test.
   * @param machIdx the index of this machine in the state.
   * @return whether or not the given state looks like the target of a pop action
   *         for this machine
   */
  boolean isGenerator(State s, int machIdx);

}
