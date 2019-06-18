

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import reachability.IMachine;
import reachability.IMachineReader;
import reachability.Pair;
import reachability.RoundRobinExplore;
import reachability.State;

public class Main {
  private static State setupInit(List<IMachine> machines, int global) {
    List<Stack<Integer>> initStacks = new ArrayList<>();
    for (IMachine m : machines) {
      initStacks.add(m.initStack());
    }
    return new State(global, initStacks, 0);
  }

  public static void main(String[] args) {
    Readable r = null;
    String filepath = "";
    int slice = 5;
    int rounds = 20;
    int delay = -1;
    try {
      for (int i = 0; i < args.length; i += 2) {
        switch (args[i]) {
        case "-in":
          filepath = args[i+1];
          r = new FileReader(filepath);          
          break;
        case "-slice":
          slice = Integer.parseInt(args[i + 1]);
          break;
        case "-rounds":
          rounds = Integer.parseInt(args[i + 1]);
          break;
        case "-delay":
          delay = Integer.parseInt(args[i + 1]);
          break;
        }
      }
    }
    catch (IOException e) {
      throw new IllegalArgumentException("File not found.");
    }

    System.out.println("Running " + filepath + " with slice=" + slice + ", rounds=" + rounds
        + ", and delayBound=" + ((delay == -1) ? "dynamic" : delay));
    Pair<Integer, List<IMachine>> input = IMachineReader.read(r);
    RoundRobinExplore explorer2 = RoundRobinExplore.RRBuilder.build(input.getSecond());
    if (delay != -1) {
      List<State> set = RoundRobinExplore.abstractCleanAndSort(explorer2.runWithDelays(slice,
          rounds, setupInit(input.getSecond(), input.getFirst()), delay));
      for (int d = 0; d <= delay; d++) {
        System.out.println("Delay Bound " + d);
        int delayss = d;
        System.out
            .println(set.stream()
                .filter(s -> s.getDelays() == delayss).collect(Collectors.toList()));
        System.out.println("-------------------------");
      }
    }
    else {
      explorer2.runWithDelaysInteractive(slice, rounds,
          setupInit(input.getSecond(), input.getFirst()));
    }
  }
}
