package reachability;

import java.util.List;

public class RoundRobin implements IScheduler {

  private int counter = 0;

  @Override
  public ITask pickTask(List<ITask> tasks) {
    if (tasks.isEmpty()) {
      throw new IllegalArgumentException("Cannot pick a task from an empty list");
    }
    ITask ans = tasks.get(counter);
    counter = (counter + 1) % tasks.size();
    return ans;
  }

}
