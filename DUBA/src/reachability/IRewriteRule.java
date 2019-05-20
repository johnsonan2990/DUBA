package reachability;

import java.util.Map;
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
   * The new State after this rewrite rule is applied to the given task's stack.
   * 
   * @param stacks The current map from Tasks to local stacks, to be used in the
   *               next state
   * @param task   The task to rewrite for
   * @return The next state after this rule is applied
   */
  State rewrite(Map<ITask, Stack<Integer>> stacks, ITask task);

  /**
   * The bound for a machine's stack. A rule will not rewrite a stack if it would
   * make the stack larger than this.
   */
  static int stackBound = 5;
}
