package reachability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

public class State {
  private final int global;
  private final List<Stack<Integer>> stacks;
  private final int delaysTaken;
  private static int clock;
  public final int timeStamp;

  public State(int global, List<Stack<Integer>> stacks) {
    this(global, stacks, 0);
  }

  public State(int global, List<Stack<Integer>> stacks, int delaysTaken) {
    this.global = global;
    this.stacks = stacks;
    this.delaysTaken = delaysTaken;
    this.timeStamp = clock;
    clock += 1;
  }

  private State(int global, List<Stack<Integer>> stacks, int delaysTaken, int timeStamp) {
    this.global = global;
    this.stacks = stacks;
    this.delaysTaken = delaysTaken;
    this.timeStamp = timeStamp;
  }

  /**
   * Returns the set of successor states to this state using the given rules.
   * 
   * @param toRun The current machine.
   * @param toUse The list of rules to use.
   * @return The set of states that can be reached in one step using the given
   *         rules.
   */
  public Set<State> successors(int machineNum, List<IRewriteRule> toUse) {
    Set<State> ans = new HashSet<>();
    for (IRewriteRule r : toUse) {
      if (r.canRewrite(this.global, this.stacks.get(machineNum))) {
        ans.add(r.rewrite(this.stacks, machineNum, this.delaysTaken));
      }
    }
    return ans;
  }

  public State abstraction() {
    List<Stack<Integer>> list = new ArrayList<>();
    for (Stack<Integer> s : this.stacks) {
      Stack<Integer> top = new Stack<>();
      if (!s.isEmpty()) {
        top.push(s.peek());
      }
      list.add(top);
     
    }
    return new State(this.global, list, this.delaysTaken, this.timeStamp);
  }

  public int getDelays() {
    return this.delaysTaken;
  }

  public State cloneAndSetDelays() {
    return this.cloneAndSetDelays(this.delaysTaken + 1);
  }

  public State cloneAndSetDelays(int newDelays) {
    return new State(this.global, cloneList(this.stacks), newDelays);
  }

  public static List<Stack<Integer>> cloneList(List<Stack<Integer>> list) {
    List<Stack<Integer>> ans = new ArrayList<>();
    for (Stack<Integer> stack : list) {
      Stack<Integer> newStack = new Stack<>();
      for (int i = 0; i < stack.size(); i += 1) {
        newStack.push(stack.get(i).intValue());
      }
      ans.add(newStack);
    }
    return ans;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof State) {
      State that = (State) other;
      return this.global == that.global && this.stacks.equals(that.stacks)
          && this.delaysTaken == that.delaysTaken;
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.global, this.stacks, this.delaysTaken);
  }

  @Override
  public String toString() {
    StringBuilder ans = new StringBuilder();
    ans.append("\n<" + this.global + " | ");
    for (int i = 0; i < this.stacks.size(); i += 1) {
      ans.append(stackString(this.stacks.get(i)));
      ans.append(i == this.stacks.size() - 1 ? ">" : " , ");
    }
    return ans.toString();
  }

  private static String stackString(Stack<Integer> s) {
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
