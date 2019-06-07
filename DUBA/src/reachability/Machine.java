package reachability;

import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Machine implements IMachine {
  private final List<IRewriteRule> rules;
  private final int localInit;

  Machine(List<IRewriteRule> rules, int localInit) {
    this.rules = rules;
    this.localInit = localInit;
  }

  @Override
  public Set<State> getSuccessors(State state, int machineNum) {
    return state.successors(machineNum, this.rules);
  }

  @Override
  public Stack<Integer> initStack() {
    Stack<Integer> ans = new Stack<>();
    ans.push(this.localInit);
    return ans;
  }
}
