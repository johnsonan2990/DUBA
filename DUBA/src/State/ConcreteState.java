package State;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

import reachability.Abstractor;
import reachability.IConcreteRewriteRule;
import reachability.IRewriteRule;
import reachability.Pair;

public class ConcreteState implements IConcreteState {

  private final int global;
  private final List<Stack<Pair<Integer, Integer>>> locals;
  private final int delaysTaken;
  private static int clock = 0;
  public final int timestamp;

  ConcreteState(int global, List<Stack<Pair<Integer, Integer>>> locals, int delaysTaken) {
    this.global = global;
    this.locals = locals;
    this.delaysTaken = delaysTaken;
    this.timestamp = clock;
    clock++;
  }

  public ConcreteState(int global, List<Stack<Pair<Integer, Integer>>> locals) {
    this(global, locals, 0);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof ConcreteState) {
      ConcreteState that = (ConcreteState) other;
      return this.global == that.global && this.locals.equals(that.locals);
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.global, this.locals);
  }

  @Override
  public String toString() {
    StringBuilder ans = new StringBuilder();
    ans.append("\n<" + this.global + " | ");
    for (int i = 0; i < this.locals.size(); i += 1) {
      ans.append(locals.toString());
      ans.append(i == this.locals.size() - 1 ? ">" : " , ");
    }
    return ans.toString();
  }
  @Override
  public Set<IConcreteState> concreteSuccessors(int machineNum, List<IConcreteRewriteRule> toUse) {
    Set<IConcreteState> ans = new HashSet<>();
    for (IConcreteRewriteRule r : toUse) {
      if (r.canRewrite(this.global, this.locals.get(machineNum))) {
        ans.add(r.rewrite(this.global, this.locals, machineNum, this.delaysTaken));
      }
    }
    return ans;
  }

  @Override
  public IState abstraction() {
    List<Stack<Pair<Integer, Integer>>> list = new ArrayList<>();
    for (Stack<Pair<Integer, Integer>> s : this.locals) {
      Stack<Pair<Integer, Integer>> top = new Stack<>();
      if (!s.isEmpty()) {
        top.push(s.peek());
      }
      list.add(top);
    }
    return new ConcreteState(this.global, list, this.delaysTaken);
  }

  @Override
  public Pair<Integer, Stack<Pair<Integer, Integer>>> getLocalConcreteState(int machIdx) {
    return new Pair<>(this.global, this.locals.get(machIdx));
  }

  @Override
  public int getDelays() {
    // TODO Auto-generated method stub
    return this.delaysTaken;
  }

  @Override
  public IConcreteState cloneAndSetDelays() {
    // TODO Auto-generated method stub
    return this.cloneAndSetDelays(this.delaysTaken + 1);
  }

  @Override
  public IConcreteState cloneAndSetDelays(int newDelays) {
    return new ConcreteState(this.global, this.locals, newDelays);
  }

  @Override
  public int getTimestamp() {
    return this.timestamp;
  }

  @Override
  public Set<IState> successors(int machineNum, List<IRewriteRule> toUse) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Pair<Integer, Stack<Integer>> getLocalState(int machIdx) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IState concreteToAbstract(Abstractor abstractor) {
    return new StateWrapper(abstractor.abstractGlobal(this.global),
        abstractor.abstractLocals(this.locals));
  }

}
