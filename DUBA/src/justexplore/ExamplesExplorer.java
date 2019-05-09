package justexplore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import tester.Tester;

public class ExamplesExplorer {
  public static void main(String [] args) {
    // List<Integer> l = new ArrayList<Integer>();
    StateExplore explorer = new StateExplore(0, setupTasks(), new RoundRobin());
    while (!explorer.checkTermination()) {
      explorer.step();
    }
    System.out.println("Done!");
    explorer.printStates();
  }

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
    Stack<Integer> t1Init = new Stack<>();
    t1Init.add(1);
    Stack<Integer> t2Init = new Stack<>();
    t2Init.add(4);

    List<IRewriteRule> t1Rules = new ArrayList<>();
    List<IRewriteRule> t2Rules = new ArrayList<>();
    Stack<Integer> stackWith1 = new Stack<>();
    stackWith1.push(1);
    Stack<Integer> stackWith2 = new Stack<>();
    stackWith2.push(2);
    Stack<Integer> stackWith4 = new Stack<>();
    stackWith4.push(4);
    Stack<Integer> stackWith5 = new Stack<>();
    stackWith5.push(5);
    Stack<Integer> stackWith46 = new Stack<>();
    stackWith46.push(6);
    stackWith46.push(4);
    
    // t1Rules.add(new PushRule(new State(0, stackWith1), Optional.of(0),
    // stackWith1));
    t1Rules.add(new OverwriteRule(new State(0, stackWith1), Optional.of(1), Optional.of(2)));
    t1Rules.add(new OverwriteRule(new State(3, stackWith2), Optional.of(0), Optional.of(1)));
    
    t2Rules.add(new PopRule(new State(0, stackWith4), Optional.of(0)));
    t2Rules.add(new OverwriteRule(new State(1, stackWith4), Optional.of(2), Optional.of(5)));
    t2Rules.add(new PushRule(new State(2, stackWith5), Optional.of(3), 4, Optional.of(6)));
    
    ITask t1 = new Task("Task 1", t1Rules, new State(0, t1Init));
    ITask t2 = new Task("Task 2", t2Rules, new State(0, t2Init));
    return Arrays.asList(t1, t2);
  }

  void testTopEqandStep(Tester t) {
    Stack<Integer> stackWith1 = new Stack<>();
    stackWith1.push(1);
    Stack<Integer> stackWith123 = new Stack<>();
    stackWith123.push(3);
    stackWith123.push(2);
    stackWith123.push(1);
    Stack<Integer> stackWith4 = new Stack<>();
    stackWith4.push(4);
    Stack<Integer> stackWith5 = new Stack<>();
    stackWith5.push(5);
    Stack<Integer> stackWith46 = new Stack<>();
    stackWith46.push(6);
    stackWith46.push(4);
    Stack<Integer> stackWith566 = new Stack<>();
    stackWith566.push(6);
    stackWith566.push(6);
    stackWith566.push(5);
    t.checkExpect(new State(0, stackWith1).topEquivalent(new State(0, stackWith123)), true);
    t.checkExpect(new State(1, stackWith4).topEquivalent(new State(1, stackWith46)), true);
    t.checkExpect(new State(1, stackWith4).topEquivalent(new State(0, stackWith46)), false);

    IRewriteRule rule1 = new OverwriteRule(new State(1, stackWith4), Optional.of(2),
        Optional.of(5));
    IRewriteRule rule2 = new PushRule(new State(2, stackWith5), Optional.of(3), 4, Optional.of(6));

    t.checkExpect(rule1.canRewrite(new State(1, stackWith46)), true);
    t.checkExpect(rule2.canRewrite(new State(2, stackWith5)), true);
    t.checkExpect(new State(2, stackWith566).topEquivalent(new State(2, stackWith5)), true);
    t.checkExpect(rule2.canRewrite(new State(2, stackWith566)), true);

    ITask task1 = new Task("Test", Arrays.asList(rule1, rule2), new State(1, stackWith46));
    t.checkExpect(task1.isDone(), false);
    t.checkExpect(task1.toString(), "Test: < 1 | [6, 4] >");
    task1.step();
    t.checkExpect(task1.toString(), "Test: < 2 | [6, 5] >");
    t.checkExpect(task1.isDone(), false);
    System.out.println(task1.toString());
    task1.step();
    t.checkExpect(task1.toString(), "Test: < 3 | [6, 6, 4] >");
  }
}
