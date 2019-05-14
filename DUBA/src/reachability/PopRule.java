package reachability;

import java.util.Map;
import java.util.Stack;

public class PopRule extends ARewriteRule {

  PopRule(int globalFrom, int topFrom, int globalTo) {
    super(globalFrom, topFrom, globalTo);
  }

  @Override
  public State rewrite(Map<ITask, Stack<Integer>> stacks, ITask task) {
    Map<ITask, Stack<Integer>> nextMap = cloneMap(stacks);
    Stack<Integer> toRewrite = nextMap.get(task);
    toRewrite.pop();
    return new State(this.globalTo, nextMap);
  }

}