package reachability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Main {

  public static List<IMachine> setupTasks() {
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
  
  public static List<IMachine> setupTasks2() {
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

  public static State setupInit(List<IMachine> machines) {
    Map<IMachine, Stack<Integer>> initStacks = new HashMap<>();
    for (IMachine m : machines) {
      initStacks.put(m, m.initStack());
    }
    return new State(0, initStacks);
  }

  public static void main(String[] args) {
    List<IMachine> machines = setupTasks2();
    RoundRobinExplore explorer = new RoundRobinExplore(machines, new RoundRobin());
    System.out.println(explorer.run(1, 20, setupInit(machines)).toString());
  }
}
