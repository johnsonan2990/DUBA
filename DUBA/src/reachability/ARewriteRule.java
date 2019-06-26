package reachability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
      Map<String, Function<int[], IRewriteRule>> builders = new HashMap<>();
      builders.put("\\(?\\s?(\\d+),?\\s?(\\d+)\\s?\\)?\\s?->\\s?\\(?\\s?(\\d+),?\\s?(\\d+)\\s?\\)?",
          a -> OverwriteRule.makeOverwrite(a));
      builders.put(
          "\\(?\\s?(\\d+),?\\s?(\\d+)\\s?\\)?\\s?->\\s?\\(?\\s?(\\d+),?\\s?(\\d+)\\.(\\d+)\\s?\\)?",
          a -> PushRule.makePush(a));
      builders.put("\\(?\\s?(\\d+),?\\s?(\\d+)\\s?\\)?\\s?->\\s?\\(?\\s?(\\d+),?\\s?[e-]\\s?\\)?",
          a -> PopRule.makePop(a));
      builders.put("\\(?\\s?(\\d+),?\\s?[e-]\\s?\\)?\\s?->\\s?\\(?\\s?(\\d+),?\\s?(\\d+)\\s?\\)?",
          a -> PushRule.makePush(a));
      builders.put("\\(?\\s?(\\d+),?\\s?[e-]\\s?\\)?\\s?->\\s?\\(?\\s?(\\d+),?\\s?[e-]\\s?\\)?",
          a -> OverwriteRule.makeOverwrite(a));

      Function<int[], IRewriteRule> builder = null;
      int[] args = null;
      for (String s : builders.keySet()) {
        Matcher m = Pattern.compile(s).matcher(ruleAsString);
        if (m.find()) {
          args = new int[m.groupCount()];
          for (int i = 0; i < args.length; i++) {
            args[i] = Integer.parseInt(m.group(i + 1));
          }
          builder = builders.get(s);
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
  public void makeNewRuleIfPush(int globalFrom, int topFrom, int globalTo, List<IRewriteRule> acc) {
    // Nothing to do unless this is a push rule!
  }

  @Override
  public boolean looksLikeThisTarget(Pair<Integer, Stack<Integer>> local, List<IRewriteRule> others,
      boolean preMet) {
    return false;
  }
}
