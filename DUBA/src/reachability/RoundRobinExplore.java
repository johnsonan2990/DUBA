package reachability;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RoundRobinExplore {

  private final Set<State> reached;
  private final List<IMachine> machines;
  private final IScheduler sched;

  RoundRobinExplore(List<IMachine> machines, IScheduler sched) {
    this.reached = new HashSet<>();
    this.machines = machines;
    this.sched = sched;
  }

  public Set<State> run(int timeSlice, int rounds, State initial) {
    Set<State> init = new HashSet<>();
    init.add(initial);
    return this.run(timeSlice, rounds, init);
  }

  public Set<State> run(int timeSlice, int rounds, Set<State> initial) {
    this.reached.clear();
    this.reached.addAll(initial);
    Set<State> currFrontier = new HashSet<>();
    currFrontier.addAll(initial);
    for (int round = 0; round < rounds; round++) {
      for (int machine = 0; machine < this.machines.size(); machine++) {
        for (int steps = 0; steps < timeSlice; steps++) {
          currFrontier = this.step(machine, currFrontier);
        }
      }
    }
    Set<State> toGive = new HashSet<>();
    toGive.addAll(this.reached);
    return toGive;
  }

  public Set<State> runWithDelays(int timeSlice, int rounds, State initial, int delayBound) {
    this.reached.add(initial);
    Set<State> frontier = new HashSet<>();
    Set<State> nextMachineFrontier = new HashSet<>();
    nextMachineFrontier.add(initial);
    for (int round = 0; round < rounds; round++) {
      for (int machine = 0; machine < this.machines.size(); machine++) {
        frontier.addAll(nextMachineFrontier);
        nextMachineFrontier.clear();
        for (int step = 0; step < timeSlice; step++) {
          frontier = this.stepDelay(machine, frontier, nextMachineFrontier, delayBound);
        }
      }
    }
    return this.reached
        .stream()
        .sorted(Comparator.comparing(s -> s.timeStamp))
        .filter(s -> {
          boolean ans = true;
          for (int delays = 0; delays < s.getDelays(); delays++) {
            ans = ans && !this.reached.contains(s.cloneAndSetDelays(delays));
          }
          return ans;
        })
        .collect(Collectors.toSet());
  }

  private Set<State> step(int machineNum, Set<State> currFrontier) {
    Set<State> nextFrontier = new HashSet<>();
    for (State s : currFrontier) {
      Set<State> successors = this.machines.get(machineNum).getSuccessors(s, machineNum);
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

  private Set<State> stepDelay(int machineNum, Set<State> currFrontier,
      Set<State> nextMachineFrontier, int delayBound) {
    Set<State> nextFrontier = new HashSet<>();
    for (State s : currFrontier) {
      if (s.getDelays() < delayBound) {
        State delayed = s.cloneAndSetDelays();
        nextMachineFrontier.add(delayed);
        this.reached.add(delayed);
      }
      Set<State> successors = this.machines.get(machineNum).getSuccessors(s, machineNum);
      this.reached.addAll(successors);
      if (successors.isEmpty()) {
        nextMachineFrontier.add(s);
      }
      else {
        nextFrontier.addAll(successors);
      }
    }
    return nextFrontier;
  }
}
