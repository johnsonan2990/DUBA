package reachability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import State.IState;

/**
 * An explorer to find reachable states given a deterministic scheduler.
 * 
 * @author Andrew
 *
 */
public class ReachabilityExplore {
  // private final Set<State> nextExplore;
  private final Set<IState> reached;
  private final List<IMachine> machines;
  private final Map<IMachine, Set<IState>> reachedLast;

  /**
   * Create an explorer.
   * 
   * @param initial  The initial state of all machines
   * @param machines The list of machines.
   * @param sched    The scheduler to use.
   */
  ReachabilityExplore(IState initial, List<IMachine> machines) {
//    this.nextExplore = new HashSet<>();
//    this.nextExplore.add(initial);
    this.reached = new HashSet<>();
    this.reached.add(initial);
    this.machines = machines;
    this.reachedLast = new HashMap<>();
    for (IMachine t : machines) {
      this.reachedLast.put(t, new HashSet<>());
    }
  }

  /**
   * Run this state explorer for the given number of rounds. Intended for use with
   * a fair scheduler.
   * 
   * @param rounds The number of rounds
   * @return The set of states reachable in that many rounds
   */
  public Set<IState> run(int rounds) {
    for (int round = 0; round < rounds; round += 1) {
      for (int machine = 0; machine < this.machines.size(); machine += 1) {
        this.runProcedure(machine);
        this.reachedLast.get(this.machines.get(machine)).addAll(this.reached);
      }
    }
    return this.reached;
  }

  /**
   * Pick the next machine and run it until no more states can be reached.
   * 
   * @return The set of reachable states
   */
  public Set<IState> run() {
    while (!this.complete()) {
      for (int machine = 0; machine < this.machines.size(); machine += 1) {
        this.runProcedure(machine);
        this.reachedLast.get(this.machines.get(machine)).addAll(this.reached);
      }
    }
    return this.reached;
  }

  /**
   * Run the given procedure to completion. Add all states found to reached. Add
   * all states found where the machine has terminated to terminalReached.
   * 
   * @param toRun The machine to be run.
   */
  private void runProcedure(int machineNum) {
    Set<IState> unexplored = setDiff(this.reached,
        this.reachedLast.get(this.machines.get(machineNum)));

    while (!unexplored.isEmpty()) {
      Set<IState> nextUnexplored = new HashSet<>();
      for (IState s : unexplored) {
        Set<IState> successors = this.machines.get(machineNum).getSuccessors(s, machineNum);
        for (IState successor : successors) {
          if (!reached.contains(successor)) {
            reached.add(successor);
            nextUnexplored.add(successor);
          }
        }
      }
      unexplored.clear();
      unexplored.addAll(nextUnexplored);
    }
  }

  /**
   *  Has this explorer found all the possible states?
   * @return Whether or not the exploration has concluded.
   */
  public boolean complete() {
    boolean ans = true;
    for (IMachine t : this.reachedLast.keySet()) {
      ans = ans && setDiff(this.reached, this.reachedLast.get(t)).isEmpty();
    }
    return ans;
  }

  /**
   * Return a new set with all of the elements in set 1 but not in set 2
   * 
   * @param set1 The first set
   * @param set2 The set to subtract
   * @return The set difference set1 / set2
   */
  public static <T> Set<T> setDiff(Set<T> set1, Set<T> set2) {
    Set<T> ans = new HashSet<>();
    ans.addAll(set1);
    ans.removeAll(set2);
    return ans;
  }
}
