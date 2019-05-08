package justexplore;

// A task for a program that uses rewrite rules. This is expected to terminate. 
public interface ITask {

  // Apply the first applicable rule, if any, to this Task's internal state, given
  // the current global state. Returns the new global state
  // EFFECT: modifies this
  int step(int currGlobal);

  // Has this task terminated?
  boolean isDone();
}
