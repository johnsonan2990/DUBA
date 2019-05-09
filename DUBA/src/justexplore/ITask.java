package justexplore;

// A task for a program that uses rewrite rules. This is expected to terminate. 
public interface ITask {

  // Apply the first applicable rewrite rule. Returns the global state
  // EFFECT: modifies this.
  int step();

  // Has this task terminated?
  boolean isDone();

  // Update the current global state
  void updateGlobal(int currGlobal);

  static final int stackBound = 5;
}
