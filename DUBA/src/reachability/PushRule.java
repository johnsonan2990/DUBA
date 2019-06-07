package reachability;

import java.util.List;
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
  public State rewrite(List<Stack<Integer>> stacks, int machineNum, int delays) {
    // Need to copy states to avoid mutating them.
    List<Stack<Integer>> nextList = State.cloneList(stacks);
    Stack<Integer> toRewrite = nextList.get(machineNum);
    toRewrite.pop();
    toRewrite.push(this.topTo);
    toRewrite.push(this.toPush);
    return new State(this.globalTo, nextList, delays);
  }

}
