package reachability;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import State.IState;
import State.StateWrapper;

/**
 * A rule to overwrite the global state, the top of the local stack, and push a
 * new element onto the stack.
 * 
 * @author Andrew
 *
 */
class PushRule extends ARewriteRule {
  protected final Optional<Integer> topTo;
  protected final int toPush;

  /**
   * To make a push rule from a non-empty stack.
   * 
   * @param globalFrom The global variable from state
   * @param topFrom    The top of the stack to fire
   * @param globalTo   The global to state
   * @param topTo      the new stack second from the top
   * @param toPush     The new top of the stack
   */
  private PushRule(int globalFrom, int topFrom, int globalTo, int toPush, int topTo) {
    super(globalFrom, topFrom, globalTo);
    this.topTo = Optional.of(topTo);
    this.toPush = toPush;
  }

  /**
   * To make a push rule from the empty stack
   * 
   * @param globalFrom The global from state to fire
   * @param globalTo   The new global value
   * @param toPush     The new top of the stack.
   */
  private PushRule(int globalFrom, int globalTo, int toPush) {
    super(globalFrom, globalTo);
    this.topTo = Optional.empty();
    this.toPush = toPush;
  }

  static IRewriteRule makePush(int... args) {
    if (args.length == 5) {
      return new PushRule(args[0], args[1], args[2], args[3], args[4]);
    }
    else if (args.length == 3) {
      return new PushRule(args[0], args[1], args[2]);
    }
    else {
      throw new IllegalArgumentException(
          "Input length must be all integers, and the array must be of length 5 or 3");
    }
  }

  @Override
  public boolean canRewrite(int global, Stack<Integer> local) {
    return super.canRewrite(global, local)
        && ((IRewriteRule.stackBound == 0) || (local.size() < IRewriteRule.stackBound));
  }

  @Override
  public IState rewrite(int global, List<Stack<Integer>> stacks, int machineNum, int delays) {
    // Need to copy states to avoid mutating them.
    List<Stack<Integer>> nextList = StateWrapper.cloneList(stacks);
    Stack<Integer> toRewrite = nextList.get(machineNum);
    if (this.topTo.isPresent()) {
      toRewrite.pop();
      toRewrite.push(this.topTo.get());
    }
    toRewrite.push(this.toPush);
    return new StateWrapper(this.globalTo, nextList, delays);
  }

  @Override
  public List<IRewriteRule> overapproxRewrite(int bound, Set<Integer> emerging) {
    List<IRewriteRule> list = new ArrayList<>();
    list.add(this.topFrom.isPresent() && this.topTo.isPresent()
        ? new PushRuleBoundChecker(this.globalFrom, this.topFrom.get(), this.globalTo,
            this.toPush, this.topTo.get(), bound)
        : new PushRuleBoundChecker(this.globalFrom, this.globalTo, this.toPush, bound));
    return list;
  }

  @Override
  public void addEmergingSymbols(Set<Integer> acc) {
    if (this.topTo.isPresent()) {
      acc.add(this.topTo.get());
    }
  }
  
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof PushRule) {
      PushRule that = (PushRule) other;
      return this.globalFrom == that.globalFrom
          && this.globalTo == that.globalTo
          && this.topFrom.equals(that.topFrom)
          && this.topTo.equals(that.topTo)
          && this.toPush == that.toPush;
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.globalFrom, this.globalTo, this.topFrom, this.topTo, this.toPush);
  }

  /**
   * A rule that only activates when the stack is at the bound. When it does, it
   * drops the bottom of the stack.
   * 
   * @author Andrew
   *
   */
  private class PushRuleBoundChecker extends PushRule {
    private final int bound;

    PushRuleBoundChecker(int globalFrom, int topFrom, int globalTo, int toPush, int topTo,
        int bound) {
      super(globalFrom, topFrom, globalTo, toPush, topTo);
      this.bound = bound;
    }

    PushRuleBoundChecker(int globalFrom, int globalTo, int toPush, int bound) {
      super(globalFrom, globalTo, toPush);
      this.bound = bound;
    }

    @Override
    public boolean canRewrite(int global, Stack<Integer> local) {
      return super.canRewrite(global, local) && local.size() <= this.bound;
    }

    @Override
    public IState rewrite(int global, List<Stack<Integer>> stacks, int machineNum, int delays) {
      List<Stack<Integer>> nextList = StateWrapper.cloneList(stacks);
      Stack<Integer> toRewrite = nextList.get(machineNum);
      if (this.topTo.isPresent()) {
        toRewrite.pop();
        toRewrite.push(this.topTo.get());
      }
      toRewrite.push(this.toPush);
      if (toRewrite.size() == this.bound + 1) {
        toRewrite.removeElementAt(0);
      }
      return new StateWrapper(this.globalTo, nextList, delays);
    }
  }
}
