package State;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import reachability.Abstractor;
import reachability.IConcreteRewriteRule;
import reachability.Pair;

public interface IConcreteState extends IState {
  /**
   * Returns the set of successor states to this state using the given rules.
   * 
   * @param toRun The current machine.
   * @param toUse The list of rules to use.
   * @return The set of states that can be reached in one step using the given
   *         rules.
   */
  public Set<IConcreteState> concreteSuccessors(int machineNum, List<IConcreteRewriteRule> toUse);

  /**
   * Return the part of this state relevant to the given machine.
   * 
   * @param machIdx the machine to get the local state for
   * @return
   */
  public Pair<Integer, Stack<Pair<Integer, Integer>>> getLocalConcreteState(int machIdx);

  public IState concreteToAbstract(Abstractor abstractor);
}
