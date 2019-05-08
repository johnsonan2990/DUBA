package justexplore;

import java.util.Stack;

// A state of the global variable and the local stack for a task
public class State {
  private int global;
  private Stack<Integer> locals;

  State(int global) {
    this.global = global;
    this.locals = new Stack<Integer>();
  }
  // Is this state's global and top of stack equivalent to the given one?
  boolean topEquivalent(State that) {
    if (this.locals.isEmpty()) { 
        return that.locals.isEmpty();
      } else {
      return !that.locals.isEmpty() && this.global == that.global
          && this.locals.peek() == that.locals.peek();
      }
  }
  
  // Update this state's global state
  public void updateGlobal(int currGlobal) {
    this.global = currGlobal;
  }

  // Returns this state's global component
  public int getGlobal() {
    return this.global;
  }

  // Use the given rule to rewrite this state
  // EFFECT: modifies this
  void rewriteWith(IRewriteRule rule) {
    this.global = rule.rewriteGlobal(this.global);
    this.locals = rule.rewriteStack(this.locals);
  }
}
