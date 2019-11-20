package State;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class AbstractState extends State {

  private final List<String> abstraction;

  AbstractState(int global, List<Stack<Integer>> stacks) {
    super(0, null);
    this.abstraction = new ArrayList<>();
    this.abstraction.add(Integer.toString(global));
    for (Stack<Integer> s : stacks) {
      if (!s.isEmpty()) {
        this.abstraction.add(Integer.toString(s.peek()));
      }
      else {
        this.abstraction.add("e");
      }
    }
  }

  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    else if (other instanceof AbstractState) {
      AbstractState that = (AbstractState) other;
      return this.abstraction.equals(that.abstraction);
    }
    else {
      return false;
    }
  }

  public int hashCode() {
    return this.abstraction.hashCode();
  }

  public String toString() {
    StringBuilder ans = new StringBuilder();
    ans.append("< ");
    ans.append(this.abstraction.get(0));
    ans.append(" | ");
    for (int i = 1; i < this.abstraction.size() - 1; i++) {
      ans.append(this.abstraction.get(i));
      ans.append(", ");
    }
    ans.append(this.abstraction.get(this.abstraction.size() - 1));
    ans.append(" >");
    return ans.toString();
  }

}