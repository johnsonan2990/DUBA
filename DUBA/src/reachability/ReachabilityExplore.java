package reachability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An explorer to find reachable states given a deterministic scheduler.
 * 
 * @author Andrew
 *
 */
public class ReachabilityExplore {
  private final Set<State> nextExplore;
  private final Set<State> reached;
  private final List<ITask> tasks;
  private final IScheduler sched;
  private final Map<ITask, Set<State>> reachedLast;

  /**
   * Create an explorer.
   * 
   * @param initial The initial state of all tasks
   * @param tasks   The list of tasks.
   * @param sched   The scheduler to use.
   */
  ReachabilityExplore(State initial, List<ITask> tasks, IScheduler sched) {
    this.nextExplore = new HashSet<>();
    this.nextExplore.add(initial);
    this.reached = new HashSet<>();
    this.reached.add(initial);
    this.tasks = tasks;
    this.sched = sched;
    this.reachedLast = new HashMap<>();
    for (ITask t : tasks) {
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
  public Set<State> run(int rounds) {
    for (int i = 0; i < rounds * this.tasks.size(); i += 1) {
      this.step();
    }
    return this.reached;
  }

  /**
   * Pick the next task and run it until no more states can be reached.
   * 
   * @return The set of reachable states
   */
  public Set<State> run() {
    while (!this.complete()) {
      this.step();
    }
    return this.reached;
  }

  /**
   * Run the given procedure to completion. 
   * Add all states found to reached.
   * Add all states found where the task has terminated to terminalReached.
   * 
   * @param toRun The task to be run.
   */
  private void runProcedure(ITask toRun) {
    Set<State> unexplored = new HashSet<>();
    unexplored.addAll(this.nextExplore);
    this.nextExplore.clear();
    int numTasks = this.tasks.size();

    while (!unexplored.isEmpty()) {
      Set<State> nextUnexplored = new HashSet<>();
      for (State s : unexplored) {
        Set<State> successors = toRun.getSuccessors(s);
        if (successors.isEmpty()) {
          this.nextExplore.add(s);
        }
        for (State successor : successors) {
          if (!reached.contains(successor)) {
            reached.add(successor);
            nextUnexplored.add(successor);
          }
          if (successor.tasksInState().size() > numTasks) {
            for (ITask t : successor.tasksInState()) {
              if (!this.tasks.contains(t)) {
                this.tasks.add(t);
              }
            }
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
    for (ITask t : this.reachedLast.keySet()) {
      ans = ans && setDiff(this.reached, this.reachedLast.get(t)).isEmpty();
    }
    return ans;
  }

  /**
   * Pick one procedure and run it.
   */
  private void step() {
    ITask next = this.sched.pickTask(this.tasks);
    this.runProcedure(next);
    this.reachedLast.get(next).addAll(this.reached);
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
