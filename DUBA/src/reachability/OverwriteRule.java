package reachability;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

/**
 * A rule to overwrite the global state and the top of the local stack.
 * 
 * @author Andrew
 *
 */
public class OverwriteRule extends ARewriteRule {
  private final Optional<Integer> topTo;

  private OverwriteRule(int globalFrom, int topFrom, int globalTo, int topTo) {
    super(globalFrom, topFrom, globalTo);
    this.topTo = Optional.of(topTo);
  }

  private OverwriteRule(int globalFrom, int globalTo) {
    super(globalFrom, globalTo);
    this.topTo = Optional.empty();
  }

  static IRewriteRule makeOverwrite(int... args) {
    if (args.length == 4) {
      return new OverwriteRule(args[0], args[1], args[2], args[3]);
    }
    else if (args.length == 2) {
      return new OverwriteRule(args[0], args[1]);
    }
    else {
      throw new IllegalArgumentException(
          "Input length must be all integers, and the array must be of length 4 or 2");
    }
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
    return new State(this.globalTo, nextList, delays);
  }

  @Override
  public List<IRewriteRule> overapproxRewrite(int bound, Set<Integer> emergingSymbols) {
    return Arrays.asList(this);
  }
  
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof OverwriteRule) {
      OverwriteRule that = (OverwriteRule) other;
      return this.globalFrom == that.globalFrom
          && this.globalTo == that.globalTo
          && this.topFrom.equals(that.topFrom)
          && this.topTo.equals(that.topTo);
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.globalFrom, this.globalTo, this.topFrom, this.topTo);
  }

}
