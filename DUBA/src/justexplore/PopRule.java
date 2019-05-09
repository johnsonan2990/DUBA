package justexplore;

import java.util.Optional;
import java.util.Stack;

public class PopRule extends ARewriteRule {
  PopRule(State from, Optional<Integer> toGlobal) {
    super(from, toGlobal);
  }

  @Override
  public void rewriteStack(Stack<Integer> stack) {
    stack.pop();
  }
}
