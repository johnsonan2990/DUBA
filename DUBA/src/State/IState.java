package State;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import reachability.IRewriteRule;
import reachability.Pair;

public interface IState {
  /**
   * Returns the set of successor states to this state using the given rules.
   * 
   * @param toRun The current machine.
   * @param toUse The list of rules to use.
   * @return The set of states that can be reached in one step using the given
   *         rules.
   */
  public Set<IState> successors(int machineNum, List<IRewriteRule> toUse);
  
  /**
   * Returns the abstraction of this state; i.e. a state with the same global and tops of the stacks.
   */
  public IState abstraction();

  /**
   * Return the part of this state relevant to the given machine.
   * 
   * @param machIdx the machine to get the local state for
   * @return
   */
  public Pair<Integer, Stack<Integer>> getLocalState(int machIdx);

  /**
   * The delays taken to get to this state.
   * 
   * @return the number of delays.
   */
  public int getDelays();

  /**
   * Return a new state that is the same as this one with a new timeStamp, and
   * delays incremented by one.
   * 
   * @return the new state.
   */
  public IState cloneAndSetDelays();

  /**
   * Return a new state that is the same as this one with a new timeStamp, and
   * delays set to whatever was passed.
   * 
   * @return the new state.
   */
  public IState cloneAndSetDelays(int newDelays);

  /**
   * Returns the timestamp for this state.
   */
  public int getTimestamp();
}
