package reachability;

import java.util.List;
import java.util.Stack;

public interface IRewriteRule {
  /**
   * Can this rule rewrite the given global and local state?
   * 
   * @param global The global state
   * @param local  The local stack for the machine
   * @return whether or not this rule should be used on the given state.
   */
  boolean canRewrite(int global, Stack<Integer> local);

  /**
   * The new State after this rewrite rule is applied to the given machine's
   * stack.
   * 
   * @param stacks  The current list of stacks for this state, indexed by machine
   * @param machine The machine index to rewrite
   * @param delays  The number of delays the state has taken
   * @return The next state after this rule is applied
   */
  State rewrite(List<Stack<Integer>> stacks, int machineNum, int delays);

  /**
   * Return the list of rules that this rule will turn into when to statically
   * overapproximate reachable states.
   * 
   * @param rules the other rules that the same machine has
   * @return a list of new rules that look like this rule for exhaustive
   *         reachability analysis
   */
  List<IRewriteRule> overapproxRewrite(List<IRewriteRule> otherRules);

  /**
   * Adds to the given list a new rule that represents what a pop rule might be
   * knowing about this rule
   * 
   * @param globalFrom The pop rule's global from
   * @param topFrom    the pop rule's top from
   * @param globalTo   the pop rule's global to
   */
  void makeNewRuleIfPush(int globalFrom, int topFrom, int globalTo, List<IRewriteRule> acc);

  /**
   * The bound for a machine's stack. A rule will not rewrite a stack if it would
   * make the stack larger than this.
   * Set to 0 to ignore the stack bound.
   */
  static int stackBound = 3;
}
