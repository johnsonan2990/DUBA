package reachability;

import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

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

  @Override
  public IMachine simplify() {
    return new Machine(this.rules.stream()
        .flatMap(r -> r.overapproxRewrite(this.rules).stream())
        .collect(Collectors.toList()),
        this.localInit);
  }
}
