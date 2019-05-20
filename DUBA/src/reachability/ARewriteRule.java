package reachability;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public abstract class ARewriteRule implements IRewriteRule {
  protected final int globalFrom;
  protected final int globalTo;
  protected final int topFrom;

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
  

  protected static <T1, T2> Map<T1, Stack<T2>> cloneMap(Map<T1, Stack<T2>> map) {
    Map<T1, Stack<T2>> nextMap = new HashMap<>();
    for (T1 t : map.keySet()) {
      Stack<T2> nextStack = new Stack<>();
      for (int i = 0; i < map.get(t).size(); i += 1) {
        nextStack.push(map.get(t).get(i));
      }
      nextMap.put(t, nextStack);
    }
    return nextMap;
  }

}
