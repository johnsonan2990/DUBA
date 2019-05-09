package justexplore;

import java.util.Optional;
import java.util.Stack;

public class PushRule extends ARewriteRule {
  private final int topLocal;
  private final Optional<Integer> rewriteBeforePush;

  PushRule(State from, Optional<Integer> toGlobal, int topLocal) {
    this(from, toGlobal, topLocal, null);
  }

  // Constructor if you want to have the rule change the local state second from
  // the top
  PushRule(State from, Optional<Integer> toGlobal, int topLocal,
      Optional<Integer> rewriteBeforePush) {
    super(from, toGlobal);
    this.topLocal = topLocal;
    this.rewriteBeforePush = rewriteBeforePush;
  }

  @Override
  // Returns whether or not the given state matched this one's from
  public boolean canRewrite(State currentState) {
    return this.from.topEquivalent(currentState) && !currentState.metBound();
  }

  @Override
  public void rewriteStack(Stack<Integer> stack) {
    stack.pop();
    if (this.rewriteBeforePush.isPresent()) {
      stack.push(this.rewriteBeforePush.get());
    }
    stack.push(this.topLocal);
  }
}
