package reachability;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Main {

  public static List<ITask> setupTasks() {
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
    IRewriteRule r5 = new PushRule(2, 5, 3, 4, 6);

    Stack<Integer> s = new Stack<>();
    s.push(2);
    ITask t1 = new Task(Arrays.asList(r1, r2));
    ITask t2 = new Task(Arrays.asList(r3, r4, r5));

    return Arrays.asList(t1, t2);
  }

  public static State setupInit(List<ITask> tasks) {
    Map<ITask, Stack<Integer>> initStacks = new HashMap<>();
    initStacks.put(tasks.get(0), new Stack<>());
    initStacks.put(tasks.get(1), new Stack<>());
    initStacks.get(tasks.get(0)).push(1);
    initStacks.get(tasks.get(1)).push(4);
    
    return new State(0, initStacks);
  }

  public static void main(String[] args) {
    List<ITask> tasks = setupTasks();
    ReachabilityExplore explorer = new ReachabilityExplore(setupInit(tasks), tasks,
        new RoundRobin());
    System.out.println(explorer.run().toString());
  }
}
