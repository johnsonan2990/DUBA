package reachability;

import java.util.Stack;

public abstract class ARewriteRule implements IRewriteRule {
  protected final int globalFrom;
  protected final int globalTo;
  protected final int topFrom;
  static int count = 0;

  ARewriteRule(int globalFrom, int topFrom, int globalTo) {
    this.globalFrom = globalFrom;
    this.globalTo = globalTo;
    this.topFrom = topFrom;
  }

  // Can this rule be used on the given global and stack?
  public boolean canRewrite(int global, Stack<Integer> local) {
    return this.globalFrom == global 
        && !local.isEmpty()
        && this.topFrom == local.peek().intValue();
  }


}
