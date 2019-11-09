package reachability;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class RoundRobinExplore {

  private final Set<State> reached;
  private final List<IMachine> machines;
  private final IScheduler sched;

  private RoundRobinExplore(List<IMachine> machines, IScheduler sched) {
    this.reached = new HashSet<>();
    this.machines = machines;
    this.sched = sched;
  }

  public static class RRBuilder {
    public static RoundRobinExplore build(List<IMachine> machines, IScheduler sched) {
      return new RoundRobinExplore(machines, sched);
    }

    public static RoundRobinExplore build(List<IMachine> machines) {
      return build(machines, new RoundRobin());
    }
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
    Set<State> nextMachFrontier = new HashSet<>();
    for (int round = 0; round < rounds; round++) {
      for (int machine = 0; machine < this.machines.size(); machine++) {
        currFrontier.clear();
        currFrontier.addAll(nextMachFrontier);
        nextMachFrontier.clear();
        for (int steps = 0; steps < timeSlice; steps++) {
          currFrontier = this.step(machine, currFrontier);
          nextMachFrontier.addAll(currFrontier);
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
          if (frontier.isEmpty()) {
            break;
          }
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

  public void runWithDelaysInteractive(int timeSlice, int rounds, State initial, Readable r,
      int stackBoundForOverApprox, boolean cont) {
    Set<State> z = this.overapproxReachable(initial, stackBoundForOverApprox).stream()
        .map(s -> s.abstraction().cloneAndSetDelays(0)).collect(Collectors.toSet());
    // System.out.println("Appprox set Z:" + z);
    Scanner in = new Scanner(r).useDelimiter("");
    Set<State> gIntersectTR = this.intersectGenerator(z);
    System.out.println("Found G intersect Z:");
    if (gIntersectTR.size() < 20) {
      System.out.println(gIntersectTR);
    }
    else if (cont) {
      System.out.println("Print it? (y to print)");
      while (in.hasNextLine()) {
        String next = in.nextLine();
        if (next.equals("y") || next.equals("Y")) {
          System.out.println(gIntersectTR);
          break;
        }
        else {
          break;
        }
      }
    }
    else {
      System.out.println("Too big to print.");
    }
    int delay = -1;
    List<State> reachedThisBound;
    int plateauLength = 0;
    int delayB = 4;
    Set<State> known = this.runWithDelays(timeSlice, rounds, initial, delayB);
    while (true) {
      delay++;
      int d = delay;
      System.out.println("New abstract states with delay " + delay + ":");
      if (delay > delayB) {
        delayB += this.machines.size();
        known = this.runWithDelays(timeSlice, rounds, initial, delayB);
      }
      reachedThisBound = abstractCleanAndSort(known).stream().filter(s -> s.getDelays() == d)
          .collect(Collectors.toList());

      if (reachedThisBound.size() < 20) {
        System.out.println(": " + reachedThisBound);
      }
      else if (cont) {
        System.out.println("Print new states? (y to print)");
        while (in.hasNextLine()) {
          String next = in.nextLine();
          if (next.equals("y") || next.equals("Y")) {
            System.out.println(reachedThisBound);
            break;
          }
          else {
            break;
          }
        }
      }
      else {
        System.out.println("Too big to print");
      }

      if (reachedThisBound.isEmpty()) {
        plateauLength++;
        if (plateauLength == this.machines.size() && plateauLength > 0) {
          System.out.println(
              "Plateau has reached length of " + plateauLength + ". Testing convergence...");
          Set<State> thisAll0Delay = known.stream().filter(s -> s.getDelays() <= d)
              .map(s -> s.abstraction().cloneAndSetDelays(0)).collect(Collectors.toSet());
          Set<State> missed = ReachabilityExplore.setDiff(gIntersectTR, thisAll0Delay);
          if (missed.isEmpty()) {
            System.out.println("Found all reachable states!");
            in.close();
            return;
          }
          else {
            System.out.println("Not quite there! Missed " + missed.size() + " generators");
            if (cont) {
              if (missed.size() < 20) {
                System.out.println(": " + missed);
              }
              else {
                System.out.println("Print them? (y to print)");
                while (in.hasNextLine()) {
                  String next = in.nextLine();
                  if (next.equals("y") || next.equals("Y")) {
                    System.out.println(missed);
                    break;
                  }
                  else {
                    break;
                  }
                }
              }
            }
          }
        }
      }
      else {
        plateauLength = 0;
      }
      if (cont) {
        System.out.println("Continue? ('n' to stop)");
        while (in.hasNextLine()) {
          String next = in.nextLine();
          if (next.equals("n") || next.equals("N")) {
            System.out.println("Quitting.");
            in.close();
            return;
          }
          else {
            break;
          }
        }
      }
    }
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

  private Set<State> overapproxReachable(State initial, int bound) {
    List<IMachine> simplerMachines = this.machines.stream().map(m -> m.simplify(bound))
        .collect(Collectors.toList());
    return new ReachabilityExplore(initial, simplerMachines).run().stream()
        .map(s -> s.abstraction()).collect(Collectors.toSet());
  }

  private Set<State> intersectGenerator(Set<State> reachable) {
    Set<State> ans = new HashSet<>();
    for (State s : reachable) {
      boolean check = false;
      for (int mach = 0; mach < this.machines.size(); mach++) {
        check = check || this.machines.get(mach).isGenerator(s, mach);
      }
      if (check) {
        ans.add(s);
      }
    }
    return ans;
  }

  public static List<State> abstractCleanAndSort(Set<State> full) {
    Set<State> abstractedSetNotCleaned = full.stream().map(s -> s.abstraction())
        .collect(Collectors.toSet());
    return abstractedSetNotCleaned.stream().filter(s -> {
      boolean ans = true;
      for (int delays = 0; delays < s.getDelays(); delays++) {
        ans = ans && !abstractedSetNotCleaned.contains(s.cloneAndSetDelays(delays));
      }
      return ans;
    }).sorted(Comparator.comparing(s -> s.timeStamp)).collect(Collectors.toList());
  }
}
