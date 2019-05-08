package justexplore;

//A rule for a task that allows it to change one state into another
public abstract class ARewriteRule implements IRewriteRule {
  private final State from;
  private final State to;

  ARewriteRule(State from, State to) {
    this.from = from;
    this.to = to;
  }

  // Returns whether or not the given state matched this one's from
  public boolean canRewrite(State currentState) {
    return this.from.topEquivalent(currentState);
  }



}
