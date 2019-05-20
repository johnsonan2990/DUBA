package reachability;

import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Task implements ITask {
  private final List<IRewriteRule> rules;
  private final int localInit;

  Task(List<IRewriteRule> rules, int localInit) {
    this.rules = rules;
    this.localInit = localInit;
  }

  @Override
  public Set<State> getSuccessors(State state) {
    return state.successors(this, this.rules);
  }

  @Override
  public Stack<Integer> initStack() {
    Stack<Integer> ans = new Stack<>();
    ans.push(this.localInit);
    return ans;
  }
}
