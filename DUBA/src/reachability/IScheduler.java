package reachability;

import java.util.List;

// A scheduler to provide an ordering of tasks
public interface IScheduler {

  // Returns the next task to be run
  ITask pickTask(List<ITask> tasks);
}
