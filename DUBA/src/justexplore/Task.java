package justexplore;

import java.util.List;

// A terminating task, represented as a state machine
public class Task implements ITask {
  private final String name;
  private final List<IRewriteRule> rules;
  private State currState;

  Task(String name, List<IRewriteRule> rules, State initialState) {
    this.name = name;
    this.rules = rules;
    this.currState = initialState;
  }

  // Apply the first applicable rule, if any, to this Task's internal state
  // EFFECT: modifies this.currState
  public int step() {
    for (IRewriteRule rule : rules) {
      if (rule.canRewrite(this.currState)) {
        rule.rewrite(this.currState);
        break;
      }
    }
    return this.currState.getGlobal();
  }

  // Has this task terminated?
  public boolean isDone() {
    return this.rules
        .stream()
        .allMatch(rule -> !rule.canRewrite(this.currState));
  }

  public void updateGlobal(int currGlobal) {
    this.currState.updateGlobal(currGlobal);
  }

  @Override
  public String toString() {
    return this.name + ": " + this.currState.toString();
  }
}
