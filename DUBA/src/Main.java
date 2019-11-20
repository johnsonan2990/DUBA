import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import State.IState;
import State.StateWrapper;
import reachability.IMachine;
import reachability.IMachineReader;
import reachability.Pair;
import reachability.RoundRobinExplore;

public class Main {
  private static IState setupInit(List<IMachine> machines, int global) {
    List<Stack<Integer>> initStacks = new ArrayList<>();
    for (IMachine m : machines) {
      initStacks.add(m.initStack());
    }
    return new StateWrapper(global, initStacks, 0);
  }

  public static void main(String[] args) {
    Readable r = null;
    String filepath = "";
    int slice = 5;
    int rounds = 20;
    int delay = -1;
    int stackBoundForOverApprox = 1;
    boolean cont = true;
    try {
      for (int i = 0; i < args.length; i += 2) {
        switch (args[i]) {
        case "-in":
          filepath = args[i + 1];
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
        case "-bound":
          stackBoundForOverApprox = Integer.parseInt(args[i + 1]);
          break;
        case "-automatic":
          cont = false;
          break;
        }
      }
    }
    catch (IOException e) {
      throw new IllegalArgumentException("File not found. " + e.getLocalizedMessage());
    }

    if (r == null) {
      throw new IllegalArgumentException("Please input at least \"-in\" followed by a filepath!");
    }

    System.out.println("Running " + filepath + " with slice=" + slice + ", rounds=" + rounds
        + ", delayBound=" + ((delay == -1) ? "dynamic" : delay) + ", overApproxBound= "
        + stackBoundForOverApprox);
    Pair<Integer, List<IMachine>> input = IMachineReader.read(r);
    RoundRobinExplore explorer2 = RoundRobinExplore.RRBuilder.build(input.getSecond());
    if (delay != -1) {
      List<IState> set = RoundRobinExplore.abstractCleanAndSort(explorer2.runWithDelays(slice,
          rounds, setupInit(input.getSecond(), input.getFirst()), delay));
      for (int d = 0; d <= delay; d++) {
        System.out.println("Delay Bound " + d);
        int delayss = d;
        System.out.println(
            set.stream().filter(s -> s.getDelays() == delayss).collect(Collectors.toList()));
        System.out.println("-------------------------");
      }
    }
    else {
      explorer2.runWithDelaysInteractive(slice, rounds,
          setupInit(input.getSecond(), input.getFirst()), new InputStreamReader(System.in),
          stackBoundForOverApprox, cont);
    }
  }
}
