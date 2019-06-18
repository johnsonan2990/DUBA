package reachability;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;

/**
 * A rule to overwrite the global state, the top of the local stack, and push a
 * new element onto the stack.
 * 
 * @author Andrew
 *
 */
class PushRule extends ARewriteRule {
  private final Optional<Integer> topTo;
  private final int toPush;

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
        && (IRewriteRule.stackBound == 0 || (local.size() < IRewriteRule.stackBound));
  }

  @Override
  public State rewrite(List<Stack<Integer>> stacks, int machineNum, int delays) {
    // Need to copy states to avoid mutating them.
    List<Stack<Integer>> nextList = State.cloneList(stacks);
    Stack<Integer> toRewrite = nextList.get(machineNum);
    if (this.topTo.isPresent()) {
      toRewrite.pop();
      toRewrite.push(this.topTo.get());
    }
    toRewrite.push(this.toPush);
    return new State(this.globalTo, nextList, delays);
  }

  @Override
  public List<IRewriteRule> overapproxRewrite(List<IRewriteRule> otherRules) {
    return Arrays.asList(this.topTo.isPresent() && this.topFrom.isPresent()
        ? OverwriteRule.makeOverwrite(this.globalFrom, this.topFrom.get(), this.globalTo,
            this.toPush)
        : PushRule.makePush(this.globalFrom, this.globalTo, this.toPush));
  }

  @Override
  public void makeNewRuleIfPush(int globalFrom, int topFrom, int globalTo, List<IRewriteRule> acc) {
    acc.add(OverwriteRule.makeOverwrite(globalFrom, topFrom, globalTo, this.topTo.get()));
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
}
