package reachability;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
  public List<IRewriteRule> overapproxRewrite(int bound, Set<Integer> emerging) {
    List<IRewriteRule> list = new ArrayList<>();
    list.add(this);
    for (int e : emerging) {
      list.add(
          new PopRuleBoundChecker(this.globalFrom, this.topFrom.get(), this.globalTo, e, bound));
    }
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

  @Override
  public void addGlobalToIfPop(Set<Integer> acc) {
    acc.add(this.globalTo);
  }

  /**
   * A rule that only activates when the stack is at the bound. When it does, puts
   * the given int at the bottom of the stack.
   * 
   * @author Andrew
   *
   */
  private class PopRuleBoundChecker extends PopRule {
    private final int bottomStack;
    private final int bound;

    PopRuleBoundChecker(int globalFrom, int topFrom, int globalTo, int bottomStack, int bound) {
      super(globalFrom, topFrom, globalTo);
      this.bottomStack = bottomStack;
      this.bound = bound;
    }

    @Override
    public boolean canRewrite(int global, Stack<Integer> local) {
      return super.canRewrite(global, local) && local.size() == this.bound;
    }

    @Override
    public State rewrite(List<Stack<Integer>> stacks, int machineNum, int delays) {
      List<Stack<Integer>> nextStacks = State.cloneList(stacks);
      Stack<Integer> s = nextStacks.get(machineNum);
      s.pop();
      s.add(0, this.bottomStack);
      return new State(this.globalTo, nextStacks, delays);
    }

  }

}