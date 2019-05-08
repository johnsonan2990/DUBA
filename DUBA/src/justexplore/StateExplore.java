package justexplore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class StateExplore {

  private int global;
  private final List<ITask> tasks = new ArrayList<>();
  private final IScheduler sched;
  private Optional<ITask> currTask;

  StateExplore(int global, List<Task> tasks, IScheduler sched) {
    this.global = global;
    this.tasks.clear();
    this.tasks.addAll(tasks);
    this.sched = sched;
  }

  public static void main(String[] args) {

  }

  public void step() {
    if (this.currTask.isPresent()) {
      this.global = currTask.get().step(this.global);
      if (this.currTask.get().isDone()) {
        this.currTask = Optional.empty();
      }
    }
    else {
      this.currTask = Optional.of(this.sched.pickTask(this.tasks));
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
