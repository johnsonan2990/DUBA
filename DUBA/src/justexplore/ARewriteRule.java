package justexplore;

import java.util.Optional;

//A rule for a task that allows it to change one state into another
public abstract class ARewriteRule implements IRewriteRule {
  protected final State from;
  protected final Optional<Integer> toGlobal;

  ARewriteRule(State from, Optional<Integer> toGlobal) {
    this.from = from;
    this.toGlobal = toGlobal;
  }

  // Returns whether or not the given state matched this one's from
  public boolean canRewrite(State currentState) {
    return this.from.topEquivalent(currentState);
  }

  // rewrites the given state using this rule
  // EFFECT: modifies the given state
  public void rewrite(State state) {
    state.rewriteWith(this);
  }

  public int rewriteGlobal(int oldGlobal) {
    if (this.toGlobal.isPresent()) {
      return this.toGlobal.get();
    }
    else {
      return oldGlobal;
    }
  }
}
