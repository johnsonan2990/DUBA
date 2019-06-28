package reachability;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

class Machine implements IMachine {
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
  public IMachine simplify(int bound) {
    Set<Integer> emerging = new HashSet<>();
    for (IRewriteRule r : this.rules) {
      r.addEmergingSymbols(emerging);
    }
    return new Machine(this.rules.stream()
        .flatMap(r -> r.overapproxRewrite(bound, emerging).stream())
        .collect(Collectors.toList()), this.localInit);
  }

  @Override
  public boolean isGenerator(State s, int machIdx) {
    Pair<Integer, Stack<Integer>> state = s.getLocalState(machIdx);
    Set<Integer> popGlobals = new HashSet<>();
    for (IRewriteRule r : this.rules) {
      r.addGlobalToIfPop(popGlobals);
    }
    Set<Integer> emerging = new HashSet<>();
    for (IRewriteRule r : this.rules) {
      r.addEmergingSymbols(emerging);
    }
    return popGlobals.contains(state.getFirst())
        && (state.getSecond().isEmpty() || emerging.contains(state.getSecond().peek()));
  }
}
