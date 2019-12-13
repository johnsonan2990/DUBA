package reachability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

import State.IConcreteState;
import State.IState;

/**
 * Just hard-coded for now for testing.
 * 
 * @author Andrew
 *
 */
public class AbstractorImpl implements Abstractor {

  Function<Integer, Integer> globalAbstractor;
  List<Function<Pair<Integer, Integer>, Integer>> abstractors;

  AbstractorImpl() {
    globalAbstractor = (g) -> {
      if (g == 0) {
        return 2;
      }
      else if (g == 5) {
        return 1;
      }
      else {
        return 0;
      }
    };

    Function<Pair<Integer, Integer>, Integer> local1Abs = (p) -> {
      int pred = p.getSecond().equals(3) ? 7 : 0;
      return p.getFirst() + pred;
    };
    Function<Pair<Integer, Integer>, Integer> local2Abs = (p) -> {
      int pred = p.getSecond().equals(0) ? 7 : 0;
      return p.getFirst() + pred;
    };
    this.abstractors = Arrays.asList(local1Abs, local2Abs);
  }

  @Override
  public IState apply(IConcreteState concrete) {
    return concrete.concreteToAbstract(this);
  }

  @Override
  public List<Stack<Integer>> abstractLocals(List<Stack<Pair<Integer, Integer>>> locals) {
    List<Stack<Integer>> newLocals = new ArrayList<>();
    for (int i = 0; i < locals.size(); i++) {
      Stack<Integer> newStack = new Stack<>();
      if (!locals.get(i).isEmpty()) {
        newStack.push(this.abstractors.get(i).apply(locals.get(i).peek()));
      }
      newLocals.add(newStack);
    }
    return newLocals;
  }

  @Override
  public int abstractGlobal(int oldGlobal) {
    return this.globalAbstractor.apply(oldGlobal);
  }

}
