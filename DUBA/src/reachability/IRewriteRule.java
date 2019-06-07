package reachability;

import java.util.List;
import java.util.Stack;

public interface IRewriteRule {
  /**
   * Can this rule rewrite the given global and local state?
   * 
   * @param global The global state
   * @param local  The local stack for the machine
   * @return whether or not this rule should be used on the given state.
   */
  boolean canRewrite(int global, Stack<Integer> local);

  /**
   * The new State after this rewrite rule is applied to the given machine's
   * stack.
   * 
   * @param stacks  The current list of stacks for this state, indexed by machine
   * @param machine The machine index to rewrite
   * @param delays  The number of delays the state has taken
   * @return The next state after this rule is applied
   */
  State rewrite(List<Stack<Integer>> stacks, int machineNum, int delays);

  /**
   * The bound for a machine's stack. A rule will not rewrite a stack if it would
   * make the stack larger than this.
   */
  static int stackBound = 5;
}
