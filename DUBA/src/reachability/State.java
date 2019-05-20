package reachability;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class State {
  private final int global;
  private final Map<ITask, Stack<Integer>> stacks;

  public State(int global, Map<ITask, Stack<Integer>> stacks) {
    this.global = global;
    this.stacks = stacks;
  }

  /**
   * Returns the set of successor states to this state using the given rules.
   * 
   * @param toRun The current task.
   * @param toUse The list of rules to use.
   * @return The set of states that can be reached in one step using the given
   *         rules.
   */
  public Set<State> successors(ITask toRun, List<IRewriteRule> toUse) {
    Set<State> ans = new HashSet<>();
    for (IRewriteRule r : toUse) {
      if (r.canRewrite(this.global, this.stacks.get(toRun))) {
        ans.add(r.rewrite(this.stacks, toRun));
      }
    }
    return ans;
  }

  /**
   * Returns the set of tasks accounted for in this state
   * 
   * @return the set of tasks in this state
   */
  public Set<ITask> tasksInState() {
    return this.stacks.keySet();
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof State) {
      return this.global == ((State) other).global && this.stacks.equals(((State) other).stacks);
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.global + this.stacks.hashCode();
  }

  @Override
  public String toString() {
    String ans = "\nGlobal State: " + this.global;
    int taskNum = 0;
    for (ITask t : this.stacks.keySet()) {
      ans += " Task " + taskNum + ": " + this.stacks.get(t).toString();
      taskNum += 1;
    }
    return ans;
  }
}
