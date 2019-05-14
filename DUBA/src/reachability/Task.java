package reachability;

import java.util.List;
import java.util.Set;

public class Task implements ITask {
  private final List<IRewriteRule> rules;

  Task(List<IRewriteRule> rules) {
    this.rules = rules;
  }

  // Returns all of the possible successors to the given state using this task's
  // rewrite rules
  public Set<State> getSuccessors(State s) {
    return s.successors(this, this.rules);
  }
}
