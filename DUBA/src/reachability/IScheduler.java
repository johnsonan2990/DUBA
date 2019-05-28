package reachability;

import java.util.List;

/**
 * A scheduler that will pick the next task to use given a list of tasks
 * 
 * @author Andrew
 *
 */
public interface IScheduler {

  /**
   * Picks a task from the given list to be the next one to run.
   * 
   * @param tasks
   * @return The next task to run.
   */
  IMachine pickTask(List<IMachine> tasks);
}
