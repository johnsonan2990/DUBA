package reachability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class ARewriteRule implements IRewriteRule {
  protected final int globalFrom;
  protected final int globalTo;
  protected final Optional<Integer> topFrom;
  static int count = 0;

  ARewriteRule(int globalFrom, int topFrom, int globalTo) {
    this.globalFrom = globalFrom;
    this.globalTo = globalTo;
    this.topFrom = Optional.of(topFrom);
  }

  ARewriteRule(int globalFrom, int globalTo) {
    this.globalFrom = globalFrom;
    this.globalTo = globalTo;
    this.topFrom = Optional.empty();
  }

  /**
   * Builder for rules.
   * 
   * @author Andrew
   *
   */
  public static class RuleBuilder {
    public static IRewriteRule build(String ruleAsString) {
      List<Pair<String, Function<int[], IRewriteRule>>> builders = new ArrayList<>();
      builders.add(new Pair<>(
          "\\(?\\s?(\\d+),?\\s(\\d+)\\s?\\)?\\s?->\\s?\\(?\\s?(\\d+),?\\s(\\d+).(\\d+)\\s?\\)?",
          a -> PushRule.makePush(a)));
      builders.add(
          new Pair<>("\\(?\\s?(\\d+),?\\s(\\d+)\\s?\\)?\\s?->\\s?\\(?\\s?(\\d+),?\\s[e-]\\s?\\)?",
              a -> PopRule.makePop(a)));
      builders.add(
          new Pair<>("\\(?\\s?(\\d+),?\\s[e-]\\s?\\)?\\s?->\\s?\\(?\\s?(\\d+),?\\s(\\d+)\\s?\\)?",
              a -> PushRule.makePush(a)));
      builders
          .add(new Pair<>("\\(?\\s?(\\d+),?\\s\\s?\\)?\\s?->\\s?\\(?\\s?(\\d+),?\\s[e-]\\s?\\)?",
              a -> OverwriteRule.makeOverwrite(a)));
      builders.add(new Pair<>(
          "\\(?\\s?(\\d+),?\\s(\\d+)\\s?\\)?\\s?->\\s?\\(?\\s?(\\d+),?\\s(\\d+)\\s?\\)?",
          a -> OverwriteRule.makeOverwrite(a)));

      Function<int[], IRewriteRule> builder = null;
      int[] args = null;
      for (Pair<String, Function<int[], IRewriteRule>> pair : builders) {
        Matcher m = Pattern.compile(pair.getFirst()).matcher(ruleAsString);
        if (m.find()) {
          args = new int[m.groupCount()];
          for (int i = 0; i < m.groupCount(); i++) {
            args[i] = Integer.parseInt(m.group(i + 1));
          }
          builder = pair.getSecond();
          break;
        }
      }
      if (builder != null) {
        return builder.apply(args);
      }
      else {
        throw new IllegalArgumentException(
            "Bad string input \"" + ruleAsString + "\", please check format.");
      }
    }
  }

  // Can this rule be used on the given global and stack?
  public boolean canRewrite(int global, Stack<Integer> local) {
    if (this.topFrom.isPresent()) {
      return this.globalFrom == global && !local.isEmpty() && this.topFrom.get() == local.peek();
    }
    else {
      return this.globalFrom == global && local.isEmpty();
    }
  }

  @Override
  public void addEmergingSymbols(Set<Integer> acc) {
    // Do nothing because not a push
  }

  @Override
  public void addGlobalToIfPop(Set<Integer> acc) {
    // Do nothing
  }
}
