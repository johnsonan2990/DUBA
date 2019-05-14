package reachability;

import java.util.Map;
import java.util.Stack;

public interface IRewriteRule {
  // Can this rule rewrite the given global and local state?
  boolean canRewrite(int global, Stack<Integer> local);

  // The new State after this rewrite rule is applied to the given task's stack.
  State rewrite(Map<ITask, Stack<Integer>> stacks, ITask task);

  // The bound for a machine's stack. A rule will not rewrite a stack if it would
  // make the stack larger than this.
  static int stackBound = 5;
}
