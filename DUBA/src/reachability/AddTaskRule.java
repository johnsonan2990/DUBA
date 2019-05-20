package reachability;

import java.util.Map;
import java.util.Stack;

/**
 * A rule to overwrite the global and local states, then add a new task to the
 * state.
 */
public class AddTaskRule extends ARewriteRule {
  private final ITask toAdd;

  /**
   * 
   * @param globalFrom The global state needed to apply this rule
   * @param topFrom    The top of the local stack needed to apply this rule
   * @param globalTo   The global state to rewrite to
   * @param toAdd      The task to add
   */
  AddTaskRule(int globalFrom, int topFrom, int globalTo, ITask toAdd) {
    super(globalFrom, topFrom, globalTo);
    this.toAdd = toAdd;
  }

  @Override
  public State rewrite(Map<ITask, Stack<Integer>> stacks, ITask task) {
    Map<ITask, Stack<Integer>> newMap = cloneMap(stacks);
    newMap.put(this.toAdd, this.toAdd.initStack());
    return new State(this.globalTo, newMap);
  }

}
