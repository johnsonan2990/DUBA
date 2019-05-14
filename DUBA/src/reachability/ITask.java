package reachability;

import java.util.Set;

public interface ITask {
  // Returns all of the possible successors to the given state using this task's
  // rewrite rules
  Set<State> getSuccessors(State s);


}
