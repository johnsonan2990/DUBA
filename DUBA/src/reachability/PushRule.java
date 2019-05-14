package reachability;

import java.util.Map;
import java.util.Stack;

public class PushRule extends ARewriteRule {
  private final int topTo;
  private final int toPush;

  PushRule(int globalFrom, int topFrom, int globalTo, int topTo, int toPush) {
    super(globalFrom, topFrom, globalTo);
    this.topTo = topTo;
    this.toPush = toPush;
  }

  @Override
  public State rewrite(Map<ITask, Stack<Integer>> stacks, ITask task) {
    // Need to copy states to avoid mutating them.
    Map<ITask, Stack<Integer>> nextMap = cloneMap(stacks);
    Stack<Integer> toRewrite = nextMap.get(task);
    toRewrite.pop();
    toRewrite.push(this.toPush);
    toRewrite.push(this.topTo);
    return new State(this.globalTo, nextMap);
  }

}
