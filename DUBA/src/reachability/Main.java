package reachability;

import java.io.FileReader;
import java.io.IOException;
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
    IRewriteRule r1 = OverwriteRule.makeOverwrite(0, 1, 1, 2);
    IRewriteRule r2 = OverwriteRule.makeOverwrite(3, 2, 0, 1);
    IRewriteRule r3 = PopRule.makePop(0, 4, 0);
    IRewriteRule r4 = OverwriteRule.makeOverwrite(1, 4, 2, 5);
    IRewriteRule r5 = PushRule.makePush(2, 5, 3, 4, 6);
    

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
    IRewriteRule r1 = OverwriteRule.makeOverwrite(0, 1, 1, 2);
    IRewriteRule r2 = OverwriteRule.makeOverwrite(1, 2, 2, 3);
    IRewriteRule r3 = OverwriteRule.makeOverwrite(2, 3, 0, 4);
    IRewriteRule r4 = PushRule.makePush(0, 4, 1, 4, 4);
    IRewriteRule r5 = PopRule.makePop(0, 4, 0);
    IRewriteRule r6 = OverwriteRule.makeOverwrite(0, 4, 0, 1);

    IRewriteRule r10 = PushRule.makePush(0, 5, 1, 5, 6);
    IRewriteRule r11 = PopRule.makePop(2, 6, 2);
    IRewriteRule r12 = OverwriteRule.makeOverwrite(1, 6, 0, 6);

    IRewriteRule r21 = OverwriteRule.makeOverwrite(0, 7, 0, 8);
    IRewriteRule r22 = PushRule.makePush(0, 7, 0, 7, 8);
    IRewriteRule r23 = OverwriteRule.makeOverwrite(0, 8, 2, 7);
    IRewriteRule r24 = PopRule.makePop(2, 8, 0);
    IRewriteRule r25 = OverwriteRule.makeOverwrite(2, 8, 0, 7);
    IRewriteRule r26 = OverwriteRule.makeOverwrite(1, 8, 2, 7);
    
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

    IRewriteRule r1 = OverwriteRule.makeOverwrite(0, 1, 1, 2);
    IRewriteRule r2 = OverwriteRule.makeOverwrite(3, 2, 0, 1);
    IRewriteRule r3 = OverwriteRule.makeOverwrite(2, 2, 4, 2);

    IRewriteRule r4 = PushRule.makePush(1, 4, 3, 5, 4);
    IRewriteRule r5 = PushRule.makePush(1, 5, 3, 6, 5);
    IRewriteRule r6 = PushRule.makePush(1, 6, 3, 7, 6);

    IRewriteRule r7 = OverwriteRule.makeOverwrite(3, 8, 2, 8);
    IRewriteRule r8 = PushRule.makePush(4, 8, 2, 8, 8);

    IMachine m1 = new Machine(Arrays.asList(r1, r2, r3), 1);
    IMachine m2 = new Machine(Arrays.asList(r4, r5, r6), 4);
    IMachine m3 = new Machine(Arrays.asList(r7, r8), 8);
    return Arrays.asList(m1, m2, m3);

  }

  private static List<IMachine> setupTasks4() {
    IRewriteRule r1 = OverwriteRule.makeOverwrite(0, 1, 1, 2);
    IRewriteRule r2 = OverwriteRule.makeOverwrite(3, 2, 0, 1);
    IRewriteRule r3 = OverwriteRule.makeOverwrite(4, 2, 2, 2);

    IRewriteRule r4 = PushRule.makePush(1, 4, 6, 4, 6);
    IRewriteRule r5 = PushRule.makePush(6, 4, 3, 4, 4);
    IRewriteRule r6 = PopRule.makePop(4, 4, 3);

    IRewriteRule r7 = OverwriteRule.makeOverwrite(3, 8, 4, 8);
    IRewriteRule r8 = PushRule.makePush(2, 8, 4, 8, 8);

    IMachine m1 = new Machine(Arrays.asList(r1, r2, r3), 1);
    IMachine m2 = new Machine(Arrays.asList(r4, r5, r6), 4);
    IMachine m3 = new Machine(Arrays.asList(r7, r8), 8);

    return Arrays.asList(m1, m2, m3);
  }

  public static State setupInit(List<IMachine> machines, int global) {
    List<Stack<Integer>> initStacks = new ArrayList<>();
    for (IMachine m : machines) {
      initStacks.add(m.initStack());
    }
    return new State(global, initStacks, 0);
  }

  public static void main(String[] args) {
    Readable r = null;
    int slice = 5;
    int rounds = 20;
    int delay = 0;
    try {
      for (int i = 0; i < args.length; i += 2) {
        switch (args[i]) {
        case "-in":
          r = new FileReader(args[i + 1]);
          break;
        case "-slice":
          slice = Integer.parseInt(args[i + 1]);
          break;
        case "-rounds":
          rounds = Integer.parseInt(args[i + 1]);
          break;
        case "-delay":
          delay = Integer.parseInt(args[i + 1]);
          break;
        }
      }
    }
    catch (IOException e) {
      throw new IllegalArgumentException("File not found.");
    }

    System.out.println(slice + "," + rounds + "," + delay);
    Pair<Integer, List<IMachine>> input = IMachineReader.read(r);
    System.out.println(input.getSecond().size());
    RoundRobinExplore explorer2 = new RoundRobinExplore(input.getSecond(), new RoundRobin());
    Set<State> set = explorer2.runWithDelays(slice, rounds,
        setupInit(input.getSecond(), input.getFirst()), delay);
    Set<State> abstractedSetNotCleaned = set.stream().map(s -> s.abstraction())
        .collect(Collectors.toSet());
    Set<State> abstractedSetCleaned = abstractedSetNotCleaned.stream().filter(s -> {
      boolean ans = true;
      for (int delays = 0; delays < s.getDelays(); delays++) {
        ans = ans && !abstractedSetNotCleaned.contains(s.cloneAndSetDelays(delays));
      }
      return ans;
    }).collect(Collectors.toSet());
    for (int d = 0; d <= delay; d++) {
      System.out.println("Delay Bound " + d);
      int delayss = d;
      System.out
          .println(abstractedSetCleaned.stream().sorted(Comparator.comparing(s -> s.timeStamp))
              .filter(s -> s.getDelays() == delayss).collect(Collectors.toList()));
      System.out.println("-------------------------");
    }
//    ReachabilityExplore explorer = new ReachabilityExplore(setupInit(machines, 0), machines);
//    Set<State> check = abstractedSetCleaned.stream().map(s -> s.cloneAndSetDelays(0))
//        .collect(Collectors.toSet());
//    System.out.println(explorer.run().stream().map(s -> s.abstraction().cloneAndSetDelays(0))
//        .distinct().collect(Collectors.toSet()).equals(check));
  }
}
