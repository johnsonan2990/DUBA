package reachability;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Function;

import State.ConcreteState;
import State.IConcreteState;
import reachability.OverwriteRuleLambda.OPERATION;

public class ConcretePopRule extends AConcreteRewriteRule {

  ConcretePopRule(TriFunction<Integer, Integer, Integer, Boolean> applicable,
      Function<Integer, Integer> globalFunc, Function<Integer, Integer> localFunc,
      Optional<Integer> localTo) {
    super(applicable, globalFunc, localFunc, localTo);
    // TODO Auto-generated constructor stub
  }

  /**
   * parameters for a lambda rule.
   * 
   * @param globalOpn which operation to use for the global updater.
   * @param localOp   which operation to use for the local updater
   * @param args      these should be: [GLOBALFROM, TOPFROM, GLOBALMODIFIER,
   *                  TOPMODIFIER]
   * @return a new rule
   */
  static IConcreteRewriteRule makeConcretePopRule(boolean globalNot, OPERATION globalOp,
      boolean localNot, Optional<Integer> globalFrom, Optional<Integer> topFrom, int localFrom,
      int globalModifier) {
//    if (args.length == 4) {
//      return new OverwriteRuleLambda(args[0], args[1], args[2], args[3]);
//    }

    Function<Integer, Boolean> globalChecker;
    if (globalFrom.isPresent()) {
      if (globalNot) {
        globalChecker = (x) -> x != globalFrom.get();
      }
      else {
        globalChecker = (x) -> x == globalFrom.get();
      }
    }
    else {
      globalChecker = (x) -> true;
    }

    Function<Integer, Boolean> localChecker;
    if (topFrom.isPresent()) {
      if (localNot) {
        localChecker = (x) -> x != topFrom.get();
      }
      else {
        localChecker = (x) -> x == topFrom.get();
      }
    }
    else {
      localChecker = (x) -> true;
    }
    TriFunction<Integer, Integer, Integer, Boolean> applicable = (g, l, v) -> globalChecker.apply(g)
        && l == localFrom && localChecker.apply(l);

    Function<Integer, Integer> globalFunc = funcMachine(globalOp, globalModifier);
//    else {
//      throw new IllegalArgumentException(
//          "Input must be all integers, and the array must be of length 4");
//    }
    return new ConcretePopRule(applicable, globalFunc, null, Optional.empty());
  }

  @Override
  public IConcreteState rewrite(int global, List<Stack<Pair<Integer, Integer>>> stacks,
      int machineNum, int delays) {
    List<Stack<Pair<Integer, Integer>>> nextList = cloneList(stacks);
    Stack<Pair<Integer, Integer>> toRewrite = nextList.get(machineNum);
    return new ConcreteState(this.globalFunc.apply(global), nextList);
  }

}
