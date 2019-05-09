package justexplore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class StateExplore {

  private int global;
  private final List<ITask> tasks = new ArrayList<>();
  private final List<Boolean> whoIsDone = new ArrayList<>();
  private final IScheduler sched;
  private ITask currTask;
  private List<Set<String>> statesReached;

  StateExplore(int global, List<ITask> tasks, IScheduler sched) {
    this.global = global;
    this.tasks.addAll(tasks);
    this.setAllToFalse();
    this.sched = sched;
    this.currTask = this.sched.pickTask(this.tasks);
    System.out.println("Selected new task " + this.currTask.toString());
    this.statesReached = new ArrayList<>();
    for (ITask t : tasks) {
      this.statesReached.add(new HashSet<>());
    }
  }

  // Advance the selected task by one step and then pick a new one if it is done
  public void step() {
    for (ITask t : this.tasks) {
      t.updateGlobal(this.global);
    }
    this.updateReached();
    if (!this.currTask.isDone()) {
      this.global = currTask.step();
      System.out.println("Next state: " + this.currTask.toString());
    }
    else {
      System.out.println(this.currTask.toString() + " has completed.");
      this.currTask = this.sched.pickTask(this.tasks);
      System.out.println("Selected new task " + this.currTask.toString());
    }
  }

  // Deal with updating states reached, and update booleans
  private void updateReached() {
    int currIndex = this.tasks.indexOf(this.currTask);
    if (!this.statesReached.get(currIndex).add(this.currTask.toString())) {
      this.whoIsDone.set(currIndex, true);
    }
    else {
      this.setAllToFalse();
    }
  }

  // Set all of the
  private void setAllToFalse() {
    this.whoIsDone.clear();
    for (ITask t : this.tasks) {
      this.whoIsDone.add(false);
    }
  }

  // Have all of this explorer's tasks run once and not changed the reachable
  // states?
  public boolean checkTermination() {
    return this.whoIsDone.stream().allMatch(b -> b);
  }

  public void printStates() {
    System.out.println("The states that were found, for each task, are: ");
    for (Set<String> set : this.statesReached) {
      for (String s : set) {
        System.out.println(s);
      }
    }
  }

}

//// Traverses a set of Tasks and records their states
//public class StateExplore extends World {
//
//  private int global;
//  private final List<ITask> tasks = new ArrayList<>();
//  private final IScheduler sched;
//  private Optional<ITask> currTask;
//
//  StateExplore(int global, List<Task> tasks, IScheduler sched) {
//    this.global = global;
//    this.tasks.clear();
//    this.tasks.addAll(tasks);
//    this.sched = sched;
//  }
//
//  // Advances
//  public void onTick() {
//    if (this.currTask.isPresent()) {
//      this.global = currTask.get().step(this.global);
//      if (this.currTask.get().isDone()) {
//        this.currTask = Optional.empty();
//      }
//    }
//    else {
//      this.currTask = Optional.of(this.sched.pickTask(this.tasks));
//    }
//
//  }
//
//  public void onKeyEvent(String event) {
//    // TODO: implement buttons
//  }
//
//  // Draws the state of the world
//  public WorldScene makeScene() {
//    return new WorldScene(10, 10);
//  }
//}
