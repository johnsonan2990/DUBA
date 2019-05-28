package reachability;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoundRobinExplore {

  private final Set<State> reached;
  private final List<IMachine> tasks;
  private final IScheduler sched;

  RoundRobinExplore(List<IMachine> tasks, IScheduler sched) {
    this.reached = new HashSet<>();
    this.tasks = tasks;
    this.sched = sched;
  }

  public Set<State> run(int timeSlice, int rounds, State initial) {
    this.reached.add(initial);
    Set<State> currFrontier = new HashSet<>();
    currFrontier.add(initial);
    for (int s = 0; s < rounds * timeSlice; s++) {
      IMachine curr = this.sched.pickTask(tasks);
      for (int steps = 0; steps < timeSlice; steps++) {
        currFrontier = this.step(curr, currFrontier);
      }
    }
    return this.reached;
  }

  public Set<State> step(IMachine curr, Set<State> currFrontier) {
    Set<State> nextFrontier = new HashSet<>();
    for (State s : currFrontier) {
      Set<State> successors = curr.getSuccessors(s);
      this.reached.addAll(successors);
      if (successors.isEmpty()) {
        nextFrontier.add(s);
      }
      else {
        nextFrontier.addAll(successors);
      }
    }
    return nextFrontier;
  }
}
