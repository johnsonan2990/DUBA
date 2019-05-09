package justexplore;

import java.util.Stack;

// A rule for a task that allows it to change one state into another
public interface IRewriteRule {

  // Does this rule apply to the given state?
  boolean canRewrite(State currentState);

  // Apply this rule to the given state
  // EFFECT: Modifies the given state
  void rewrite(State state);

  // Apply this rule to the given stack
  void rewriteStack(Stack<Integer> stack);

  // Apply this rule to the given global state
  int rewriteGlobal(int oldGlobal);
}
