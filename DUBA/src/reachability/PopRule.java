package reachability;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * A rule to overwrite the global state and pop the local stack.
 * 
 * @author Andrew
 *
 */
class PopRule extends ARewriteRule {

  private PopRule(int globalFrom, int topFrom, int globalTo) {
    super(globalFrom, topFrom, globalTo);
  }

  static IRewriteRule makePop(int... args) {
    if (args.length == 3) {
      return new PopRule(args[0], args[1], args[2]);
    }
    else {
      throw new IllegalArgumentException(
          "Input length must be all integers, and the array must be of length 3");
    }
  }

  @Override
  public State rewrite(List<Stack<Integer>> stacks, int machineNum, int delays) {
    List<Stack<Integer>> nextStacks = State.cloneList(stacks);
    nextStacks.get(machineNum).pop();
    return new State(this.globalTo, nextStacks, delays);
  }

  @Override
  public List<IRewriteRule> overapproxRewrite(List<IRewriteRule> otherRules) {
    List<IRewriteRule> list = new ArrayList<>();
    for (IRewriteRule r : otherRules) {
      r.makeNewRuleIfPush(this.globalFrom, this.topFrom.get(), this.globalTo, list);
    }
    list.add(this);
    return list;
  }
  
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof PopRule) {
      PopRule that = (PopRule) other;
      return this.globalFrom == that.globalFrom
          && this.globalTo == that.globalTo
          && this.topFrom.equals(that.topFrom);
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.globalFrom, this.globalTo, this.topFrom);
  }
}
