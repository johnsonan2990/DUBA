package reachability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ReachabilityExplore {
  private Set<State> reached;
  private final List<ITask> tasks;
  private final IScheduler sched;
  private final Map<ITask, Set<State>> reachedLast;

  ReachabilityExplore(State initial, List<ITask> tasks, IScheduler sched) {
    this.reached = new HashSet<State>();
    this.reached.add(initial);
    this.tasks = tasks;
    this.sched = sched;
    this.reachedLast = new HashMap<>();
    for (ITask t : tasks) {
      this.reachedLast.put(t, new HashSet<>());
    }
  }


  // Pick the next task and run it until no more states can be reached. Then,
  // update the reached set.
  public Set<State> run(int rounds) {
    for (int i = 0; i < rounds * this.tasks.size(); i += 1) {
      this.step();
    }
    return this.reached;
  }

  private void step() {
    ITask next = this.sched.pickTask(this.tasks);
    this.runProcedure(next);
    this.reachedLast.get(next).addAll(this.reached);
  }

  public static <T> Set<T> setDiff(Set<T> set1, Set<T> set2) {
    Set<T> ans = new HashSet<>();
    ans.addAll(set1);
    ans.removeAll(set2);
    return ans;
  }
  
  // Return the updated set of reached states after fully exploring reachable
  // states from the given task and unexplored states.
  private void runProcedure(ITask toRun) {
    Set<State> unexplored = setDiff(this.reached, this.reachedLast.get(toRun));

    while (!unexplored.isEmpty()) {
      Set<State> nextUnexplored = new HashSet<>();
      for (State s : unexplored) {
        Set<State> successors = toRun.getSuccessors(s);
        for (State successor : successors) {
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
}
