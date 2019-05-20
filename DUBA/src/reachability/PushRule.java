package reachability;

import java.util.Map;
import java.util.Stack;

/**
 * A rule to overwrite the global state, the top of the local stack, and push a
 * new element onto the stack.
 * 
 * @author Andrew
 *
 */
public class PushRule extends ARewriteRule {
  private final int topTo;
  private final int toPush;

  PushRule(int globalFrom, int topFrom, int globalTo, int topTo, int toPush) {
    super(globalFrom, topFrom, globalTo);
    this.topTo = topTo;
    this.toPush = toPush;
  }

  @Override
  public boolean canRewrite(int global, Stack<Integer> local) {
    return !(local.size() >= IRewriteRule.stackBound) && super.canRewrite(global, local);
  }

  @Override
  public State rewrite(Map<ITask, Stack<Integer>> stacks, ITask task) {
    // Need to copy states to avoid mutating them.
    Map<ITask, Stack<Integer>> nextMap = cloneMap(stacks);
    Stack<Integer> toRewrite = nextMap.get(task);
    toRewrite.pop();
    toRewrite.push(this.topTo);
    toRewrite.push(this.toPush);
    return new State(this.globalTo, nextMap);
  }

}
