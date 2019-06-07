package reachability;

import java.util.List;
import java.util.Stack;

/**
 * A rule to overwrite the global state and pop the local stack.
 * 
 * @author Andrew
 *
 */
public class PopRule extends ARewriteRule {

  PopRule(int globalFrom, int topFrom, int globalTo) {
    super(globalFrom, topFrom, globalTo);
  }

  @Override
  public State rewrite(List<Stack<Integer>> stacks, int machineNum, int delays) {
    List<Stack<Integer>> nextStacks = State.cloneList(stacks);
    nextStacks.get(machineNum).pop();
    return new State(this.globalTo, nextStacks, delays);
  }

}
