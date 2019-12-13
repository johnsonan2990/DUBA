package State;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

import reachability.IRewriteRule;
import reachability.Pair;

public class StateWrapper implements IState {

  private final State state;
  private final int delaysTaken;
  private static int clock = 0;
  public final int timestamp;
  
  private StateWrapper(State state, int delaysTaken) {
    this(state, delaysTaken, clock);
    clock += 1;
  }

  private StateWrapper(State state, int delaysTaken, int timestamp) {
    this.state = state;
    this.delaysTaken = delaysTaken;
    this.timestamp = timestamp;
  }

  public StateWrapper(int global, List<Stack<Integer>> stacks, int delaysTaken) {
    this(new State(global, stacks), delaysTaken);
  }

  public StateWrapper(int global, List<Stack<Integer>> stacks) {
    this(global, stacks, 0);
  }

  @Override
  public Set<IState> successors(int machineNum, List<IRewriteRule> toUse) {
    Set<IState> ans = new HashSet<>();
    for (IRewriteRule r : toUse) {
      if (r.canRewrite(this.state.global, this.state.stacks.get(machineNum))) {
        ans.add(r.rewrite(this.state.global, this.state.stacks, machineNum, this.delaysTaken));
      }
    }
    return ans;
  }

  @Override
  public StateWrapper abstraction() {

    List<Stack<Integer>> list = new ArrayList<>();
    for (Stack<Integer> s : this.state.stacks) {
      Stack<Integer> top = new Stack<>();
      if (!s.isEmpty()) {
        top.push(s.peek());
      }
      list.add(top);

    }
    return new StateWrapper(new State(this.state.global, list),
        this.delaysTaken, this.timestamp);
  }

  @Override
  public Pair<Integer, Stack<Integer>> getLocalState(int machIdx) {
    return new Pair<>(this.state.global, this.state.stacks.get(machIdx));
  }

  @Override
  public int getDelays() {
    return this.delaysTaken;
  }

  @Override
  public IState cloneAndSetDelays() {
    return this.cloneAndSetDelays(this.delaysTaken + 1);
  }

  @Override
  public IState cloneAndSetDelays(int newDelays) {
    return new StateWrapper(this.state, newDelays);
  }
  
  public int getTimestamp() {
    return this.timestamp;
  }

  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    else if (other instanceof StateWrapper) {
      StateWrapper that = (StateWrapper) other;
      return this.state.equals(that.state) && this.delaysTaken == that.delaysTaken;
    }
    else {
      return false;
    }
  }

  public int hashCode() {
    return Objects.hash(this.state, this.delaysTaken);
  }

  public String toString() {
    return this.state.toString();
  }

  /**
   * Return a clone of a list of stacks.
   * 
   * @param list the list to clone.
   * @return a clone of the given.
   */
  public static List<Stack<Integer>> cloneList(List<Stack<Integer>> list) {
    List<Stack<Integer>> ans = new ArrayList<>();
    for (Stack<Integer> stack : list) {

      ans.add(cloneStack(stack));
    }
    return ans;
  }

  /**
   * Return a clone of the given stack.
   * 
   * @param stack the stack to clone.
   * @return a clone of the given stack.
   */
  private static Stack<Integer> cloneStack(Stack<Integer> stack) {
    Stack<Integer> newStack = new Stack<>();
    for (int i = 0; i < stack.size(); i += 1) {
      newStack.push(stack.get(i).intValue());
    }
    return newStack;
  }

  static String stackString(Stack<Integer> s) {
    StringBuilder ans = new StringBuilder();
    if (s.isEmpty()) {
      ans.append("e");
    }
    else {
      for (int i = s.size() - 1; i >= 0; i -= 1) {
        ans.append(s.get(i));
        ans.append(i == 0 ? "" : ".");
      }
    }
    return ans.toString();
  }
}
