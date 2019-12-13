package State;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

class State {
  final int global;
  final List<Stack<Integer>> stacks;

  State(int global, List<Stack<Integer>> stacks) {
    this.global = global;
    this.stacks = stacks;
  }

  @Override

  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof State) {
      State that = (State) other;
      return this.global == that.global && this.stacks.equals(that.stacks);
    }
    else {
      return false;
      }
    }

  @Override
  public int hashCode() {
    return Objects.hash(this.global, this.stacks);
    }

  @Override
  public String toString() {
    StringBuilder ans = new StringBuilder();
    ans.append("\n<" + this.global + " | ");
    for (int i = 0; i < this.stacks.size(); i += 1) {
      ans.append(StateWrapper.stackString(this.stacks.get(i)));
      ans.append(i == this.stacks.size() - 1 ? ">" : " , ");
      }
    return ans.toString();
    }
  }