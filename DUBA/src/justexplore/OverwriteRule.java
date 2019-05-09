package justexplore;

import java.util.Optional;
import java.util.Stack;

public class OverwriteRule extends ARewriteRule {
  private final Optional<Integer> topLocal;

  OverwriteRule(State from, Optional<Integer> toGlobal, Optional<Integer> topLocal) {
    super(from, toGlobal);
    this.topLocal = topLocal;
  }

  @Override
  public void rewriteStack(Stack<Integer> stack) {
    if (this.topLocal.isPresent()) {
      stack.pop();
      stack.push(this.topLocal.get());
    }
  }
}
