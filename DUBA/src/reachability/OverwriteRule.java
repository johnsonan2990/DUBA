package reachability;

import java.util.List;
import java.util.Stack;

/**
 * A rule to overwrite the global state and the top of the local stack.
 * 
 * @author Andrew
 *
 */
public class OverwriteRule extends ARewriteRule {
  private final int topTo;

  OverwriteRule(int globalFrom, int topFrom, int globalTo, int topTo) {
    super(globalFrom, topFrom, globalTo);
    this.topTo = topTo;
  }

  @Override
  public State rewrite(List<Stack<Integer>> stacks, int machineNum, int delays) {
    // Need to copy states to avoid mutating them.
    List<Stack<Integer>> nextList = State.cloneList(stacks);
    Stack<Integer> toRewrite = nextList.get(machineNum);
    toRewrite.pop();
    toRewrite.push(this.topTo);
    return new State(this.globalTo, nextList, delays);
  }

}
