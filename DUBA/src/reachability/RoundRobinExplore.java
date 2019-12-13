package reachability;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import State.IState;

public class RoundRobinExplore {

  private final Set<IState> reached;
  private final List<IMachine> machines1;
  private final List<IMachine> machines2;
  private final IScheduler sched;
  private final Abstractor abstractor;

  private RoundRobinExplore(List<IMachine> machines, IScheduler sched) {
    this.reached = new HashSet<>();
    this.machines1 = machines;
    this.machines2 = null;
    this.sched = sched;
    this.abstractor = null;
  }
  
  private RoundRobinExplore(List<IMachine> machines1, List<IMachine> machines2, IScheduler sched,
      Abstractor abstractor) {
    this.reached = new HashSet<>();
    this.machines1 = machines1;
    this.machines2 = machines2;
    this.sched = sched;
    this.abstractor = abstractor;
  }

  /**
   * use to build RRExplorers. If passing in 2 sets of machines, pass in the
   * concrete first!
   * 
   * @author Andrew
   *
   */
  public static class RRBuilder {
    public static RoundRobinExplore build(List<IMachine> machines, IScheduler sched) {
      return new RoundRobinExplore(machines, sched);
    }

    public static RoundRobinExplore build(List<IMachine> machines) {
      return build(machines, new RoundRobin());
    }

    public static RoundRobinExplore build(List<IMachine> machines1, List<IMachine> machines2,
        Abstractor abstractor) {
      return new RoundRobinExplore(machines1, machines2, new RoundRobin(), abstractor);
    }
  }

  public Set<IState> run(int timeSlice, int rounds, IState initial) {
    Set<IState> init = new HashSet<>();
    init.add(initial);
    return this.run(timeSlice, rounds, init);
  }

  public Set<IState> run(int timeSlice, int rounds, Set<IState> initial) {
    this.reached.clear();
    this.reached.addAll(initial);
    Set<IState> currFrontier = new HashSet<>();
    currFrontier.addAll(initial);
    Set<IState> nextMachFrontier = new HashSet<>();
    for (int round = 0; round < rounds; round++) {
      for (int machine = 0; machine < this.machines1.size(); machine++) {
        currFrontier.clear();
        currFrontier.addAll(nextMachFrontier);
        nextMachFrontier.clear();
        for (int steps = 0; steps < timeSlice; steps++) {
          currFrontier = this.step(machine, currFrontier);
          nextMachFrontier.addAll(currFrontier);
        }
      }
    }
    Set<IState> toGive = new HashSet<>();
    toGive.addAll(this.reached);
    return toGive;
  }

  public Set<IState> runWithDelays(int timeSlice, int rounds, IState initial, int delayBound) {
    this.reached.add(initial);
    Set<IState> frontier = new HashSet<>();
    Set<IState> nextMachineFrontier = new HashSet<>();
    nextMachineFrontier.add(initial);
    for (int round = 0; round < rounds; round++) {
      for (int machine = 0; machine < this.machines1.size(); machine++) {
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
        .sorted(Comparator.comparing(s -> s.getTimestamp()))
        .filter(s -> {
          boolean ans = true;
          for (int delays = 0; delays < s.getDelays(); delays++) {
            ans = ans && !this.reached.contains(s.cloneAndSetDelays(delays));
          }
          return ans;
        })
        .collect(Collectors.toSet());
  }

  public void runWithDelaysInteractive(int timeSlice, int rounds, IState initial, Readable r,
      int stackBoundForOverApprox, boolean cont) {
    Set<IState> z = this.overapproxReachable(initial, stackBoundForOverApprox).stream()
        .map(s -> s.abstraction().cloneAndSetDelays(0)).collect(Collectors.toSet());
    // System.out.println("Appprox set Z:" + z);
    Scanner in = new Scanner(r).useDelimiter("");
    Set<IState> gIntersectTR = this.intersectGenerator(z);
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
    List<IState> reachedThisBound;
    int plateauLength = 0;
    int delayB = 4;
    Set<IState> known = this.runWithDelays(timeSlice, rounds, initial, delayB);
    while (true) {
      delay++;
      int d = delay;
      System.out.println("New abstract states with delay " + delay + ":");
      if (delay > delayB) {
        delayB += (this.machines1.size() - plateauLength);
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
        if (plateauLength == this.machines1.size() && plateauLength > 0) {
          System.out.println(
              "Plateau has reached length of " + plateauLength + ". Testing convergence...");
          Set<IState> thisAll0Delay = known.stream().filter(s -> s.getDelays() <= d)
              .map(s -> s.abstraction().cloneAndSetDelays(0)).collect(Collectors.toSet());
          Set<IState> missed = ReachabilityExplore.setDiff(gIntersectTR, thisAll0Delay);
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

  private Set<IState> step(int machineNum, Set<IState> currFrontier) {
    Set<IState> nextFrontier = new HashSet<>();
    for (IState s : currFrontier) {
      Set<IState> successors = this.machines1.get(machineNum).getSuccessors(s, machineNum);
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

  private Set<IState> stepDelay(int machineNum, Set<IState> currFrontier,
      Set<IState> nextMachineFrontier, int delayBound) {
    Set<IState> nextFrontier = new HashSet<>();
    for (IState s : currFrontier) {
      if (s.getDelays() < delayBound) {
        IState delayed = s.cloneAndSetDelays();
        nextMachineFrontier.add(delayed);
      }
      Set<IState> successors = this.machines1.get(machineNum).getSuccessors(s, machineNum);
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

  private Set<IState> overapproxReachable(IState initial, int bound) {
    List<IMachine> toUse = this.machines2 == null ? this.machines1 : this.machines2;
    List<IMachine> simplerMachines = toUse.stream().map(m -> m.simplify(bound))
        .collect(Collectors.toList());
    return new ReachabilityExplore(initial, simplerMachines).run().stream()
        .map(s -> s.abstraction()).collect(Collectors.toSet());
  }

  private Set<IState> intersectGenerator(Set<IState> reachable) {
    List<IMachine> toUse = this.machines2 == null ? this.machines1 : this.machines2;
    Set<IState> ans = new HashSet<>();
    for (IState s : reachable) {
      boolean check = false;
      for (int mach = 0; mach < toUse.size(); mach++) {
        check = check || toUse.get(mach).isGenerator(s, mach);
      }
      if (check) {
        ans.add(s);
      }
    }
    return ans;
  }

  public static List<IState> abstractCleanAndSort(Set<IState> full) {
    Set<IState> abstractedSetNotCleaned = full.stream().map(s -> s.abstraction())
        .collect(Collectors.toSet());
    return abstractedSetNotCleaned.stream().filter(s -> {
      boolean ans = true;
      for (int delays = 0; delays < s.getDelays(); delays++) {
        ans = ans && !abstractedSetNotCleaned.contains(s.cloneAndSetDelays(delays));
      }
      return ans;
    }).sorted(Comparator.comparing(s -> s.getTimestamp())).collect(Collectors.toList());
  }
}
