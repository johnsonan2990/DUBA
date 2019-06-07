package reachability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class Main {

  private static List<IMachine> setupTasks() {
    /*
     * Set of global states: {0, 1, 2, 3} 
     * Machine 1 states: {1, 2} 
     * Machine 1 rules:
     * (0, 1) -> (1, 2) 
     * (3, 2) -> (0, 1) 
     * Machine 2 states: {4, 5, 6}
     *  Machine 2 rules: 
     *  (0, 4) -> (0, e) 
     *  (1, 4) -> (2, 5) 
     *  (2, 5) -> (3, 4 6)
     */
    IRewriteRule r1 = new OverwriteRule(0, 1, 1, 2);
    IRewriteRule r2 = new OverwriteRule(3, 2, 0, 1);
    IRewriteRule r3 = new PopRule(0, 4, 0);
    IRewriteRule r4 = new OverwriteRule(1, 4, 2, 5);
    IRewriteRule r5 = new PushRule(2, 5, 3, 6, 4);
    

    Stack<Integer> s = new Stack<>();
    s.push(2);
    IMachine t1 = new Machine(Arrays.asList(r1, r2), 1);
    IMachine t2 = new Machine(Arrays.asList(r3, r4, r5), 4);

    return Arrays.asList(t1, t2);
  }
  
  private static List<IMachine> setupTasks2() {
    /**
     * Machine 1:
     * (0, 1) -> (1, 2)
     * (1, 2) -> (2, 3)
     * (2, 3) -> (0, 4)
     * (0, 4) -> (0, 1)
     * (0, 4) -> (1, 4 4)
     * (0, 4) -> (0, e)
     * 
     * Machine 2:
     * (0, 5) -> (1, 6 5)
     * (2, 6) -> (0, e)
     * (1, 6) -> (0, 6)
     * 
     * Machine 3:
     * (0, 7) -> (0, 8)
     * (0, 7) -> (0, 8 7)
     * (0, 8) -> (2, 7)
     * (2, 8) -> (0, e)
     * (2, 8) -> (0, 7)
     * (1, 8) -> (2, 7)
     * (1, 8) -> (2, 7)
     */
    IRewriteRule r1 = new OverwriteRule(0, 1, 1, 2);
    IRewriteRule r2 = new OverwriteRule(1, 2, 2, 3);
    IRewriteRule r3 = new OverwriteRule(2, 3, 0, 4);
    IRewriteRule r4 = new PushRule(0, 4, 1, 4, 4);
    IRewriteRule r5 = new PopRule(0, 4, 0);
    IRewriteRule r6 = new OverwriteRule(0, 4, 0, 1);

    IRewriteRule r10 = new PushRule(0, 5, 1, 6, 5);
    IRewriteRule r11 = new PopRule(2, 6, 2);
    IRewriteRule r12 = new OverwriteRule(1, 6, 0, 6);

    IRewriteRule r21 = new OverwriteRule(0, 7, 0, 8);
    IRewriteRule r22 = new PushRule(0, 7, 0, 8, 7);
    IRewriteRule r23 = new OverwriteRule(0, 8, 2, 7);
    IRewriteRule r24 = new PopRule(2, 8, 0);
    IRewriteRule r25 = new OverwriteRule(2, 8, 0, 7);
    IRewriteRule r26 = new OverwriteRule(1, 8, 2, 7);
    
    List<IMachine> ans = new ArrayList<>();
    ans.add(new Machine(Arrays.asList(r1, r2, r3, r4, r5, r6), 1));
    ans.add(new Machine(Arrays.asList(r10, r11, r12), 5));
    ans.add(new Machine(Arrays.asList(r21, r22, r23, r24, r25, r26), 7));
    return ans;
  }

  private static List<IMachine> setupTasks3() {
    /**
     * Global init: 0
        Machine 1 (Init 1):
        (0, 1) -> (1, 2)
        (3, 2) -> (0, 1)
        (2, 2) -> (4, 2)
        Machine 2 (Init 4):
        (1, 4) -> (3, 5.4)
        (1, 5) -> (3, 6.5)
        (1, 6) -> (3, 7.6)
        Machine 3 (Init 8):
        (3, 8) -> (2, 8)
        (4, 8) -> (2, 8.8)
     */

    IRewriteRule r1 = new OverwriteRule(0, 1, 1, 2);
    IRewriteRule r2 = new OverwriteRule(3, 2, 0, 1);
    IRewriteRule r3 = new OverwriteRule(2, 2, 4, 2);

    IRewriteRule r4 = new PushRule(1, 4, 3, 4, 5);
    IRewriteRule r5 = new PushRule(1, 5, 3, 5, 6);
    IRewriteRule r6 = new PushRule(1, 6, 3, 6, 7);

    IRewriteRule r7 = new OverwriteRule(3, 8, 2, 8);
    IRewriteRule r8 = new PushRule(4, 8, 2, 8, 8);

    IMachine m1 = new Machine(Arrays.asList(r1, r2, r3), 1);
    IMachine m2 = new Machine(Arrays.asList(r4, r5, r6), 4);
    IMachine m3 = new Machine(Arrays.asList(r7, r8), 8);
    return Arrays.asList(m1, m2, m3);

  }

  public static State setupInit(List<IMachine> machines) {
    List<Stack<Integer>> initStacks = new ArrayList<>();
    for (IMachine m : machines) {
      initStacks.add(m.initStack());
    }
    return new State(0, initStacks, 0);
  }

  public static void main(String[] args) {
    List<IMachine> machines = setupTasks2();
    RoundRobinExplore explorer2 = new RoundRobinExplore(machines, new RoundRobin());

    int delayBound = 20;
    Set<State> set = explorer2.runWithDelays(10, 15, setupInit(machines), delayBound);
    for (int delay = 0; delay <= delayBound; delay++) {
      System.out.println("Delay Bound " + delay);
      int delays = delay;
      System.out.println(set.stream()
          .sorted(Comparator.comparing(s -> s.timeStamp))
          .map(s -> s.abstraction()).distinct() // Add or remove to see abstractions or full states
          .filter(s -> s.getDelays() == delays)
          .collect(Collectors.toList())
          .toString());
      System.out.println("-------------------------");
    }
    ReachabilityExplore explorer = new ReachabilityExplore(setupInit(machines), machines,
        new RoundRobin());
    Set<State> setAllDelay0 = set.stream().map(s -> s.cloneAndSetDelays(0))
        .collect(Collectors.toSet());
    System.out.println(explorer.run().stream()
        .map(s -> s.cloneAndSetDelays(0))
        .distinct()
        .filter(s -> !setAllDelay0.contains(s))
        .collect(Collectors.toSet())
        .toString());
  }
}
