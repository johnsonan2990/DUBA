package reachability;

import java.util.List;
import java.util.Stack;

import State.IConcreteState;

public interface IConcreteRewriteRule {
  /**
   * Can this rule rewrite the given global and local state?
   * 
   * @param global The global state
   * @param local  The local stack for the machine
   * @return whether or not this rule should be used on the given state.
   */
  boolean canRewrite(int global, Stack<Pair<Integer, Integer>> local);

  /**
   * The new State after this rewrite rule is applied to the given machine's
   * stack.
   * 
   * @param stacks  The current list of stacks for this state, indexed by machine
   * @param machine The machine index to rewrite
   * @param delays  The number of delays the state has taken
   * @return The next state after this rule is applied
   */
  IConcreteState rewrite(int global, List<Stack<Pair<Integer, Integer>>> stacks, int machineNum,
      int delays);
}
